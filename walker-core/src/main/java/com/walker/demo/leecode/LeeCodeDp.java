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
     * 请实现一个函数用来匹配包含'. '和'*'的正则表达式。模式中的字符
     * '.'表示任意一个字符，而'*'表示它前面的字符可以出现任意次（含0次）。
     * 在本题中，匹配是指字符串的所有字符匹配整个模式。例如，
     * 字符串"aaa"与模式"a.a"和"ab*ac*a"匹配，
     * 但与"aa.a"和"ab*a"均不匹配。 todo
     */
    static class SolutionisMatch {
        public boolean isMatch(String s, String p) {
            throw new RuntimeException("what ???");
        }
    }

    //    剑指 Offer 42. 连续子数组的最大和
//    输入一个整型数组，数组中的一个或连续多个整数组成一个子数组。求所有子数组的和的最大值。
//    要求时间复杂度为O(n)。
//    示例1:
//    输入: nums = [-2,1,-3,4,-1,2,1,-5,4]
//    输出: 6
//    解释: 连续子数组 [4,-1,2,1] 的和最大，为 6。 todo
    class SolutionmaxSubArray {
        public int maxSubArray(int[] nums) {
            int max = nums[0];
            int sum = max;
            for (int i = 1; i < nums.length; i++) {
                if (sum <= 0) {
                    sum = nums[i];
                } else {
                    sum += nums[i];
                }
                max = max > sum ? max : sum;
            }
            return max;
        }
    }


    //    剑指 Offer 46. 把数字翻译成字符串
//    给定一个数字，我们按照如下规则把它翻译为字符串：0 翻译成 “a” ，1 翻译成 “b”，……，11 翻译成 “l”，……，25 翻译成 “z”。一个数字可能有多个翻译。请编程实现一个函数，用来计算一个数字有多少种不同的翻译方法。
//    示例 1:
//    输入: 12258
//    输出: 5
//    解释: 12258有5种不同的翻译，分别是"bccfi", "bwfi", "bczi", "mcfi"和"mzi"
//    1 2 2 5 8
//    12 2 5 8
//    青蛙跳台阶问题
//* 倒推 最后一步 只能 跳 1  或跳 2
//* 跳1 则 = f(n - 1)
//* 跳2 则 = f(n - 2)
//* dp[n] = dp[n-1] + dp[n-2];
    class SolutiontranslateNum {
        public int translateNum(int num) {
            String s = "" + num;
            return dp(s);
        }

        public int dp(String str) {
            if (str.length() <= 1) {
                return 1;
            }
            if (Integer.valueOf(str.substring(str.length() - 2)) >= 26 || str.charAt(str.length() - 2) == '0') {
                return dp(str.substring(0, str.length() - 1));
            } else {
                return dp(str.substring(0, str.length() - 1)) + dp(str.substring(0, str.length() - 2));
            }
        }
    }

    //    剑指 Offe 1→3→5→2→1 可以拿到最多价值的礼r 47. 礼物的最大价值
//    在一个 m*n 的棋盘的每一格都放有一个礼物，每个礼物都有一定的价值（价值大于 0）。你可以从棋盘的左上角开始拿格子里的礼物，并每次向右或者向下移动一格、直到到达棋盘的右下角。给定一个棋盘及其上面的礼物的价值，请计算你最多能拿到多少价值的礼物？
//    示例 1:
//    输入:
//            [
//              [1,3,1],
//              [1,5,1],
//              [4,2,1]
//            ]
//    输出: 12
//    解释: 路径物
    class SolutionmaxValue {
        public int maxValue(int[][] grid) {
            return maxValue(grid, 0, 0);
        }

        public int maxValue(int[][] grid, int i, int j) {
            int res = grid[i][j];
            if (i >= grid.length - 1) {
                if (j >= grid[i].length - 1) {
                    return res;
                } else {
                    return res + maxValue(grid, i, j + 1);
                }
            } else {
                if (j >= grid[i].length - 1) {
                    return res + maxValue(grid, i + 1, j);
                } else {
                    return res + Math.max(maxValue(grid, i + 1, j), maxValue(grid, i, j + 1));
                }
            }
        }
    }


}
