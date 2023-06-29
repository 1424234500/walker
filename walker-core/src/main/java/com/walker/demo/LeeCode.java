package com.walker.demo;

import java.util.*;

/**
 * 数组遍历，四个线程协作处理
 */
public class LeeCode {
    public static void main(String[] args) {

    }
//剑指 Offer 05. 替换空格
    public String replaceSpace(String s) {
        return s.replaceAll(" ", "%20");
    }


//    剑指 Offer 06. 从尾到头打印链表
    public static class ListNode {
         int val;
         ListNode next;
         ListNode(int x) { val = x; }
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

    public static class CQueue {
        LinkedList<Integer> A= new LinkedList<Integer>();
        LinkedList<Integer> B= new LinkedList<Integer>();
        public CQueue() {

        }
        public void appendTail(int value) {
            A.addLast(value);
        }
        public int deleteHead() {
            // b存a的倒序 则b出栈即删头,
            if(!B.isEmpty()) return B.removeLast();
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
            for(char c : s.toCharArray()) {
                if(c >= '0' && c <= '9') t = 'd';
                else if(c == '+' || c == '-') t = 's';
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


}
