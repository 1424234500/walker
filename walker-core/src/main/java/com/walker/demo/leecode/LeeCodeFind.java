package com.walker.demo.leecode;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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


    // 剑指 Offer 50. 第一个只出现一次的字符
    // 在字符串 s 中找出第一个只出现一次的字符。如果没有，返回一个单空格。 s 只包含小写字母。
    static class SolutionfirstUniqChar {
        public char firstUniqChar(String s) {
            int[] map = new int['z' - 'a' + 1];
            for (int i = 0; i < map.length; i++) {
                map[i] = Integer.MAX_VALUE;
            }
            // 第一个位置 意味着是 'a' 里面的值为 s 中的坐标
            for (int i = 0; i < s.length(); i++) {
                int id = s.charAt(i) - 'a';
                if (map[id] == Integer.MAX_VALUE - 1) {
                    // 已经标记重复了
                } else if (map[id] == Integer.MAX_VALUE) {
                    // 第一次出现 记录
                    map[id] = i;
                } else if (map[id] >= 0) {
                    // 第二次出现 标记重复
                    map[id] = Integer.MAX_VALUE - 1;
                }
            }
            // 取出最小的i值
            Arrays.sort(map);
            return map[0] >= Integer.MAX_VALUE - 1 ? ' ' : s.charAt(map[0]);
        }
    }

    //    剑指 Offer 53 - I. 在排序数组中查找数字 I
//    统计一个数字在排序数组中出现的次数。
//    输入: nums = [5,7,7,8,8,10], target = 8
//    输出: 2
    static class Solutionsearch {
        public static void main(String[] args) {
            new Solutionsearch().search(new int[]{5, 7, 7, 8, 8, 10}, 8);
        }

        public int search(int[] nums, int target) {
            // 折半查找 比t小的 比t大的 ?
            // 简单做法 折半查找n 查到之后 前后遍历(前后折半遍历)
            int i = search(nums, 0, nums.length - 1, target);
            if (i < 0) {
                return 0;
            }
            int res = 0;
            for (int j = i; j < nums.length; j++) {
                if (nums[j] == target) {
                    res++;
                }
            }
            for (int j = i - 1; j >= 0; j--) {
                if (nums[j] == target) {
                    res++;
                }
            }
            return res;
        }

        public int search(int[] nums, int left, int right, int target) {
            if (left > right || right >= nums.length || left < 0) {
                return -1;
            }
            int n = (left + right) / 2;
            if (nums[n] == target) {
                return n;
            }
            if (left == right) {
                return -1;
            }
            if (nums[nums.length - 1] > nums[0]) {
                if (nums[n] < target) {
                    return search(nums, n + 1, right, target);
                } else {
                    return search(nums, left, n - 1, target);
                }
            } else {
                if (nums[n] > target) {
                    return search(nums, n + 1, right, target);
                } else {
                    return search(nums, left, n - 1, target);
                }
            }
        }

    }


    //    剑指 Offer 53 - II. 0～n-1 中缺失的数字
//    一个长度为n-1的递增排序数组中的所有数字都是唯一的，并且每个数字都在范围0～n-1之内。在范围0～n-1内的n个数字中有且只有一个数字不在该数组中，请找出这个数字。
//    输入: [0,1,2,3,4,5,6,7,9]
//    输出: 8
    class SolutionmissingNumber {
        public int missingNumber(int[] nums) {
            // i 位置必须为i 否则异常
            int i = 0, j = nums.length - 1;
            while (i <= j) {
                int m = (i + j) / 2;
                // 中值为i则 右边区间
                if (nums[m] == m) i = m + 1;
                else j = m - 1;
            }
            return i;
        }


    }
}
