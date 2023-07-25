package com.walker.demo.leecode;


/**
 *
 */
public class LeeCodeDoubleP {
//    剑指 Offer 18. 删除链表的节点
//    给定单向链表的头指针和一个要删除的节点的值，定义一个函数删除该节点。
//
//    返回删除后的链表的头节点。
//
//    注意：此题对比原题有改动
//
//    示例 1:
//
//    输入: head = [4,5,1,9], val = 5
//    输出: [4,1,9]
//    解释: 给定你链表中值为 5 的第二个节点，那么在调用了你的函数之后，该链表应变为 4 -> 1 -> 9.

    //    剑指 Offer 21. 调整数组顺序使奇数位于偶数前面
//    输入一个整数数组，实现一个函数来调整该数组中数字的顺序，使得所有奇数在数组的前半部分，所有偶数在数组的后半部分。
//    输入：nums = [1,2,3,4]
//    输出：[1,3,2,4]
//    注：[3,1,2,4] 也是正确的答案之一。
    static class Solutionexchange {
        public static void main(String[] args) {
            new Solutionexchange().exchange(new int[]{1, 2, 3, 4});
        }

        public int[] exchange(int[] nums) {
//        LinkedList<Integer> q = new LinkedList<>();
//        for (int num : nums) {
//            if((num & 1) == 1){
//                q.addFirst(num);
//            }else{
//                q.add(num);
//            }
//        }
//        for (int i = 0; i < q.size(); i++) {
//            nums[i] = q.get(i);
//        }
//        return nums;
            int l = 0;
            int r = nums.length - 1;
            while (l < r) {
                if ((nums[l] & 1) == 0) {
                    // 左边偶数
                    if ((nums[r] & 1) == 1) {
                        // 右边奇数
                        swap(nums, l, r);
                        l++;
                        r--;
                    } else {
                        // 右边偶数
                        r--;
                    }
                } else {
                    //左边奇数
                    l++;
                }
            }
            return nums;
        }

        private void swap(int[] nums, int l, int r) {
            int i = nums[l];
            nums[l] = nums[r];
            nums[r] = i;
        }
    }

    class SolutiondeleteNode {
        public ListNode deleteNode(ListNode head, int val) {
//            ListNode p = head;
//            ListNode pre = null;
//            while (p != null){
//                if(p.val == val){
//                    if(pre == null){
//                        // 删除头节点
//                        head = p.next;
//                    }else {
//                        pre.next = p.next;
//                    }
//                }else{
//                    pre = p;
//                }
//                p = p.next;
//            }
//
//            return head;
            // 使用虚拟头节点方式避免删除head问题差异 简化
            ListNode res = new ListNode(0);
            res.next = head;

            ListNode pre = res;
            while (head != null) {
                if (head.val == val) {
                    pre.next = head.next;
                } else {
                    pre = head;
                }
                head = head.next;
            }

            return res.next;
        }
    }


//    剑指 Offer 22. 链表中倒数第 k 个节点
//    输入一个链表，输出该链表中倒数第k个节点。为了符合大多数人的习惯，本题从1开始计数，即链表的尾节点是倒数第1个节点。
//
//    例如，一个链表有 6 个节点，从头节点开始，它们的值依次是 1、2、3、4、5、6。这个链表的倒数第 3 个节点是值为 4 的节点。
//    给定一个链表: 1->2->3->4->5, 和 k = 2.
//
//    返回链表 4->5.

    class SolutiongetKthFromEnd {
        public ListNode getKthFromEnd(ListNode head, int k) {
////            遍历两次
//            ListNode p = head;
//            int lv = 0;
//            while (p != null){
//                lv++;
//                p = p.next;
//            }
//            lv = lv - k;
//            p = head;
//            while (lv-- > 0){
//                p = p.next;
//            }
//            return p;

//            快慢指针问题
            ListNode p = head;
            ListNode p1 = head;
            while (p != null) {
                k--;
                p = p.next;
                if (k < 0) {
                    p1 = p1.next;
                }
            }
            return p1;
        }

    }

    //    剑指 Offer 25. 合并两个排序的链表
//    输入两个递增排序的链表，合并这两个链表并使新链表中的节点仍然是递增排序的。
//    示例1：
//    输入：1->2->4, 1->3->4
//    输出：1->1->2->3->4->4
    static class SolutionmergeTwoLists {
        public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
            ListNode res = new ListNode(0);
            ListNode p = res;
            while (l1 != null && l2 != null) {
                if (l1.val <= l2.val) {
                    p.next = l1;
                    l1 = l1.next;
                } else {
                    p.next = l2;
                    l2 = l2.next;
                }
                p = p.next;
            }
            if (l1 == null) {
                p.next = l2;
            } else {
                p.next = l1;
            }

            return res.next;
        }
    }

    //    剑指 Offer 52. 两个链表的第一个公共节点
//    输入两个链表，找出它们的第一个公共节点。
    class SolutiongetIntersectionNode {
        ListNode getIntersectionNode(ListNode headA, ListNode headB) {
            ListNode A = headA, B = headB;
            while (A != B) {
                A = A != null ? A.next : headB;
                B = B != null ? B.next : headA;
            }
            return A;
        }
    }

    //    剑指 Offer 57. 和为 s 的两个数字
//    输入一个递增排序的数组和一个数字s，在数组中查找两个数，使得它们的和正好是s。如果有多对数字的和等于s，则输出任意一对即可。
//    示例 1：
//    输入：nums = [2,7,11,15], target = 9
//    输出：[2,7] 或者 [7,2]
    class SolutiontwoSum {
        public int[] twoSum(int[] nums, int target) {
            int right = nums.length - 1;
            for (; right >= 0; right--) {
                if (nums[right] < target) {
                    break;
                }
            }
            while (right > 0) {
                for (int i = 0; i < right; i++) {
                    if (nums[i] + nums[right] > target) {
                        break;
                    } else if (nums[i] + nums[right] == target) {
                        return new int[]{nums[i], nums[right]};
                    }
                }
                right--;
            }
            return null;
        }
    }

    //    剑指 Offer 58 - I. 翻转单词顺序
//    输入一个英文句子，翻转句子中单词的顺序，但单词内字符的顺序不变。为简单起见，标点符号和普通字母一样处理。例如输入字符串"I am a student. "，则输出"student. a am I"。
//    输入: "the sky is blue"
//    输出: "blue is sky the"
    class Solution {
        public String reverseWords(String s) {
            s = s.replaceAll("  +", " ");
            int i = 0, j = 0;
            String res = "";
            while (j < s.length()) {
                if (s.charAt(j) == ' ') {
                    res = s.substring(i, j) + " " + res;
                    i = j + 1;
                }
                j++;
                if (j == s.length() && j > i) {
                    res = s.substring(i, j) + " " + res;
                }
            }
            return res.trim();
        }
    }
}
