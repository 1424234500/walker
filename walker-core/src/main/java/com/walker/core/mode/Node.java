package com.walker.core.mode;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * 数据结构  节点
 *
 * 链表	双向	prev	--line--	this	--line--	next
 *
 * 树				this
 * 			 --line--	 --line--
 * 			left 		    right
 *
 * 图	prev	--line--	this	--line--	next
 * 			 		--line--	 --line--
 * 					left 		    right
 *
 *
 * 每个节点只管它的子节点
 * 父操作交由父节点
 * 
 */
public class Node<T> {

	/**
	 * 值
	 */
	T item;

	/**
	 * 双向链表
	 */
	Node<T> prev;
	Node<T> next;

	/**
	 * 树
	 */
	Node<T> left;
	Node<T> right;

	/**
	 * 图
	 */
	LinkedHashMap<Node<T>, Node<T>> nodes;

	public Node(){
		nodes = new LinkedHashMap<>();
	}

	public Node(Node<T> prev, T item, Node<T> next) {
		this();
		init(prev, item, next);
	}
	public Node<T> init(Node<T> prev, T item, Node<T> next) {
		this.item = item;
		this.next = next;
		this.prev = prev;

		this.left = this.prev;
		this.right = this.next;

		this.nodes.put(prev, prev);
		this.nodes.put(next, next);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Node<?> node = (Node<?>) o;
		return item.equals(node.item);
	}

	@Override
	public int hashCode() {
		return Objects.hash(item);
	}
}

