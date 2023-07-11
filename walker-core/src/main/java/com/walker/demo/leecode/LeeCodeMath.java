package com.walker.demo.leecode;


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


}
