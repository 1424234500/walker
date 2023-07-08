package com.walker.demo.leecode;


import java.util.*;

/**
 */
public class LeeCodeFind {


//    剑指 Offer 03. 数组中重复的数字
//在一个长度为 n 的数组 nums 里的所有数字都在 0～n-1 的范围内。数组中某些数字是重复的，但不知道有几个数字重复了，也不知道每个数字重复了几次。请找出数组中任意一个重复的数字。
class SolutionfindRepeatNumber {
    public int findRepeatNumber(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if(map.containsKey(nums[i])){
                return nums[i];
            }
            map.put(nums[i], i);
        }
        return -1;
    }
}

//    剑指 Offer 04. 二维数组中的查找
//    在一个 n * m 的二维数组中，每一行都按照从左到右 非递减 的顺序排序，
//    每一列都按照从上到下 非递减 的顺序排序。请完成一个高效的函数，
//    输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。
static class SolutionfindNumberIn2DArray {
    public boolean findNumberIn2DArray(int[][] matrix, int target) {
        // 判断二维数组是否为空！???
        int i = 0, j = 0;
        while (matrix != null && i < matrix.length) {
            if(matrix[i] == null || j >= matrix[i].length){
                i++;
                j = 0;
            }else if (matrix[i][j] == target) {
                return true;
            } else if (matrix[i][j] > target) {
                // 从下一行查找
                i++;
                j = 0;//Math.max(0, j - 1);
            } else if (matrix[i][j] < target) {
                j++;
            }
        }
        return false;
    }
    // 从右上角看是一棵树
    public boolean findNumberIn2DArrayTree(int[][] matrix, int target) {
        int i = matrix.length - 1, j = 0;
        while(i >= 0 && j < matrix[0].length) {
            if(matrix[i][j] > target) i--;
            else if(matrix[i][j] < target) j++;
            else return true;
        }
        return false;
    }
    public static void main(String[] args) {
        new SolutionfindNumberIn2DArray().findNumberIn2DArray(new int[][]{
                  {1,4,7,11,15}
                , {2,5,8,12,19}
                , {3,6,9,16,22}
                , {10,13,14,17,24}
                , {18,21,23,26,30}
        }, 5);
        new SolutionfindNumberIn2DArray().findNumberIn2DArray(new int[][]{
                  {1,2,3,4,5}
                , {6,7,8,9,10}
                , {11,12,13,14,15}
                , {16,17,18,19,20}
                , {21,22,23,24,25}
        }, 19);
        new SolutionfindNumberIn2DArray().findNumberIn2DArray(new int[][]{
                {3,3,8,13,13,18}
                , {4,5,11,13,18,20}
                , {9,9,14,15,23,23}
                , {13,18,22,22,25,27}
                , {18,22,23,28,30,33}
                , {21,25,28,30,35,35}
                , {24,25,33,36,37,40}
        }, 21);
    }
}

//剑指 Offer 11. 旋转数组的最小数字
//    把一个数组最开始的若干个元素搬到数组的末尾，我们称之为数组的旋转。
//
//    给你一个可能存在 重复 元素值的数组 numbers ，它原来是一个升序排列的数组，并按上述情形进行了一次旋转。请返回旋转数组的最小元素。例如，数组 [3,4,5,1,2] 为 [1,2,3,4,5] 的一次旋转，该数组的最小值为 1。  
class SolutionminArray {
    public int minArray(int[] numbers) {
        for (int i = 0; i < numbers.length; i++) {
            if(i + 1 <  numbers.length){
                if(numbers[i + 1] < numbers[i]){
                    return numbers[i + 1];
                }
            }
        }
        return numbers[0];
    }
    public int minArray2(int[] numbers) {
        int i = 0, j = numbers.length - 1;
        while (i < j) {
            int m = (i + j) / 2;
            //右区间
            if (numbers[m] > numbers[j]) i = m + 1;
            else if (numbers[m] < numbers[j]) j = m;
            else j--;
        }
        return numbers[i];
    }
}
}
