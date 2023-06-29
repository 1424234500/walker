package com.walker.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 数组 集合 工具类 仿造 java.util.Arrays
 * 测试 Set List Quene Map of Hash Linked LinkedHash 集合 数组  Collection Iterator
 *
 * @author Walker
 * <p>
 * <p>
 * <p>
 * Collection 接口 表示一组对象 add remove clear contains isEmpty size
 * 1.Set	实现非重复
 * 2.List	实现可重复
 */
public class ArraysUtil {


	/**
	 * 生成无重复乱序序列
	 */
	public static int[] getSequence(int size, int start) {
		Integer[] res = new Integer[size];
		res = initIntArr(res, -1);

		List<Integer> vv = initList(res);


		int v = 0;
		for (int i = 0; i < res.length; i++) {
			v = (int) (Math.random() * vv.size());
			res[i] = vv.get(v);
			vv.remove(v);
		}

		System.out.println("start." + start + " size." + size + "  生成序列结果：");
		arrayMake(res, new IMake() {
			public <T> void fun(T[] arr, int i, T value) {
				if (i < 30)
					System.out.print(value + " ");
			}
		});
		System.out.println();

		int[] rr = toInt(res);
		return rr;
	}

	public static int[] toInt(Integer[] arr) {
		final int[] res = new int[arr.length];
		arrayMake(arr, new IMake() {
			public <T> void fun(T[] arr, int i, T value) {
				res[i] = (Integer) value;
			}
		});
		return res;
	}

	public static <T> void arrayMake(T[] arr, IMake make) {
		int len = arr.length;
		for (int i = 0; i < len; i++) {
			make.fun(arr, i, arr[i]);
		}
	}

	public static <T> List<T> initList(T[] arr) {
		final List<T> res = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			res.add(arr[i]);
		}
//		for(int i = 0 ; i < arr.length; i++){
//			System.out.print(arr[i] + " ");
//		}
		return res;
	}

	public static int[] initIntArr(int[] arr, int value) {
		if (value == -1) {
			for (int i = 0; i < arr.length; i++) {
				arr[i] = i;
			}
			return arr;
		}
		for (int i = 0; i < arr.length; i++) {
			arr[i] = value;
		}
		return arr;
	}

	public static Integer[] initIntArr(Integer[] arr, Integer value) {
		if (value == -1) {
			for (int i = 0; i < arr.length; i++) {
				arr[i] = i;
			}
			return arr;
		}
		for (int i = 0; i < arr.length; i++) {
			arr[i] = value;
		}
		return arr;
	}

	public interface IMake {
		<T> void fun(T[] arr, int i, T value);
	}

}
