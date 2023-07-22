package com.walker.demo.leecode;


import java.util.HashMap;
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
}
