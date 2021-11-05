package com.xjtu.core;

import com.xjtu.model.BaikeEntity;
import com.xjtu.model.BaikeRelation;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import static com.xjtu.common.Config.BAIKE_URL;
import static com.xjtu.common.Config.USER_AGENT;

@Slf4j
public class JsoupSpider implements Spider<BaikeEntity>{
    private String entityName;
    public BaikeEntity get(String url){
        StringBuilder sb = new StringBuilder();
        BaikeEntity baikeEntity = new BaikeEntity();
        // 超时重试次数设置为3次
        log.info("开始获取百度百科网页,实体为:{}",entityName);
        Document document =null;

        try {
            document = Jsoup.connect(url).userAgent(USER_AGENT).timeout(5000).get();
        } catch (IOException e) {
            log.error("{} 网页加载失败",entityName);
            e.printStackTrace();
        }
        Elements elements = document.select("div[class='lemma-summary']");
        for(Element element:elements){
            for(Element e:element.children()){
                String text = e.select("div[class='para']").text();
                if(text.length()!=0){
                    sb.append(text);
                    sb.append("\n");
                }
            }
        }
        // 可能会存在百科页面中不包含实体关系信息的情况
        try{
            Element relation = document.selectFirst("div[id='slider_relations']");
            Elements es = relation.select("a[class='J-relations-item']");
            for(Element e: es){
                String entityName = e.attr("data-title");
                String text = e.text();
                String relationName = text.substring(0,text.indexOf(entityName));
                BaikeRelation entityRelation = new BaikeRelation(relationName);
                baikeEntity.getRelationMap().put(entityName,entityRelation);
            }
        }catch(Exception e){
            log.error("百科页面不存在与实体:{} 关联的实体",entityName);
        }
        log.info("实体为:{}的百科网页解析完成",entityName);
        String abstractText = sb.toString();
        baikeEntity.setEntityName(entityName);
        baikeEntity.setAbstractText(abstractText);
        return baikeEntity;

    }
    public BaikeEntity getEntity(String entityName){
        this.entityName=entityName;
        return get(BAIKE_URL+entityName);
    }

    public static void main(String[] args) {
        JsoupSpider jsoupSpider = new JsoupSpider();
        BaikeEntity txt = jsoupSpider.getEntity("陈信宏");


    }

}
