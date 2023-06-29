package com.walker.core.mode;


import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.io.Serializable;


/**
 * Java数组大小的最大值为Integer.MAX_VALUE。使用直接内存分配，我们创建的数组大小受限于堆大小；
 * 实际上，这是堆外内存（off-heap memory）技术，在java.nio包中部分可用；
 *
 * 这种方式的内存分配不在堆上，且不受GC管理，所以必须小心Unsafe.freeMemory()的使用。
 * 它也不执行任何边界检查，所以任何非法访问可能会导致JVM崩溃
 */
public class UnsafeSuperArray implements Serializable {

	private static Unsafe unsafe = UnsafeUtils.getUnsafe();

	private final static int BYTE = 1;

	private long size;
	private long address;

	public UnsafeSuperArray(long size) {
		this.size = size;
		address = unsafe.allocateMemory(size * BYTE);
	}
	public void clear(){
		unsafe.freeMemory(address);
	}

	public void set(long i, byte value) {
		unsafe.putByte(address + i * BYTE, value);
	}

	public int get(long idx) {
		return unsafe.getByte(address + idx * BYTE);
	}

	public long size() {
		return size;
	}


}
