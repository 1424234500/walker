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
     * 剑指 Offer 10- I. 斐波那契数列
     * F(0) = 0,   F(1) = 1  1 2 3 5 8 13
     * F(N) = F(N - 1) + F(N - 2), 其中 N > 1.
     */
    static class Solutionfib {
        public int fib(int n) {
            int[] res = new int[Math.max(2,  n + 1)];
            res[0] = 0;
            res[1] = 1;
            for(int i = 2; i <= n; i ++){
                res[i] = (res[i - 1] + res[i - 2]) % 1000000007;
            }
            return res[n];
        }
    }

    /**
     * 剑指 Offer 10- II. 青蛙跳台阶问题
     * 一只青蛙一次可以跳上1级台阶，也可以跳上2级台阶。求该青蛙跳上一个 n 级的台阶总共有多少种跳法。
     * 答案需要取模 1e9+7（1000000007），如计算初始结果为：1000000008，请返回 1。
     *
     * 1 -> 1
     * 2 -> 1 + 1
     *      2
     * 3 -> 1 + 1 + 1
     *      2 + 1
     *      1 + 2
     *
     * 倒推 最后一步 只能 跳 1  或跳 2
     * 跳1 则 = f(n - 1)
     * 跳2 则 = f(n - 2)
     * dp[n] = dp[n-1] + dp[n-2];
     *
     */
    static class SolutionnumWays {
        public int numWays(int n) {
            int[] res = new int[Math.max(2,  n + 1)];
            res[0] = 1;
            res[1] = 1;
            for(int i = 2; i <= n; i ++){
                res[i] = (res[i - 1] + res[i - 2]) % 1000000007;
            }
            return res[n];
        }
    }

    /**
     * 剑指 Offer 19. 正则表达式匹配
     * 请实现一个函数用来匹配包含'. '和'*'的正则表达式。模式中的字符'.'表示任意一个字符，而'*'表示它前面的字符可以出现任意次（含0次）。在本题中，匹配是指字符串的所有字符匹配整个模式。例如，字符串"aaa"与模式"a.a"和"ab*ac*a"匹配，但与"aa.a"和"ab*a"均不匹配。
     */
    static class SolutionisMatch {
        public boolean isMatch(String s, String p) {
            throw new RuntimeException("what ???");
        }
    }

}
