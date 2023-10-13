package com.walker.demo.leecode;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://leetcode.cn/leetbook/read/illustration-of-algorithm/pxal47/
 *
 * 「随机乱序」、「接近有序」、「完全倒序」、「少数独特」四类输入数据下，各常见排序算法的排序过程。
 * 「冒泡排序」、「插入排序」、「选择排序」、「快速排序」、「归并排序」、「堆排序」、「基数排序」、「桶排序」
 */
public class LeeCodeSort {

    // 冒泡 选择 跳过 过于简单

    /**
     * 快速排序 https://leetcode.cn/leetbook/read/illustration-of-algorithm/p57uhr/
     * @param right 为实际可取坐标 length - 1
     */
    static int[] sortQuick(int[] arr, int left, int right){
        if(left >= right){
            return arr;
        }
        // 双边界反复横跳
        int l = left;
        int r = right;
        while (l < r){
            // 右边的都比烧饼大 直到某数小于烧饼 则待交换 r最小等于l
            while(l < r && arr[left] <= arr[r]){
                r--;
            }
            // 左边的都比烧饼小 直到某数大于烧饼 则待交换 l最大等于r
            while(l < r && arr[left] >= arr[l]){
                // 第一个一定相等 所以 烧饼不会参与交换
                l++;
            }
            swap(arr, l, r);
        }
        // 烧饼交换到交界地
        swap(arr, l, left);

        sortQuick(arr, left, l - 1);
        sortQuick(arr, l + 1, right);
        return arr;
    }
    static void swap(int[] nums, int i, int j) {
        if(i == j){
            return;
        }
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }


    /**
     * 归并排序 https://leetcode.cn/leetbook/read/illustration-of-algorithm/p5l0js/
     * 采用copy数组 更直观算法
     * @param right 为 length-1
     */
    static int[] sortMerge(int[] arr, int left, int right){
        // 终止条件
        if(left >= right){
            return new int[]{arr[left]};
        }
        if(left == right - 1){
            if(arr[left] <= arr[right]){
                return new int[]{arr[left], arr[right]};
            }else{
                return new int[]{arr[right], arr[left]};
            }
        }
        // 中值计算
        // 0 1 2 3 4 -> 2 -> 0,1,2   3,4
        // 0 1 2 3   -> 1 -> 0,1     2,3
        int t = (left + right) / 2;

        // 二分区间 各自排序
        int[] arrLeft = sortMerge(arr, left, t);
        int[] arrRight = sortMerge(arr, t + 1, right);

        // 合并区间
        int[] res = new int[right - left + 1];
        for(int i = 0, j = 0, k = 0; k < res.length ; k++){
            if(i >= arrLeft.length){
                res[k] = arrRight[j++];
            }else if(j >= arrRight.length){
                res[k] = arrLeft[i++];
            }else if(arrLeft[i] <= arrRight[j] ){
                res[k] = arrLeft[i++];
            }else{
                res[k] = arrRight[j++];
            }
        }
        return res;
    }


    public static void out(int[] arr){
        String s = "";
        for (int i : arr) {
            s += i + ",";
        }
        System.out.println(s);
    }
    public static void main(String[] args) {
        int[] arr = new int[]{2, 4, 1, 0, 3, 5};
        out((new LeeCodeSort().sortQuick(arr, 0, arr.length -1)));
        arr = new int[]{2, 4, 1, 0, 3, 5, 9};
        out((new LeeCodeSort().sortMerge(arr, 0, arr.length - 1)));
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
            return Arrays.copyOf(sortQuick(arr, 0, arr.length - 1), k);
        }
    }


    /**
     * 剑指 Offer 45. 把数组排成最小的数
     * 核心区别在于 数字比较 1 10 101 110问题排序条件
     * while(l < r && (Double.valueOf(("" + arr[left] + arr[r]))) <= (Double.valueOf(("" + arr[r] + arr[left])))){
     */
    class SolutionminNumber {
        public String minNumber(int[] nums) {
            sortQuick(nums, 0, nums.length - 1);
            String s = "";
            for (int num : nums) {
                s += num;
            }
            return s;
        }
    }

    /**
     *剑指 Offer 41. 数据流中的中位数
     * 如果从数据流中读出奇数个数值，那么中位数就是所有数值排序之后位于中间的数值。
     * 如果从数据流中读出偶数个数值，那么中位数就是所有数值排序之后中间两个数的平均值。
     *
     * 设计一个支持以下两种操作的数据结构：
     *
     * void addNum(int num) - 从数据流中添加一个整数到数据结构中。
     * double findMedian() - 返回目前所有元素的中位数。
     *
     */
    static class MedianFinder {
        List<Integer> data = new ArrayList<>();
        /** initialize your data structure here. */
        public MedianFinder() {

        }

        public void addNum(int num) {
            int i = 0;
            for (; i < data.size(); i++) {
                if(data.get(i) < num){
                    break;
                }
            }
            data.add(i, num);
        }

        public double findMedian() {
            if(data.size() == 0){
                return 0;
            }
            return data.size() % 2 == 1
                    ? data.get(data.size() / 2)
                    : (data.get(data.size() / 2 - 1) + data.get(data.size() / 2)) / 2f
                    ;
        }

        public static void main(String[] args) {
            MedianFinder medianFinder = new MedianFinder();
            medianFinder.addNum(1);
            medianFinder.addNum(2);
            medianFinder.findMedian();
        }
    }

    /**
     * 剑指 Offer 61. 扑克牌中的顺子
     * 从若干副扑克牌中随机抽 5 张牌，判断是不是一个顺子，即这5张牌是不是连续的。
     * 2～10为数字本身，A为1，J为11，Q为12，K为13，而大、小王为 0 ，可以看成任意数字。
     */
    static class SolutionisStraight {
        public boolean isStraight(int[] nums) {
            nums = sortMerge(nums, 0, nums.length - 1);
            int zeroNum = 0;
            for (zeroNum = 0; zeroNum < nums.length; zeroNum++) {
                if(nums[zeroNum] != 0){
                    break;
                }
            }
            // 重复则不是顺子
            for (int i = zeroNum; i < nums.length; i++) {
                if(i + 1 < nums.length && nums[i] == nums[i + 1]){
                    return false;
                }
            }

            // 阶梯计数 最大差值 不能大于补位的数量
            return zeroNum >= nums[nums.length - 1] - nums[zeroNum] - (nums.length - zeroNum - 1);
        }

        public static void main(String[] args) {
            int[] arr = new int[]{1, 2, 4, 5, 6};
            new SolutionisStraight().isStraight(arr);
        }
    }
}
