package com.xjtu.core;

import com.xjtu.common.BrowerChromeDriver;
import com.xjtu.model.BaikeEntity;
import com.xjtu.model.BaikeRelation;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

@Slf4j
public class ForkJoinSpiderTask extends RecursiveTask<List<BaikeEntity>> {
    private int threshold;
    private String[] entityList;
    private int start;
    private int end;
    private Spider spider;
    private Set<BaikeEntity> set = new HashSet<>();
    private ForkJoinSpiderTask(){}

    public static ForkJoinSpiderTask builder() {
        return new ForkJoinSpiderTask();
    }
    public ForkJoinSpiderTask setThreshold(int threshold){
        this.threshold=threshold;
        return this;
    }
    public ForkJoinSpiderTask setEntityList(String[] entityList){
        this.entityList=entityList;
        return this;
    }
    public ForkJoinSpiderTask setStartIndex(int start){
        this.start=start;
        return this;
    }
    public ForkJoinSpiderTask setEndIndex(int start){
        this.end=start;
        return this;
    }
    public ForkJoinSpiderTask setSpider(Spider spider){
        this.spider=spider;
        return this;
    }
    @Override
    protected List<BaikeEntity> compute() {
        List<BaikeEntity> result = new ArrayList<>();
        if((end-start)<=threshold){
            for(int i=start;i<end;++i){
                BaikeEntity baikeEntity = (BaikeEntity) spider.getEntity(entityList[i]);
                result.add(baikeEntity);
                // 将爬取到的信息存入文件中
                FileWriter fileWriter = null;
                Map<String, BaikeRelation> relationMap = baikeEntity.getRelationMap();
//                try {
//                    // 文件输出的目录
//                    String path="E:\\Java工程项目\\seleniumSpider\\target\\out\\";
//                    File file = new File(path+entityList[i]+".txt");
//                    fileWriter = new FileWriter(file);
//                    fileWriter.append(entityList[i]);
//                    fileWriter.append('\n');
//                    fileWriter.append(baikeEntity.getAbstractText());
//                    fileWriter.append("实体关系"+'\n');
//                    for(Map.Entry<String,BaikeRelation> e :relationMap.entrySet()){
//                        fileWriter.append(e.getKey())
//                                .append(" : ")
//                                .append(String.valueOf(e.getValue().getRelationName()))
//                                .append(String.valueOf('\n'));
//                    }
//                    fileWriter.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    log.error("实体为{}的信息保存到文件失败",entityList[i]);
//                }
            }
        }else{
            int mid = (start+end)/2;
            ForkJoinSpiderTask leftTask = new ForkJoinSpiderTask();
            ForkJoinSpiderTask rightTask = new ForkJoinSpiderTask();
            leftTask.setThreshold(threshold).setStartIndex(start).setEndIndex(mid).setSpider(spider).setEntityList(entityList);
            rightTask.setThreshold(threshold).setStartIndex(mid).setEndIndex(end).setSpider(spider).setEntityList(entityList);
            leftTask.fork();
            rightTask.fork();
            List<BaikeEntity> leftJoin = leftTask.join();
            List<BaikeEntity> rightJoin = rightTask.join();
            result.addAll(leftJoin);
            result.addAll(rightJoin);
        }
        return result;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        String[] list = new String[]{"成龙","周润发","周星驰","刘德华","张学友","周杰伦","梁朝伟","王力宏","五月天","张家辉","陈奕迅","林俊杰","陈绮贞"};
//        Spider spider= new SeleniumSpider();
                Spider<BaikeEntity> spider= new JsoupSpider();
        ForkJoinSpiderTask task = ForkJoinSpiderTask.builder()
                                                    .setThreshold(3)
                                                    .setStartIndex(0)
                                                    .setEndIndex(list.length)
                                                    .setSpider(spider)
                                                    .setEntityList(list);
                                    //                .setSpider(spider)
        ForkJoinTask<List<BaikeEntity>> submit = forkJoinPool.submit(task);
        try {
            // 爬虫结果
            List<BaikeEntity> list1 = submit.get();
            // 关闭浏览器
            if(spider instanceof SeleniumSpider){
                BrowerChromeDriver.ChromeDriverClass.driver.quit();
            }
            long end = System.currentTimeMillis();
            System.out.println("耗费时间"+ (end - startTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // 单线程爬虫时间
        long s = System.currentTimeMillis();
//        SeleniumSpider jsoupSpider = new SeleniumSpider();
        JsoupSpider jsoupSpider = new JsoupSpider();
        List<BaikeEntity> ans = new ArrayList<>();

        for(String e:list){
            BaikeEntity baikeEntity = jsoupSpider.getEntity(e);
            ans.add(baikeEntity);
        }
        long e = System.currentTimeMillis();
        System.out.println(e-s);
        System.out.println();
    }
}
