package com.walker.util;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 字段描述约束
 */
@Data
@AllArgsConstructor
public class Model<T> {
    /**
     * 键
     */
    String key;
    /**
     * 中文描述
     */
    String name;
    /**
     * 值
     */
    T value;


    public static Model<String> buildDir(String dir){
        return build("dir", "目录", dir);
    }

    public static <T> Model<T> build(String name, T value){
        return build("", name, value);
    }
    public static <T> Model<T> build(String key, String name, T value){
        return new Model(key, name, value);
    }


}
