package com.xjtu.core;

import com.xjtu.common.BrowerChromeDriver;
import com.xjtu.model.BaikeEntity;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.xjtu.common.Config.SOUGOU_URL;


@Slf4j
public class SeleniumSpider implements Spider<BaikeEntity> {
    private final ChromeDriver driver = BrowerChromeDriver.ChromeDriverClass.driver;
    private volatile Map<String,String> handleMap = new ConcurrentHashMap<>();
    private final Lock lockPage = new ReentrantLock();
    @Override
    public BaikeEntity get(String entityName) {
        String htmlText;
        // 打开新窗口，并将driver句柄切换到新窗口
        lockPage.lock();
        try {
            driver.executeScript("window.open()");
            Set<String> handleSet = driver.getWindowHandles();
            List<String> tmpHandleList = new ArrayList<>(handleSet);
            String curHandle = tmpHandleList.get(tmpHandleList.size() - 1);
            driver.switchTo().window(curHandle);
            driver.get(SOUGOU_URL);
            handleMap.put(entityName,curHandle);
            driver.switchTo().window(handleMap.get(entityName));
            driver.findElement(By.id("searchText")).sendKeys(entityName);
            driver.findElement(By.id("enterLemma")).click();
        }finally {
            lockPage.unlock();
        }
        htmlText = driver.getPageSource();
        Document document = Jsoup.parse(htmlText);
        StringBuilder sb = new StringBuilder();
        Elements elements = document.select("div[class='abstract_main']");

        for(Element e:elements){
            for(Element e1:e.children()){
                if(e1.hasClass("abstract")){
                    for(Element ee: e1.children()){
                        String text = ee.select("p").text();
                        if(text.length()!=0){
                            sb.append(text);
                            sb.append("\n");
                        }
                    }
                }
            }
        }
        BaikeEntity entity = new BaikeEntity();
        entity.setEntityName(entityName);
        entity.setAbstractText(sb.toString());
        log.info("实体为{}的页面解析完成",entityName);
//        driver.close();

        return entity;
    }

    public static void main(String[] args) {
        SeleniumSpider seleniumSpider = new SeleniumSpider();
        BaikeEntity e = seleniumSpider.getEntity("成龙");
        System.out.println(e.getAbstractText());
    }


    @Override
    public BaikeEntity getEntity(String entityName) {
        return get(entityName);
    }
}
