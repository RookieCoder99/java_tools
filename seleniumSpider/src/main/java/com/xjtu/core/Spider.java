package com.xjtu.core;

public interface Spider<T> {
    T get(String url);
    T getEntity(String entityName);
}
