package com.walker.demo.leecode;


import java.util.LinkedList;
import java.util.Objects;

/**
 *
 */
public class LeeCodeSimilute {

//   剑指 Offer 31. 栈的压入、弹出序列
//    输入两个整数序列，第一个序列表示栈的压入顺序，请判断第二个序列是否为该栈的弹出顺序。假设压入栈的所有数字均不相等。例如，序列 {1,2,3,4,5} 是某栈的压栈序列，序列 {4,5,3,2,1} 是该压栈序列对应的一个弹出序列，但 {4,3,5,1,2} 就不可能是该压栈序列的弹出序列。
//输入：pushed = [1,2,3,4,5], popped = [4,5,3,2,1]
//输出：true
//解释：我们可以按以下顺序执行：
//push(1), push(2), push(3), push(4), pop() -> 4,
//push(5), pop() -> 5, pop() -> 3, pop() -> 2, pop() -> 1
    class SolutionvalidateStackSequences {
        public boolean validateStackSequences(int[] pushed, int[] popped) {
            int i = 0;
            int j = 0;
            LinkedList<Integer> stack = new LinkedList<Integer>();

            while (true) {
                if (i == pushed.length && j == popped.length) {
                    return true;
                }
                if (Objects.equals(popped[j], stack.peek())) {
                    stack.pop();
                    j++;
                } else if (i < pushed.length) {
                    stack.push(pushed[i++]);
                } else {
                    return false;
                }
            }
        }

    }

//    剑指 Offer 29. 顺时针打印矩阵
//    输入一个矩阵，按照从外向里以顺时针的顺序依次打印出每一个数字。
//    输入：matrix = [
//     [1,2,3]
//    ,[4,5,6]
//    ,[7,8,9]]
//    输出：[1,2,3,6,9,8,7,4,5]

    class SolutionspiralOrder {
        public int[] spiralOrder(int[][] matrix) {
            if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
                return new int[0];
            }

            int left = 0, right = matrix[0].length - 1, top = 0, bottom = matrix.length - 1, x = 0;
            int[] res = new int[(right + 1) * (bottom + 1)];
            while (true) {
                for (int i = left; i <= right; i++) res[x++] = matrix[top][i]; // left to right
                if (++top > bottom) break;
                for (int i = top; i <= bottom; i++) res[x++] = matrix[i][right]; // top to bottom
                if (left > --right) break;
                for (int i = right; i >= left; i--) res[x++] = matrix[bottom][i]; // right to left
                if (top > --bottom) break;
                for (int i = bottom; i >= top; i--) res[x++] = matrix[i][left]; // bottom to top
                if (++left > right) break;
            }
            return res;
//            int res[] = new int[matrix.length * matrix[0].length];
//            int i = 0, j = 0;
//            int r = 0;
//            int tx,ty;
//            int lastI, lastJ;
//            while (true){
//                lastI = i;
//                lastJ = j;
//
//                // 向右
//                ty = i;
//                tx = matrix[i].length - 1 - lastJ;
//                while (j < tx){
//                    res[r++] = matrix[i][j++];
//                }
//                // 向下
//                tx = j;
//                ty = matrix.length - 1 - lastI;
//                while (i < ty){
//                    res[r++] = matrix[i++][j];
//                }
//                // 向左
//                tx = lastJ;
//                ty = i;
//                while (j > tx){
//                    res[r++] = matrix[i][j--];
//                }
//                // 向上
//                tx = i;
//                ty = lastI + 1;
//                while (i > ty){
//                    res[r++] = matrix[i--][j];
//                }
//                j++;
//                if(r >= res.length - 1){
//                    return res;
//                }
//            }
        }
    }


}
