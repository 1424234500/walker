package com.walker.demo.leecode;


import java.util.ArrayList;
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


}
