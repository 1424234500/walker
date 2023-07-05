package com.walker.demo.leecode;


import java.util.Arrays;

/**
 * https://leetcode.cn/leetbook/read/illustration-of-algorithm/pxal47/
 *
 * 「随机乱序」、「接近有序」、「完全倒序」、「少数独特」四类输入数据下，各常见排序算法的排序过程。
 * 「冒泡排序」、「插入排序」、「选择排序」、「快速排序」、「归并排序」、「堆排序」、「基数排序」、「桶排序」
 */
public class LeeCodeSort {

    // 冒泡 选择 跳过 过于简单

    /**
     * 快速排序
     */
    int[] sort(int[] arr){
        sort(arr, 0, arr.length - 1);
        return arr;
    }
    void sort(int[] arr, int left, int right){
        if(left >= right){
            return;
        }
        // 双边界反复横跳
        int l = left;
        int r = right;
        while (l < r){
            while(l < r && arr[left] <= arr[r]){
                r--;
            }
            while(l < r && arr[left] >= arr[l]){
                // 第一个一定相等 所以 烧饼不会参与交换
                l++;
            }
            swap(arr, l, r);
        }
        // 烧饼交换到交界地
        swap(arr, l, left);

        sort(arr, left, l - 1);
        sort(arr, l + 1, right);
    }
    void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }



    public static void out(int[] arr){
        String s = "";
        for (int i : arr) {
            s += i + ",";
        }
        System.out.println(s);
    }
    public static void main(String[] args) {
        out((new LeeCodeSort().sort(new int[]{2, 4, 1, 0, 3, 5})));
    }

    /**
     * 剑指 Offer 40. 最小的 k 个数
     */
    class SolutiongetLeastNumbers {
        public int[] getLeastNumbers(int[] arr, int k) {
//            sort(arr);
//            int[] res = new int[k];
//            System.arraycopy(arr, 0, res, 0, k);
//            return res;

            return Arrays.copyOf(sort(arr), k);
        }
    }


    /**
     * 剑指 Offer 45. 把数组排成最小的数
     * 核心区别在于 数字比较 1 10 101 110问题排序条件
     * while(l < r && (Double.valueOf(("" + arr[left] + arr[r]))) <= (Double.valueOf(("" + arr[r] + arr[left])))){
     */
    class SolutionminNumber {
        public String minNumber(int[] nums) {
            sort(nums);
            String s = "";
            for (int num : nums) {
                s += num;
            }
            return s;
        }
    }


}
