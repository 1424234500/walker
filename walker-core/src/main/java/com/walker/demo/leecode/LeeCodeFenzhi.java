package com.walker.demo.leecode;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class LeeCodeFenzhi {

    //剑指 Offer 07. 重建二叉树 what?????????????????
    //输入某二叉树的前序遍历和中序遍历的结果，请构建该二叉树并返回其根节点。
    //假设输入的前序遍历和中序遍历的结果中都不含重复的数字。
    static class SolutionbuildTree {
        TreeNode res = null;
        List<Integer> pre = new ArrayList<>();
        List<Integer> in = new ArrayList<>();

        public TreeNode buildTree(int[] preorder, int[] inorder) {
            // 前序 中 前 后
            // 中序 前 中 后
            if (preorder.length == 0 || inorder.length == 0) {
                return null;
            }

            for (int i : preorder) {
                pre.add(i);
            }
            for (int i : inorder) {
                in.add(i);
            }
            res = new TreeNode(pre.remove(0));
            return res;
        }

    }

    //    剑指 Offer 16. 数值的整数次方
//    实现 pow(x, n) ，即计算 x 的 n 次幂函数（即，xn）。不得使用库函数，同时不需要考虑大数问题。
    class Solution {
        public double myPow(double x, int n) {
            if (n == 0) {
                return 1;
            }
            // what fuck ???
            if (x == 1) {
                return 1;
            }
            if (x == -1) {
                if (n <= -2147483648) {
                    return 1;
                }
                return -1;
            }
            if (n >= 2147483647 && x > 0 && x < 1) {
                return 0;
            }
            if (n <= -2147483647) {
                return 0;
            }
            int nn = n < 0 ? -n : n;

            double res = pow(x, nn);
            return n > 0 ? res : 1 / res;
        }

        public double pow(double x, int nn) {
            if (nn == 0) {
                return 1;
            }
            if (nn == 1) {
                return x;
            }
            boolean flag = false;
            if ((nn & 1) == 0) {
                // 偶数
            } else {
                // 奇数
                nn += 1;
                flag = true;
            }
            double res = pow(x * x, nn / 2);
            if (flag) {
                res /= x;
            }
            return res;
        }
    }

    //    剑指 Offer 17. 打印从 1 到最大的 n 位数
//    输入数字 n，按顺序打印出从 1 到最大的 n 位十进制数。比如输入 3，则打印出 1、2、3 一直到最大的 3 位数 999。
//    示例 1:
//    输入: n = 1
//    输出: [1,2,3,4,5,6,7,8,9]
    class SolutionprintNumbers {
        public int[] printNumbers(int n) {
            if (n <= 0) {
                return new int[0];
            }
            int[] pre = printNumbers(n - 1);
            int begin = (int) Math.pow(10, n - 1);
            int end = begin * 10;
            int[] res = new int[pre.length + end - begin];
            System.arraycopy(pre, 0, res, 0, pre.length);
            int k = pre.length;
            for (int i = begin; i < end; i++) {
                res[k++] = i;
            }
            return res;
        }
    }

    //    剑指 Offer 33. 二叉搜索树的后序遍历序列
//    输入一个整数数组，判断该数组是不是某二叉搜索树的后序遍历结果。如果是则返回 true，否则返回 false。假设输入的数组的任意两个数字都互不相同。
//    输入: [1,6,3,2,5] todo
//    输出: false
    class SolutionverifyPostorder {
        public boolean verifyPostorder(int[] postorder) {
            LinkedList<Integer> stack = new LinkedList<>();
            int root = Integer.MAX_VALUE;
            for (int i = postorder.length - 1; i >= 0; i--) {
                if (postorder[i] > root) return false;
                while (!stack.isEmpty() && stack.peek() > postorder[i])
                    root = stack.pop();
                stack.add(postorder[i]);
            }
            return true;

        }
    }

    //
//    剑指 Offer 51. 数组中的逆序对
//    在数组中的两个数字，如果前面一个数字大于后面的数字，则这两个数字组成一个逆序对。输入一个数组，求出这个数组中的逆序对的总数。
//    输入: [7,5,6,4]
//    输出: 5 -> 7,5  7,6  7,4  5,4  6,4 todo
    class SolutionreversePairs {
        public int reversePairs(int[] nums) {
            int res = 0;
            for (int i = 0; i < nums.length; i++) {
                for (int j = i + 1; j < nums.length; j++) {
                    if (nums[i] > nums[j]) {
                        res++;
                    }
                }
            }
            return res;
        }
    }


}
