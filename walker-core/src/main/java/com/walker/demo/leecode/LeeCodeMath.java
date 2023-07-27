package com.walker.demo.leecode;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class LeeCodeMath {

    //    剑指 Offer 14- I. 剪绳子
//    给你一根长度为 n 的绳子，请把绳子剪成整数长度的 m 段（m、n都是整数，n>1并且m>1）
//    ，每段绳子的长度记为 k[0],k[1]...k[m-1] 。请问 k[0]*k[1]*...*k[m-1]
//    可能的最大乘积是多少？例如，当绳子的长度是8时，我们把它剪成长度分别为2、3、3的三段，此时得到的最大乘积是18。
//    输入: 10
//    输出: 36
//    解释: 10 = 3 + 3 + 4, 3 × 3 × 4 = 36
    class Solution {
        public int cuttingRope(int n) {
            // 面积 最大 即 边长相等时 均分最大值
            if (n <= 3) {
                return n - 1;
            }
            long a = 1;
            int mod = 1000000007;
            while (n > 4) {
                n -= 3;
                a = (a * 3) % mod;
            }
            a = (a * n) % mod;

            return (int) a;
//
//        int p = n / 3;
//        if(last == 0){
//            return (int) Math.pow(3, p);
//        }else if(last == 1){
//            return (int) Math.pow(3, p - 1) * 4;
//        }else if(last == 2){
//            return (int) Math.pow(3, p) * 2;
//        }
        }
    }


    //    剑指 Offer 44. 数字序列中某一位的数字
//    数字以0123456789101112131415…的格式序列化到一个字符序列中。在这个序列中，第5位（从下标0开始计数）是5，第13位是1，第19位是4，等等。
//
//    请写一个函数，求任意第n位对应的数字。
    static class SolutionfindNthDigit {
        public static void main(String[] args) {
            new SolutionfindNthDigit().findNthDigit(1000000000);
        }

        public int findNthDigit(int n) {
            // 占位规律
            // [0,10) 1         -> 10
            // [10, 100) 2      -> 10 + 2 * 90
            // [100, 1000) 3    -> 10 + 2 * 90 + 3 * 900
            long nfrom = 1;
            int bit = 1;
            while (true) {
                long dbit = nfrom * 9 * bit;
                if (n > dbit) {
                    n -= dbit;
                    nfrom *= 10;
                    bit++;
                } else {
                    // 区间命中 n - 1 ???
                    long preNum = nfrom + ((n - 1) / bit);
                    return ((preNum) + "").charAt((n - 1) % bit) - '0';
                }
            }
        }
    }

    //    剑指 Offer 39. 数组中出现次数超过一半的数字
//    数组中有一个数字出现的次数超过数组长度的一半，请找出这个数字。
    class SolutionmajorityElement {
        public int majorityElement(int[] nums) {

            // 3目标和非目标分别去除 投票机制 摩尔投票法： 核心理念为 票数正负抵消
            int res = 0, votes = 0;
            for (int num : nums) {
                if (votes == 0) res = num;
                votes += (num == res ? 1 : -1);
            }

            // 2出现次数超过一半的话，排序之后的位于中间的元素肯定是

            // 1直观逻辑 计数 排序查找
            Map<Integer, Integer> map = new HashMap<>();
            for (int num : nums) {
                Integer i = map.get(num);
                if (i == null) {
                    i = 0;
                }
                map.put(num, ++i);
                if (i > nums.length / 2) {
                    return num;
                }
            }
            return 0;
        }
    }

    //    剑指 Offer 43. 1～n 整数中 1 出现的次数
//    输入一个整数 n ，求1～n这n个整数的十进制表示中1出现的次数。
//    例如，输入12，1～12这些整数中包含1 的数字有1、10、11和12，1一共出现了5次。
//    输入：n = 12
//    输出：5 todo
    class SolutioncountDigitOne {
        public int countDigitOne(int n) {
//    规律 每在后面加一位数, 则 可能1 , 可能!1, 1则须加上前面所有的数字遍历数, !1则加上(n/10)
//            [0, 10)
//             1
//            [0, 100)
//            f(100/10) * 10 + (10 - 1) * 1
//            [0, 1000)
//            f(1000/10) * 10 + f(1000/10)
            if (n < 10) {
                if (n >= 1) {
                    return 1;
                }
                return 0;
            } else {
                return n / 10 + countDigitOne(n / 10) + countDigitOne(n % 10);
            }
        }
    }

//    剑指 Offer 57 - II. 和为 s 的连续正数序列
//    输入一个正整数 target ，输出所有和为 target 的连续正整数序列（至少含有两个数）。
//    序列内的数字由小到大排列，不同序列按照首个数字从小到大排列。
//    示例 1：
//    输入：target = 9
//    输出：[[2,3,4],[4,5]]
//    示例 2：
//    输入：target = 15
//    输出：[[1,2,3,4,5],[4,5,6],[7,8]]

    static class SolutionfindContinuousSequence {
        public static void main(String[] args) {
            new SolutionfindContinuousSequence().findContinuousSequence(15);
        }

        public int[][] findContinuousSequence(int target) {
            List<int[]> res = new ArrayList<>();
//            for(int i = target - 1; i >= 2 ; i--){
//                int t = target / i;
//                List<Integer> list = new ArrayList<>();
//                int sum = 0;
//                int lt = t - (i - 1) / 2;
//                int rt = t + (i) / 2;
//                for(int j = lt; j <= rt && j > 0; j++){
//                    sum += j;
//                    list.add(j);
//                }
//                if(sum == target){
//                    res.add(list);
//                }
//
//            }

//            滑动窗口
            int left = 1;
            int right = 1;
            int sum = 0;
            while (right < target && left < target) {
                if (sum == target) {
                    int[] a = new int[right - left];
                    for (int i = left; i < right; i++) {
                        a[i - left] = i;
                    }
                    res.add(a);
                    sum -= left;
                    left++;
                } else if (sum > target) {
                    sum -= left;
                    left++;
                } else {
                    sum += right;
                    right++;
                }
            }

            return res.toArray(new int[0][]);
        }
    }

    //    剑指 Offer 62. 圆圈中最后剩下的数字
//    0,1,···,n-1这n个数字排成一个圆圈，从数字0开始，每次从这个圆圈里删除第m个数字（删除后从下一个数字开始计数）。求出这个圆圈里剩下的最后一个数字。
//    例如，0、1、2、3、4这5个数字组成一个圆圈，从数字0开始每次删除第3个数字，则删除的前4个数字依次是2、0、4、1，因此最后剩下的数字是3。
//    示例 1：
//    输入: n = 5, m = 3
//    输出: 3
    class SolutionlastRemaining {
        public int lastRemaining(int n, int m) {
            // 等价问题 m+1次删除什么数字 todo？？？
//            f(n)
//            =(f(n−1)+t)%n
//            =(f(n−1)+m%n)%n
//            =(f(n−1)+m)%n

            int x = 0;
            for (int i = 2; i <= n; i++) {
                x = (x + m) % i;
            }
            return x;
        }
    }

    //    剑指 Offer 66. 构建乘积数组
//    给定一个数组 A[0,1,…,n-1]，请构建一个数组 B[0,1,…,n-1]，其中 B[i] 的值是数组 A 中除了下标 i 以外的元素的积, 即 B[i]=A[0]×A[1]×…×A[i-1]×A[i+1]×…×A[n-1]。不能使用除法。
//    示例:
//    输入: [1,2,3,4,5]
//    输出: [120,60,40,30,24]
    class SolutionconstructArr {
        public int[] constructArr(int[] a) {

            return null;
        }
    }


}
