package com.walker.demo.leecode;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 */
public class LeeCodeStayBack {

//  剑指 Offer 12. 矩阵中的路径
//   给定一个 m x n 二维字符网格 board 和一个字符串单词 word 。如果 word 存在于网格中，返回 true ；否则，返回 false 。
//
//单词必须按照字母顺序，通过相邻的单元格内的字母构成，其中“相邻”单元格是那些水平相邻或垂直相邻的单元格。同一个单元格内的字母不允许被重复使用。
//
//    输入：board = [
//      ["A","B","C","E"],
//      ["S","F","C","S"],
//      ["A","D","E","E"]]
//      , word = "ABCCED"
//    输出：true

    static class Solutionexist {

        public static void main(String[] args) {
            new Solutionexist().exist(new char[][]{
                    new char[]{'A', 'B', 'C', 'E'}
                    , new char[]{'S', 'F', 'C', 'S'}
                    , new char[]{'A', 'D', 'E', 'E'}
            }, "ABCCED");
        }
        String word;
        public boolean exist(char[][] board, String word) {
            this.word = word;
            Map<Character, Integer> map1 = new HashMap<>();
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    map1.put(board[i][j], 1);
                }
            }
            for (char c : word.toCharArray()) {
                if (!map1.containsKey(c)) {
                    return false;
                }
            }
            Map<String, Boolean> map = new HashMap<>();
            LinkedList<Character> route = new LinkedList<>();

            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    route.clear();
                    map.clear();
                    if (go(route, map, board, i, j)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean go(LinkedList<Character> route, Map<String, Boolean> map, char[][] board, int i, int j) {
            if (i >= board.length || i < 0) {
                return false;
            }
            if (j >= board[i].length || j < 0) {
                return false;
            }
            if (map.containsKey(i + "_" + j)) {
                return false;
            }

            route.add(board[i][j]);
            map.put(i + "_" + j, true);

            String s = "";
            for (int i1 = 0; i1 < route.size(); i1++) {
                s += route.get(i1);
            }
            if (!word.startsWith(s)) {
                route.removeLast();
                map.remove(i + "_" + j);
                return false;
            }
            if (s.contains(word)) {
                return true;
            }

            if (go(route, map, board, i, j + 1)) {
                return true;
            }
            if (go(route, map, board, i + 1, j)) {
                return true;
            }
            if (go(route, map, board, i, j - 1)) {
                return true;
            }
            if (go(route, map, board, i - 1, j)) {
                return true;
            }
            route.removeLast();
            map.remove(i + "_" + j);
            return false;
        }
    }


    //    剑指 Offer 13. 机器人的运动范围
//    地上有一个m行n列的方格，从坐标 [0,0] 到坐标 [m-1,n-1] 。一个机器人从坐标 [0, 0] 的格子开始移动，它每次可以向左、右、上、下移动一格（不能移动到方格外），也不能进入行坐标和列坐标的数位之和大于k的格子。例如，当k为18时，机器人能够进入方格 [35, 37] ，因为3+5+3+7=18。但它不能进入方格 [35, 38]，因为3+5+3+8=19。请问该机器人能够到达多少个格子？
//    输入：m = 2, n = 3, k = 1
//    输出：3
    static class SolutionmovingCount {
        public static void main(String[] args) {
            // new SolutionmovingCount().movingCount(11, 8, 16);
            new SolutionmovingCount().movingCount(38, 15, 9);
        }

        // k 数位之和 而非 步数限制
        public int movingCount(int m, int n, int k) {
            Map<String, Boolean> map = new HashMap<>();
            LinkedList<Integer> route = new LinkedList<>();
            int[][] board = new int[m][n];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    board[i][j] = 0;
                }
            }
            go(route, map, board, 0, 0, k);
//            return map.size();
            int res = 0;
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    res += (board[i][j] > 0 ? 1 : 0);
                }
            }
            return res;
        }

        public int getSum(int num) {
            int sum = 0;
            for (; num > 0; num /= 10)
                sum += num % 10;
            return sum;
        }

        public boolean go(LinkedList<Integer> route, Map<String, Boolean> map, int[][] board, int i, int j, int bitCount) {
            if (i >= board.length || i < 0) {
                return false;
            }
            if (j >= board[i].length || j < 0) {
                return false;
            }
            if (map.containsKey(i + "_" + j)) {
                return false;
            }
            if (getSum(i) + getSum(j) > bitCount) {
                return false;
            }
            route.add(board[i][j]);
            board[i][j]++;
            map.put(i + "_" + j, true);


            go(route, map, board, i, j + 1, bitCount);
            go(route, map, board, i + 1, j, bitCount);
//        go(route, map, board, i, j - 1, bitCount);
//        go(route, map, board, i - 1, j, bitCount);

            route.removeLast();
            map.remove(i + "_" + j);

            return false;
        }
    }

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    /**
     * 剑指 Offer 26. 树的子结构
     * 输入两棵二叉树A和B，判断B是不是A的子结构。(约定空树不是任意一个树的子结构)
     * B是A的子结构， 即 A中有出现和B相同的结构和节点值。
     */
    static class SolutionisSubStructure {
        public static void main(String[] args) {
            TreeNode a = new TreeNode(1);
            TreeNode a1 = new TreeNode(0);
            TreeNode a2 = new TreeNode(1);
            TreeNode a3 = new TreeNode(-4);
            TreeNode a4 = new TreeNode(-3);
            a.left = a1;
            a.right = a2;
            a1.left = a3;
            a1.right = a4;
            TreeNode b = new TreeNode(1);
            TreeNode b1 = new TreeNode(-4);
            b.left = b1;

            new SolutionisSubStructure().isSubStructure(a, b);

        }

        public boolean isSubStructure(TreeNode A, TreeNode B) {
            if (B == null) {
                return false;
            } else if (A == null) {
                return false;
            }
            if (dfs(A, B)) {
                return true;
            }

            if (A.left != null && isSubStructure(A.left, B)) {
                return true;
            }
            return A.right != null && isSubStructure(A.right, B);
        }

        public boolean dfs(TreeNode A, TreeNode B) {
            if (A == null) {
                return false;
            }
            if (A.val == B.val) {
                boolean t = true;
                if (B.left != null) {
                    t = t && dfs(A.left, B.left);
                }
                if (B.right != null) {
                    t = t && dfs(A.right, B.right);
                }
                return t;
            }
            return false;
        }
    }

//    剑指 Offer 27. 二叉树的镜像
//    请完成一个函数，输入一个二叉树，该函数输出它的镜像。

    /**
     *
     */
    class Solution {
        public TreeNode mirrorTree(TreeNode root) {
            if (root == null) {
                return null;
            }
            TreeNode res = new TreeNode(root.val);
            dfs(res, root);
            return res;
        }

        public void dfs(TreeNode A, TreeNode B) {
            if (B.left != null) {
                A.right = new TreeNode(B.left.val);
                dfs(A.right, B.left);
            }
            if (B.right != null) {
                A.left = new TreeNode(B.right.val);
                dfs(A.left, B.right);
            }
        }
    }

}
