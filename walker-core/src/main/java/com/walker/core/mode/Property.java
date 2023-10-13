package com.walker.core.mode;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 字段描述约束
 *
 * 定义一个属性 有 名字 中文名 详细信息 以及具体的值
 *
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class Property<T> {
    /**
     * 键 英文名 字段名
     */
    String key;
    /**
     * 中文名 描述
     */
    String name;
    /**
     * 详细信息
     */
    String info;
    /**
     * 值
     */
    T value;


    public static Property<String> buildDir(String nowdir) {
        return new Property<>("dir", "文件夹", "", nowdir);
    }
    public static Property<String> buildFile(String file) {
        return new Property<>("file", "文件", "", file);
    }

    public static <T> Property<T> build(String key, T value) {
        return new Property<>(key, key, "", value);
    }
    public static <T> Property<T> build(String key, String name, T value) {
        return new Property<>(key, name, "", value);
    }
    public static <T> Property<T> build(String key, String name, String info, T value) {
        return new Property<>(key, name, info, value);
    }

}
