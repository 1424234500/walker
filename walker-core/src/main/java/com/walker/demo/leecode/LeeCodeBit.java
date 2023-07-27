package com.walker.demo.leecode;


/**
 */
public class LeeCodeBit {


    /**
     * 剑指 Offer 15. 二进制中 1 的个数
     * 编写一个函数，输入是一个无符号整数（以二进制串的形式）
     * ，返回其二进制表达式中数字位数为 '1' 的个数（也被称为 汉明重量).）。
     */
    public class SolutionhammingWeight  {
        // you need to treat n as an unsigned value
        public int hammingWeight(int n) {
            int res = 0;
            do{
                if((n & 1) == 1){
                    res++;
                }
                // >> 有符号移位 正数高位补0 负数高位补1
                // >>> 无符号移位 高位都补0
                n = n >>> 1;
            } while (n > 0);
            return res;
        }
    }

//    剑指 Offer 56 - I. 数组中数字出现的次数
//    一个整型数组 nums 里除两个数字之外，其他数字都出现了两次。
//    请写程序找出这两个只出现一次的数字。要求时间复杂度是O(n)，空间复杂度是O(1)。
    // 输入：nums = [1,2,10,4,1,4,3,3]
    // 输出：[2,10] 或 [10,2]

    //    剑指 Offer 56 - II. 数组中数字出现的次数 II
//    在一个数组 nums 中除一个数字只出现一次之外，其他数字都出现了三次。请找出那个只出现一次的数字。
//    输入：nums = [9,1,7,9,7,9,7]
//    输出：1
    static class SolutionsingleNumber {
        public static void main(String[] args) {
            new SolutionsingleNumber().singleNumber(new int[]{3, 4, 3, 3});
            new SolutionsingleNumber().singleNumber(new int[]{9, 1, 7, 9, 7, 9, 7});
        }

        public int singleNumber(int[] nums) {
//        考虑数字的二进制形式，对于出现三次的数字，各 二进制位 出现的次数都是
//        3 的倍数。
//        因此，统计所有数字的各二进制位中
//        1 的出现次数，并对
//        3 求余，结果则为只出现一次的数字。
//        方法二：遍历统计
//        此方法相对容易理解，但效率较低，总体推荐方法一。
//        建立一个长度为 32 的数组    counts ，通过以上方法可记录所有数字的各二进制位的
//        1 的出现次数之和。
            int[] arr = new int[32];
            for (int i = 0; i < nums.length; i++) {
                for (int j = 0; j < 32; j++) {
                    // 每一位 bit 计数
                    arr[j] += nums[i] & 1;
                    nums[i] = nums[i] >>> 1;
                }
            }
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i] % 3;
            }
            int res = 0;
            for (int i = arr.length - 1; i >= 0; i--) {
                res = res * 2 + arr[i];
            }

            return res;
        }
    }

    class SolutionsingleNumbers {
        // 疑惑 异或 what？？？？
        public int[] singleNumbers(int[] nums) {
            int x = 0, y = 0, n = 0, m = 1;
            for (int num : nums)               // 1. 遍历异或
                n ^= num;
            while ((n & m) == 0)               // 2. 循环左移，计算 m
                m <<= 1;
            for (int num : nums) {              // 3. 遍历 nums 分组
                if ((num & m) != 0) x ^= num;  // 4. 当 num & m != 0
                else y ^= num;                // 4. 当 num & m == 0
            }
            return new int[]{x, y};          // 5. 返回出现一次的数字


//            int arr[] = new int[32];
//            for (int i = 0; i < nums.length; i++) {
//                for(int j = 0; j < 32; j++){
//                    // 每一位 bit 计数
//                    arr[j] += nums[i] & 1;
//                    nums[i] = nums[i] >>> 1;
//                }
//            }
//            for (int i = 0; i < arr.length; i++) {
//                arr[i] = arr[i] % 2;
//            }
//            int res = 0;
//            for (int i = arr.length - 1; i >= 0; i--) {
//                res = res * 2 + arr[i];
//            }
//            int i = 0;
//            int[] res1 = new int[2];
//            for (int num : nums) {
//                if((num & res) == num){
//                    res1[i++] = num;
//                }
//            }
//
//            return res1;
        }
    }


//    剑指 Offer 65. 不用加减乘除做加法
//    写一个函数，求两个整数之和，要求在函数体内不得使用 “+”、“-”、“*”、“/” 四则运算符号。
    class Solutionadd {
        public int add(int a, int b) {
            if (a == 0) return b;
            if (b == 0) return a;
            // 无进位的 加法 即 异或
            // a&b取所有同时为1 且左移 即 进位值
            return add(a ^ b, (a & b) << 1);
        }
    }
}
