package com.walker.core.annotation;

/**
 * 处理结果 决定该类的其他处理器是否执行
 */
public enum Status {
    /**
     * 正常模式 处理完com.ttt-class-table后 继续处理后续的 com.ttt-class-table
     * com.ttt-field-test
     */
    NORMAL,
    /**
     * 不再处理该类 的 同名 处理完com.ttt-class-table后 不再执行 后续的com.ttt-class-table
     */
    STOP_NAME,
    /**
     * 不再处理该类 处理完com.ttt-class-table后 不再执行 com.ttt-field 但是会执行后续的com.ttt-class-table 且可被后续返回值覆盖
     */
    STOP_CLASS,
    /**
     * 不再处理 处理完com.ttt-class-table 后该类处理完毕 不再处理
     */
    STOP,
}


///**
// * 绑定结构 class 注解 执行情况 是否停止
// */
//class Classer {
//	public Class<?> cls;
//	public Status status;
//
//	public Classer(Class<?> cls, Status status) {
//		this.cls = cls;
//		this.status = status;
//	}
//}
