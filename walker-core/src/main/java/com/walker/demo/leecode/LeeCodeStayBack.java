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
        Map<String, Boolean> map = new HashMap<>();
        String word;
        char[][] board;
        LinkedList<Character> route = new LinkedList<>();

        public static void main(String[] args) {
            new Solutionexist().exist(new char[][]{
                    new char[]{'A', 'B', 'C', 'E'}
                    , new char[]{'S', 'F', 'C', 'S'}
                    , new char[]{'A', 'D', 'E', 'E'}
            }, "ABCCED");
        }

        public boolean exist(char[][] board, String word) {
            this.board = board;
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


            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    route.clear();
                    map.clear();
                    if (go(i, j)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean go(int i, int j) {
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

            if (go(i, j + 1)) {
                return true;
            }
            if (go(i + 1, j)) {
                return true;
            }
            if (go(i, j - 1)) {
                return true;
            }
            if (go(i - 1, j)) {
                return true;
            }
            route.removeLast();
            map.remove(i + "_" + j);
            return false;
        }
    }


}
