package com.walker.demo.leecode;


import java.util.*;

/**
 *
 */
public class LeeCodeTreeTrace {

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

    //    剑指 Offer 28. 对称的二叉树
//   请实现一个函数，用来判断一棵二叉树是不是对称的。如果一棵二叉树和它的镜像一样，那么它是对称的。
//例如，二叉树 [1,2,2,3,4,4,3] 是对称的。
    static class SolutionisSymmetric {
        public boolean isSymmetric(TreeNode root) {
            if (root == null) {
                return true;
            }
            return dfs(root.left, root.right);
        }

        public boolean dfs(TreeNode a, TreeNode b) {
            if (a == null && b == null) {
                return true;
            } else if (a != null && b != null && a.val == b.val) {
                return dfs(a.left, b.right) && dfs(a.right, b.left);
            } else {
                return false;
            }

        }
    }

    //    剑指 Offer 32 - I. 从上到下打印二叉树
//    从上到下打印出二叉树的每个节点，同一层的节点按照从左到右的顺序打印。
    static class SolutionlevelOrder {
        public int[] levelOrder(TreeNode root) {
            if (root == null) {
                return new int[0];
            }
            List<Integer> res = new ArrayList<>();
            List<TreeNode> cs = new ArrayList<>();
            cs.add(root);
            bfs(cs, res);
            int[] r = new int[res.size()];
            for (int i = 0; i < res.size(); i++) {
                r[i] = res.get(i);
            }
            return r;
        }

        public void bfs(List<TreeNode> cs, List<Integer> res) {
            int t = cs.size();
            if (t <= 0) {
                return;
            }
            for (int i = 0; i < t; i++) {
                TreeNode n = cs.remove(0);
                if (n != null) {
                    res.add(n.val);
                    cs.add(n.left);
                    cs.add(n.right);
                }
            }
            bfs(cs, res);
        }
    }

    //    剑指 Offer 32 - II. 从上到下打印二叉树 II
//    从上到下按层打印二叉树，同一层的节点按从左到右的顺序打印，每一层打印到一行。
    static class SolutionlevelOrder2 {
        public List<List<Integer>> levelOrder(TreeNode root) {
            if (root == null) {
                return new ArrayList<>();
            }
            List<List<Integer>> res = new ArrayList<>();
            List<TreeNode> cs = new ArrayList<>();
            cs.add(root);
            bfs(cs, res);
            return res;
        }

        public void bfs(List<TreeNode> cs, List<List<Integer>> res) {
            int t = cs.size();
            if (t <= 0) {
                return;
            }
            List<Integer> rest = new ArrayList<>();
            for (int i = 0; i < t; i++) {
                TreeNode n = cs.remove(0);
                if (n != null) {
                    rest.add(n.val);
                    if (n.left != null) {
                        cs.add(n.left);
                    }
                    if (n.right != null) {
                        cs.add(n.right);
                    }
                }
            }
            res.add(rest);
            bfs(cs, res);
        }
    }

    //    剑指 Offer 32 - III. 从上到下打印二叉树 III
//   请实现一个函数按照之字形顺序打印二叉树，即第一行按照从左到右的顺序打印，第二层按照从右到左的顺序打印，第三行再按照从左到右的顺序打印，其他行以此类推。
    static class SolutionlevelOrder3 {
        int kk = 0;

        public List<List<Integer>> levelOrder(TreeNode root) {
            if (root == null) {
                return new ArrayList<>();
            }
            List<List<Integer>> res = new ArrayList<>();
            List<TreeNode> cs = new ArrayList<>();
            cs.add(root);
            bfs(cs, res);
            return res;
        }

        public void bfs(List<TreeNode> cs, List<List<Integer>> res) {
            int t = cs.size();
            if (t <= 0) {
                return;
            }
            kk++;
            List<Integer> rest = new ArrayList<>();
            for (int i = 0; i < t; i++) {
                TreeNode n = cs.remove(0);
                if (n != null) {
                    rest.add(n.val);
                    if (n.left != null) {
                        cs.add(n.left);
                    }
                    if (n.right != null) {
                        cs.add(n.right);
                    }
                }
            }
            if (kk % 2 == 0) {
                Collections.reverse(rest);
            }
            res.add(rest);
            bfs(cs, res);
        }
    }

    //    剑指 Offer 27. 二叉树的镜像
//    请完成一个函数，输入一个二叉树，该函数输出它的镜像。
    class SolutionmirrorTree {
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

    class SolutionpathSum {
        public List<List<Integer>> pathSum(TreeNode root, int target) {
            if (root == null) {
                return new ArrayList<>();
            }
            List<List<Integer>> res = new ArrayList<>();
            List<Integer> route = new ArrayList<>();

            dfs(root, route, res, target);

            return res;
        }

        public void dfs(TreeNode node, List<Integer> route, List<List<Integer>> res, int target) {
            route.add(node.val);
            if (node.left == null && node.right == null) {
                // 叶子节点 和校验
                int r = 0;
                for (Integer integer : route) {
                    r += integer;
                }
                if (r == target) {
                    res.add(new ArrayList<>(route));
                }
            } else {
                if (node.left != null) {
                    dfs(node.left, route, res, target);
                }
                if (node.right != null) {
                    dfs(node.right, route, res, target);
                }
            }
            route.remove(route.size() - 1);
        }
    }


    public static class Node {
        int val;
        Node left;
        Node right;

        Node(int x) {
            val = x;
        }
    }

    //    剑指 Offer 36. 二叉搜索树与双向链表 what???
//    输入一棵二叉搜索树，将该二叉搜索树转换成一个排序的循环双向链表。要求不能创建任何新的节点，只能调整树中节点指针的指向。
    static class SolutiontreeToDoublyList {
        Node pre, head;

        public Node treeToDoublyList(Node root) {
            if (root == null) {
                return null;
            }
            dfs(root);
            head.left = pre;
            pre.right = head;
            return head;

        }

        public void dfs(Node a) {
            if (a.left != null) {
                dfs(a.left);
            }
            if (pre == null) {
                head = a;
            } else {
                pre.right = a;
            }
            a.left = pre;
            pre = a;

            if (a.right != null) {
                dfs(a.right);
            }
        }
    }

//    剑指 Offer 37. 序列化二叉树
//    请实现两个函数，分别用来序列化和反序列化二叉树。
//
//    你需要设计一个算法来实现二叉树的序列化与反序列化。这里不限定你的序列 / 反序列化算法执行逻辑，你只需要保证一个二叉树可以被序列化为一个字符串并且将这个字符串反序列化为原始的树结构。

    public static class Codec {
        List<String> list = new LinkedList<>();

        public static void main(String[] args) {
            TreeNode a = new TreeNode(1);
            TreeNode a1 = new TreeNode(2);
            TreeNode a2 = new TreeNode(3);
            TreeNode a3 = new TreeNode(4);
            TreeNode a4 = new TreeNode(5);
            a.left = a1;
            a.right = a2;
            a1.left = a3;
            a1.right = a4;
            new Codec().deserialize(new Codec().serialize(a));
        }

        // Encodes a tree to a single string.
        public String serialize(TreeNode root) {
            if (root == null) {
                return "";
            }
            serialize(root, "T");
            // HEAD,T_1,TL_2,TLL_4,TLR_5,TR_3
            String res = "HEAD";
            for (String s : list) {
                res += "," + s;
            }
            return res;
        }

        public void serialize(TreeNode node, String ibit) {
            list.add(ibit + "_" + node.val);
            if (node.left != null) {
                serialize(node.left, ibit + "L");
            }
            if (node.right != null) {
                serialize(node.right, ibit + "R");
            }
        }

        // Decodes your encoded data to tree.
        public TreeNode deserialize(String data) {
            if (data == null || data.length() == 0) {
                return null;
            }
//            HEAD,T_1,TL_2,TLL_4,TLR_5,TR_3
            Map<String, TreeNode> line = new HashMap<>();
            for (String s : data.split(", *")) {
                if (s.equals("HEAD")) {
                    continue;
                }
                String[] ss = s.split("_");
                String route = ss[0];
                Integer val = Integer.valueOf(ss[1]);
                line.put(route, new TreeNode(val));
            }
            TreeNode res = null;
            for (String route : line.keySet()) {
                TreeNode cur = line.get(route);
                if (route.equals("T")) {
                    // head 无parent
                    res = cur;
                } else {
                    String routeParent = route.substring(0, route.length() - 1);
                    TreeNode p = line.get(routeParent);
                    if (p != null) {
                        if (route.charAt(route.length() - 1) == 'L') {
                            p.left = cur;
                        } else {
                            p.right = cur;
                        }
                    }
                }
            }
            return res;
        }

    }

    //    输入一个字符串，打印出该字符串中字符的所有排列。
//    你可以以任意顺序返回这个字符串数组，但里面不能有重复元素。
//    输入：s = "abc"
//    输出：["abc","acb","bac","bca","cab","cba"]
    static class Solutionpermutation {
        List<Character> list = new LinkedList<>();

        public String[] permutation(String s) {
            if (s == null || s.length() == 0) {
                return new String[0];
            }

            char[] cs = s.toCharArray();
            for (char c : cs) {
                list.add(c);
            }
            //类似 古典概率数学问题的 任意组合
            List<String> res = fs(list.size());

//                //模拟概率命中方案 超时
//                int i = 0;
//                while (i++ < 100){
//                    String s1 = "";
//                    List<Character> t = new LinkedList<>(list);
//                    while (t.size() > 0){
//                        s1 += t.remove(Math.floor(Math.random() * t.size()));
//                    }
//                    res.add(s1);
//                }
            res = new ArrayList<>(new HashSet<>(res));

            return res.toArray(new String[0]);
        }

        private List<String> fs(int i) {
            char ci = list.get(i - 1);

            if (i == 1) {
                return Arrays.asList("" + ci);
            }

            List<String> fi = fs(i - 1);

            // 增加第i个字符 则把以前的字符组合集合每个再按次序组合追加一个字符
            List<String> fn = new LinkedList<>();
            for (String s : fi) {
                for (int j = 0; j < s.length(); j++) {
                    fn.add(s.substring(0, j) + ci + s.substring(j));
                }
                fn.add(s + ci);
            }
            return fn;
        }
    }


//    剑指 Offer 54. 二叉搜索树的第 k 大节点
//    给定一棵二叉搜索树，请找出其中第 k 大的节点的值。
//    输入: root = [5,3,6,2,4,null,null,1], k = 3
//            5
//            / \
//            3   6
//            / \
//            2   4
//            /
//            1
//    输出: 4
//

    static class SolutionkthLargest {
        int k = -2;
        Integer res = null;

        public static void main(String[] args) {
            TreeNode a = new TreeNode(3);
            TreeNode a1 = new TreeNode(1);
            TreeNode a2 = new TreeNode(4);
            TreeNode a4 = new TreeNode(2);
            a.left = a1;
            a.right = a2;
            a1.right = a4;
            new SolutionkthLargest().kthLargest(a, 1);
        }

        public int kthLargest(TreeNode root, int k) {
            if (root == null) {
                return -1;
            }
            this.k = k;
            //倒中序遍历第n个元素？
            dfs(root);

            return res;
        }

        public boolean dfs(TreeNode a) {
            if (a.right != null) {
                if (dfs(a.right)) {
                    return true;
                }
            }
            k--;
            if (k <= 0) {
                res = a.val;
                return true;
            }

            if (a.left != null) {
                return dfs(a.left);
            }
            return false;
        }
    }
//    剑指 Offer 55 - I. 二叉树的深度
//    输入一棵二叉树的根节点，求该树的深度。从根节点到叶节点依次经过的节点（含根、叶节点）形成树的一条路径，最长路径的长度为树的深度。

    static class SolutionmaxDepth {
        public int maxDepth(TreeNode root) {
            if (root == null) {
                return 0;
            }
            return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
        }
    }
}
