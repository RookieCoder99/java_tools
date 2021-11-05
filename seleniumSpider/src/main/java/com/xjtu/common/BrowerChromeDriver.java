package com.xjtu.common;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

import static com.xjtu.common.Config.CHROME_DRIVER_PATH;

public class BrowerChromeDriver {
    public static ChromeOptions defaultChromeOptions(){
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("no-sandbox");
        chromeOptions.addArguments("headless");
        return chromeOptions;
    }
    public static ChromeDriver defaultChromeDriver(){
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        ChromeDriver driver = new ChromeDriver(defaultChromeOptions());
        driver.manage().timeouts().pageLoadTimeout(5000, TimeUnit.MILLISECONDS);
        return driver;
    }
    public static class ChromeDriverClass{
        public static  ChromeDriver driver=  defaultChromeDriver();
    }
}
