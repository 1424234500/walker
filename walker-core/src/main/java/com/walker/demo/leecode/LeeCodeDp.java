package com.walker.demo.leecode;

/**
 * 暴力递归
 *
 * 记忆化递归和动态规划的本质思想是一致的，是对斐波那契数列定义的不同表现形式：
 *
 * 记忆化递归 — 从顶至低： 求 �(�)f(n) 需要 �(�−1)f(n−1) 和 �(�−2)f(n−2) ； ⋯⋯ ；求 �(2)f(2) 需要 �(1)f(1) 和 �(0)f(0) ；而 �(1)f(1) 和 �(0)f(0) 已知；
 *
 * 动态规划 — 从底至顶： 将已知 �(0)f(0) 和 �(1)f(1) 组合得到 �(2)f(2) ；⋯⋯ ；将 �(�−2)f(n−2) 和 �(�−1)f(n−1) 组合得到 �(�)f(n) ；
 *
 */
public class LeeCodeDp {

    /**
     * F(0) = 0,   F(1) = 1
     * F(N) = F(N - 1) + F(N - 2), 其中 N > 1.
     */
    static class Solutionfib {
        public int fib(int n) {
            int[] res = new int[Math.max(2,  n + 1)];
            res[0] = 0;
            res[1] = 1;
            for(int i = 2; i <= n; i ++){
                res[i] = (res[i - 1] + res[i - 2])%1000000007;
            }
            return res[n];
        }
    }



}
