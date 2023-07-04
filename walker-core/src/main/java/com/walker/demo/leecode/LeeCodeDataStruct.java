package com.walker.demo.leecode;

import java.util.*;

/**
 * leecode.1 数据结构篇
 */
public class LeeCodeDataStruct {
    //剑指 Offer 05. 替换空格
    public String replaceSpace(String s) {
        return s.replaceAll(" ", "%20");
    }


    //    剑指 Offer 06. 从尾到头打印链表
    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    public int[] reversePrint(ListNode head) {
        List<Integer> list = new ArrayList<>();
        reverse(list, head);
        int[] res = new int[list.size()];
        for (int i = list.size() - 1; i >= 0; i--) {
            res[i] = list.get(i);
        }
        return res;
    }

    private void reverse(List<Integer> list, ListNode head) {
        if (head == null) {
            return;
        }
        if (head.next != null) {
            reverse(list, head.next);
        }
        list.add(head.val);
    }

    //    剑指 Offer 24. 反转链表
//    定义一个函数，输入一个链表的头节点，反转该链表并输出反转后链表的头节点。
    public ListNode reverseList(ListNode head) {
        ListNode res = null;
        ListNode n;
        while (head != null) {
            n = head.next;
            head.next = res;
            res = head;
            head = n;
        }

        return res;
    }

    //剑指 Offer 09. 用两个栈实现队列
//用两个栈实现一个队列。队列的声明如下，请实现它的两个函数 appendTail 和 deleteHead ，分别完成在队列尾部插入整数和在队列头部删除整数的功能。(若队列中没有元素，deleteHead 操作返回 -1 )
    public static class CQueue {
        LinkedList<Integer> A = new LinkedList<Integer>();
        LinkedList<Integer> B = new LinkedList<Integer>();

        public CQueue() {

        }

        public void appendTail(int value) {
            A.addLast(value);
        }

        public int deleteHead() {
            // b存a的倒序 则b出栈即删头,
            if (!B.isEmpty()) return B.removeLast();
            // 若b没了 则 把a全倒序挪动到b
            // 但若a也没了 则没了
            if (A.isEmpty()) return -1;

            // 若b没了 则 把a全倒序挪动到b
            while (!A.isEmpty())
                B.addLast(A.removeLast());
            return B.removeLast();
        }
    }

    // 数字识别 编译原理实现
//    请实现一个函数用来判断字符串是否表示数值（包括整数和小数）。
    // https://leetcode.cn/leetbook/read/illustration-of-algorithm/5d6vi6/
    // todo 数字识别 状态机模式实现
    public static class SolutionIsNumber {

        class MapBuilder<KEY, VALUE> {
            Map<KEY, VALUE> map;

            public MapBuilder() {
                map = new HashMap<>();
            }

            public MapBuilder put(KEY key, VALUE value) {
                map.put(key, value);
                return this;
            }

            public Map<KEY, VALUE> build() {
                return map;
            }

        }

        public boolean isNumber(String s) {
            Map[] states = {
                    new MapBuilder().put(' ', 0).put('s', 1).put('d', 2).put('.', 4).build(), // 0.
                    new MapBuilder().put('d', 2).put('.', 4).build(),                           // 1.
                    new MapBuilder().put('d', 2).put('.', 3).put('e', 5).put(' ', 8).build(), // 2.
                    new MapBuilder().put('d', 3).put('e', 5).put(' ', 8).build(),              // 3.
                    new MapBuilder().put('d', 3).build(),                                        // 4.
                    new MapBuilder().put('s', 6).put('d', 7).build(),                           // 5.
                    new MapBuilder().put('d', 7).build(),                                        // 6.
                    new MapBuilder().put('d', 7).put(' ', 8).build(),                           // 7.
                    new MapBuilder().put(' ', 8).build()                                         // 8.
            };
            int p = 0;
            char t;
            for (char c : s.toCharArray()) {
                if (c >= '0' && c <= '9') t = 'd';
                else if (c == '+' || c == '-') t = 's';
                else if (c == 'e' || c == 'E') t = 'e';
                else if (c == '.' || c == ' ') t = c;
                else t = '?';
                if (!states[p].containsKey(t)) return false;
                p = (int) states[p].get(t);
            }
            return p == 2 || p == 3 || p == 7 || p == 8;
        }
    }

    //剑指 Offer 30. 包含 min 函数的栈
//    定义栈的数据结构，请在该类型中实现一个能够得到栈的最小元素的 min 函数在该栈中，调用 min、push 及 pop 的时间复杂度都是 O(1)。
    static class MinStack {
        LinkedList<Integer> stack = new LinkedList<>();
        LinkedList<Integer> stackMin = new LinkedList<>();

        /**
         * initialize your data structure here.
         */
        public MinStack() {

        }

        public void push(int x) {
            stack.push(x);
            if (!stackMin.isEmpty()) {
                x = Math.min(stackMin.peek(), x);
            }
            //补位填充充数
            stackMin.push(x);
        }

        public void pop() {
            stack.pop();
            stackMin.pop();
        }

        public int top() {
            return stack.peek();
        }

        public int min() {
            return stackMin.peek();
        }
    }

    //    剑指 Offer 35. 复杂链表的复制
//    请实现 copyRandomList 函数，复制一个复杂链表。在复杂链表中，每个节点除了有一个 next 指针指向下一个节点，还有一个 random 指针指向链表中的任意节点或者 null。
    static class Node {
        int val;
        Node next;
        Node random;

        public Node(int val) {
            this.val = val;
            this.next = null;
            this.random = null;
        }
    }

    class SolutionNodeRandomCopy {
        public Node copyRandomList(Node head) {
            if (head == null) {
                return null;
            }
            Map<Node, Node> link = new HashMap<>();
            Node p = head;
            while (p != null) {
                link.put(p, new Node(p.val));
                p = p.next;
            }
            p = head;
            while (p != null) {
                Node r = link.get(p);
                r.random = link.get(p.random);
                r.next = link.get(p.next);
                p = p.next;
            }
            return link.get(head);
        }
    }

    //剑指 Offer 58 - II. 左旋转字符串
//    字符串的左旋转操作是把字符串前面的若干个字符转移到字符串的尾部。请定义一个函数实现字符串左旋转操作的功能。比如，输入字符串"abcdefg"和数字2，该函数将返回左旋转两位得到的结果"cdefgab"。
    static class SolutionreverseLeftWords {
        public String reverseLeftWords(String s, int n) {
            char arr[] = s.toCharArray();
            char res[] = new char[s.length()];
            if (n > 0 && n < arr.length) {
                for (int i = 0; i < arr.length; i++) {
                    res[i] = arr[(i + n) % s.length()];
                }


            }
            return new String(res);
        }
    }

    //    剑指 Offer 59 - I. 滑动窗口的最大值
//给定一个数组 nums 和滑动窗口的大小 k，请找出所有滑动窗口里的最大值。
    static class SolutionmaxSlidingWindow {
        public int[] maxSlidingWindow(int[] nums, int k) {
            if (nums == null || nums.length <= 0) {
                return new int[]{};
            }
            int[] res = new int[nums.length - k + 1];
            Deque<Integer> window = new LinkedList<>();
            for (int i = 0; i < nums.length; i++) {
                if (i < k) {
                    //低于now的全部删除
                    while (!window.isEmpty() && window.peekLast() < nums[i]) {
                        window.removeLast();
                    }
                    window.add(nums[i]);
                    res[0] = window.peekFirst();
                } else {
                    // 最大值为窗口第一个
                    if (window.peekFirst() == nums[i - k]) {
                        window.removeFirst();
                    }
                    //低于now的全部删除
                    while (!window.isEmpty() && window.peekLast() < nums[i]) {
                        window.removeLast();
                    }
                    window.add(nums[i]);

                    res[1 + i - k] = window.peekFirst();
                }
            }
            return res;
        }

        public static void main(String[] args) {
            System.out.println(new SolutionmaxSlidingWindow().maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3));
        }
    }

//    剑指 Offer 59 - II. 队列的最大值

    static class MaxQueue {
        LinkedList<Integer> queue = new LinkedList<>();
        LinkedList<Integer> deque = new LinkedList<>();

        public MaxQueue() {

        }

        public int max_value() {
            return deque.isEmpty() ? -1 : deque.peekFirst();
        }

        public void push_back(int value) {
            queue.add(value);
//            queue.offer(value);

            //低于now的全部删除
            while (!deque.isEmpty() && deque.peekLast() < value) {
                deque.removeLast();
//                deque.pollLast();
            }
            // 追加到尾部
            deque.addLast(value);
//            deque.offerLast(value);
        }

        public int pop_front() {
            if (queue.isEmpty()) {
                return -1;
            }
            // 最大值为窗口第一个 deque.peekFirst(
            if (deque.peekFirst().equals(queue.peekFirst())) {
                deque.removeFirst();
//                deque.pollFirst();
            }
            return queue.removeFirst();
//            return queue.poll();
        }
    }

    //    剑指 Offer 67. 把字符串转换成整数
    static class SolutionstrToInt {
        public int strToInt(String str) {
            str = str.trim();
            int i = 1;
            int res = 0;
            int len = 0;
            for (char c : str.toCharArray()) {
                if (c == '-' && len == 0) {
                    i = -1;
                    len++;
                } else if (c == '+' && len == 0) {
                    i = 1;
                    len++;
                } else if ('0' <= c && c <= '9') {
                    len++;
                    long resN = 1L * res * 10 + (c - '0');
                    if (resN > Integer.MAX_VALUE) {
                        return i == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                    }
                    res = (int) resN;
                } else if (len > 0) {
                    return i * res;
                } else {
                    return 0;
                }
            }
            return i * res;
        }
    }


}
