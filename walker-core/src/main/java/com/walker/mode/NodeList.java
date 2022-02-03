package com.walker.mode;


import java.util.*;

/**
 * 数据结构  双向权重链表
 *
 */
public class NodeList<E, WEIGHT> {
	Map<E, Map<E, WEIGHT>> lines;

	public WEIGHT setLineN(int f, int t, WEIGHT defaultWeight) {
		if (f < 0 || f > size || t < 0 || t > size || f >= t) {
			throw new NoSuchElementException("args err " + f + " " + t);
		}
		return setLine(this.get(f), this.get(f), defaultWeight);
	}
	/**
	 * 设置节点之间的距离
	 * @param from
	 * @param to
	 * @param weight null则删除
	 */
	public WEIGHT setLine(E from, E to, WEIGHT weight){
		WEIGHT res = null;

		checkNodeNull(from);
		checkNodeNull(to);

		Map<E, WEIGHT> tos = lines.get(from);
		if(tos == null){
			tos = new LinkedHashMap<>();
			lines.put(from, tos);
		}
		if(weight == null){
			res = tos.remove(to);
			if(tos.size() <= 0){
				lines.remove(from);
			}
		}else {
			tos.put(to, weight);
		}
		return res;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("NodeList size:" + size);
		sb.append(",");
		if(this.size > 0) {
			sb.append(this.get(0));
			for (int i = 1; i < size; i++) {
				WEIGHT weight = this.getLineN(i - 1, i, null);
				sb.append(" - ").append(weight).append(" - ");
				sb.append(this.get(i));
			}
		}

		return sb.toString();
	}

	private void checkNodeNull(Object node) {
		if(node == null){
			throw new NullPointerException();
		}
	}

	/**
	 * 获取相邻节点的距离
	 * @param f
	 * @param t
	 * @param defaultWeight
	 */
	public WEIGHT getLineN(int f, int t, WEIGHT defaultWeight){
		if(f < 0 || f > size || t < 0 || t > size || f >= t){
			throw new NoSuchElementException("args err " + f + " " + t);
		}
		E from = this.get(f);
		E to = this.get(t);
		return getLine(from, to, defaultWeight);
	}
	public WEIGHT getLine(E from, E to, WEIGHT defaultWeight){
		checkNodeNull(from);
		checkNodeNull(to);

		Map<E, WEIGHT> tos = lines.get(from);
		WEIGHT weight = defaultWeight;
		if(tos != null){
			weight = tos.get(to);
			if(weight == null){
				weight = defaultWeight;
			}
		}
		return weight;
	}

	/**
	 * 获取节点之间 的权重值 多个WEIGHT和
	 */
	public List<WEIGHT> getLines(E from, E to, WEIGHT defaultWeight){
		int f = indexOf(from);
		int t = indexOf(to);
		if(from == null){
			f = 0;
		}
		if(to == null){
			t = size - 1;
		}
		return getLinesN(f, t, defaultWeight);
	}
	/**
	 * 获取节点之间 的权重值 多个WEIGHT和
	 */
	public List<WEIGHT> getLinesN(int f, int t, WEIGHT defaultWeight){
		List<WEIGHT> res = new ArrayList<>();
		if(this.size > 1) {
			if(f < 0 || f > size || t < 0 || t > size || f >= t){
				throw new NoSuchElementException("args err " + f + " " + t);
			}
			//from to必为list中节点
			if(f < t){
				//从from遍历到to
				for (int i = f; i < t; i++) {
					WEIGHT weight = getLine(this.get(i), this.get(i + 1), defaultWeight);
					res.add(weight);
				}
			}
		}
		return res;
	}


	/**
	 * 反转
	 */
	public NodeList<E, WEIGHT> reverse(){
		if(size > 1){
			Node<E> now = first, nowPrev = null, nowNext = null;
			last = now;
			while(now != null){

				nowPrev = now.prev;
				nowNext = now.next;

				if(nowPrev == null){	//first
				}
				if(nowNext == null){	//last
				}
				now.next = nowPrev;
				now.prev = nowNext;

				if(nowNext != null && now != null) {
					setLine(nowNext.item, now.item
							, setLine(now.item, nowNext.item, null)
					);
				}
				now = nowNext;
			}
			first = nowPrev;


		}

		return this;
	}












	protected transient int modCount = 0;

	transient int size = 0;

	/**
	 * Pointer to first node.
	 * Invariant: (first == null && last == null) ||
	 *            (first.prev == null && first.item != null)
	 */
	transient Node<E> first;

	/**
	 * Pointer to last node.
	 * Invariant: (first == null && last == null) ||
	 *            (last.next == null && last.item != null)
	 */
	transient Node<E> last;

	/**
	 * Constructs an empty list.
	 */
	public NodeList() {
		this.lines = new LinkedHashMap<>();
	}

	/**
	 * Constructs a list containing the elements of the specified
	 * collection, in the order they are returned by the collection's
	 * iterator.
	 *
	 * @param  c the collection whose elements are to be placed into this list
	 * @throws NullPointerException if the specified collection is null
	 */
	public NodeList(Collection<? extends E> c) {
		this();
		addAll(c);
	}

	/**
	 * Links e as first element.
	 */
	private void linkFirst(E e) {
		final Node<E> f = first;
		final Node<E> newNode = new Node<>(null, e, f);
		first = newNode;
		if (f == null)
			last = newNode;
		else
			f.prev = newNode;
		size++;
		modCount++;
	}

	/**
	 * Links e as last element.
	 */
	void linkLast(E e) {
		final Node<E> l = last;
		final Node<E> newNode = new Node<>(l, e, null);
		last = newNode;
		if (l == null)
			first = newNode;
		else
			l.next = newNode;
		size++;
		modCount++;
	}

	/**
	 * Inserts element e before non-null Node succ.
	 */
	void linkBefore(E e, Node<E> succ) {
		// assert succ != null;
		final Node<E> pred = succ.prev;
		final Node<E> newNode = new Node<>(pred, e, succ);
		succ.prev = newNode;
		if (pred == null)
			first = newNode;
		else
			pred.next = newNode;
		size++;
		modCount++;
	}

	/**
	 * Unlinks non-null first node f.
	 */
	private E unlinkFirst(Node<E> f) {
		// assert f == first && f != null;
		final E element = f.item;
		final Node<E> next = f.next;
		f.item = null;
		f.next = null; // help GC
		first = next;
		if (next == null)
			last = null;
		else
			next.prev = null;
		size--;
		modCount++;
		return element;
	}

	/**
	 * Unlinks non-null last node l.
	 */
	private E unlinkLast(Node<E> l) {
		// assert l == last && l != null;
		final E element = l.item;
		final Node<E> prev = l.prev;
		l.item = null;
		l.prev = null; // help GC
		last = prev;
		if (prev == null)
			first = null;
		else
			prev.next = null;
		size--;
		modCount++;
		return element;
	}

	/**
	 * Unlinks non-null node x.
	 */
	E unlink(Node<E> x) {
		// assert x != null;
		final E element = x.item;
		final Node<E> next = x.next;
		final Node<E> prev = x.prev;

		if (prev == null) {
			first = next;
		} else {
			prev.next = next;
			x.prev = null;
		}

		if (next == null) {
			last = prev;
		} else {
			next.prev = prev;
			x.next = null;
		}

		x.item = null;
		size--;
		modCount++;
		return element;
	}

	/**
	 * Returns the first element in this list.
	 *
	 * @return the first element in this list
	 * @throws NoSuchElementException if this list is empty
	 */
	public E getFirst() {
		final Node<E> f = first;
		if (f == null)
			throw new NoSuchElementException();
		return f.item;
	}

	/**
	 * Returns the last element in this list.
	 *
	 * @return the last element in this list
	 * @throws NoSuchElementException if this list is empty
	 */
	public E getLast() {
		final Node<E> l = last;
		if (l == null)
			throw new NoSuchElementException();
		return l.item;
	}

	/**
	 * Removes and returns the first element from this list.
	 *
	 * @return the first element from this list
	 * @throws NoSuchElementException if this list is empty
	 */
	public E removeFirst() {
		final Node<E> f = first;
		if (f == null)
			throw new NoSuchElementException();
		return unlinkFirst(f);
	}

	/**
	 * Removes and returns the last element from this list.
	 *
	 * @return the last element from this list
	 * @throws NoSuchElementException if this list is empty
	 */
	public E removeLast() {
		final Node<E> l = last;
		if (l == null)
			throw new NoSuchElementException();
		return unlinkLast(l);
	}

	/**
	 * Inserts the specified element at the beginning of this list.
	 *
	 * @param e the element to add
	 */
	public void addFirst(E e) {
		linkFirst(e);
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * <p>This method is equivalent to {@link #add}.
	 *
	 * @param e the element to add
	 */
	public void addLast(E e) {
		linkLast(e);
	}

	/**
	 * Returns {@code true} if this list contains the specified element.
	 * More formally, returns {@code true} if and only if this list contains
	 * at least one element {@code e} such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 *
	 * @param o element whose presence in this list is to be tested
	 * @return {@code true} if this list contains the specified element
	 */
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return the number of elements in this list
	 */
	public int size() {
		return size;
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * <p>This method is equivalent to {@link #addLast}.
	 *
	 * @param e element to be appended to this list
	 * @return {@code true} (as specified by {@link Collection#add})
	 */
	public boolean add(E e) {
		linkLast(e);
		return true;
	}

	/**
	 * Removes the first occurrence of the specified element from this list,
	 * if it is present.  If this list does not contain the element, it is
	 * unchanged.  More formally, removes the element with the lowest index
	 * {@code i} such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
	 * (if such an element exists).  Returns {@code true} if this list
	 * contained the specified element (or equivalently, if this list
	 * changed as a result of the call).
	 *
	 * @param o element to be removed from this list, if present
	 * @return {@code true} if this list contained the specified element
	 */
	public boolean remove(Object o) {
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null) {
					unlink(x);
					return true;
				}
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (o.equals(x.item)) {
					unlink(x);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Appends all of the elements in the specified collection to the end of
	 * this list, in the order that they are returned by the specified
	 * collection's iterator.  The behavior of this operation is undefined if
	 * the specified collection is modified while the operation is in
	 * progress.  (Note that this will occur if the specified collection is
	 * this list, and it's nonempty.)
	 *
	 * @param c collection containing elements to be added to this list
	 * @return {@code true} if this list changed as a result of the call
	 * @throws NullPointerException if the specified collection is null
	 */
	public boolean addAll(Collection<? extends E> c) {
		return addAll(size, c);
	}

	/**
	 * Inserts all of the elements in the specified collection into this
	 * list, starting at the specified position.  Shifts the element
	 * currently at that position (if any) and any subsequent elements to
	 * the right (increases their indices).  The new elements will appear
	 * in the list in the order that they are returned by the
	 * specified collection's iterator.
	 *
	 * @param index index at which to insert the first element
	 *              from the specified collection
	 * @param c collection containing elements to be added to this list
	 * @return {@code true} if this list changed as a result of the call
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 * @throws NullPointerException if the specified collection is null
	 */
	public boolean addAll(int index, Collection<? extends E> c) {
		checkPositionIndex(index);

		Object[] a = c.toArray();
		int numNew = a.length;
		if (numNew == 0)
			return false;

		Node<E> pred, succ;
		if (index == size) {
			succ = null;
			pred = last;
		} else {
			succ = node(index);
			pred = succ.prev;
		}

		for (Object o : a) {
			@SuppressWarnings("unchecked") E e = (E) o;
			Node<E> newNode = new Node<>(pred, e, null);
			if (pred == null)
				first = newNode;
			else
				pred.next = newNode;
			pred = newNode;
		}

		if (succ == null) {
			last = pred;
		} else {
			pred.next = succ;
			succ.prev = pred;
		}

		size += numNew;
		modCount++;
		return true;
	}

	/**
	 * Removes all of the elements from this list.
	 * The list will be empty after this call returns.
	 */
	public void clear() {
		// Clearing all of the links between nodes is "unnecessary", but:
		// - helps a generational GC if the discarded nodes inhabit
		//   more than one generation
		// - is sure to free memory even if there is a reachable Iterator
		for (Node<E> x = first; x != null; ) {
			Node<E> next = x.next;
			x.item = null;
			x.next = null;
			x.prev = null;
			x = next;
		}
		first = last = null;
		size = 0;
		modCount++;
	}


	// Positional Access Operations

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param index index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	public E get(int index) {
		checkElementIndex(index);
		return node(index).item;
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 *
	 * @param index index of the element to replace
	 * @param element element to be stored at the specified position
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	public E set(int index, E element) {
		checkElementIndex(index);
		Node<E> x = node(index);
		E oldVal = x.item;
		x.item = element;
		return oldVal;
	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any
	 * subsequent elements to the right (adds one to their indices).
	 *
	 * @param index index at which the specified element is to be inserted
	 * @param element element to be inserted
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	public void add(int index, E element) {
		checkPositionIndex(index);

		if (index == size)
			linkLast(element);
		else
			linkBefore(element, node(index));
	}

	/**
	 * Removes the element at the specified position in this list.  Shifts any
	 * subsequent elements to the left (subtracts one from their indices).
	 * Returns the element that was removed from the list.
	 *
	 * @param index the index of the element to be removed
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	public E remove(int index) {
		checkElementIndex(index);
		return unlink(node(index));
	}

	/**
	 * Tells if the argument is the index of an existing element.
	 */
	private boolean isElementIndex(int index) {
		return index >= 0 && index < size;
	}

	/**
	 * Tells if the argument is the index of a valid position for an
	 * iterator or an add operation.
	 */
	private boolean isPositionIndex(int index) {
		return index >= 0 && index <= size;
	}

	/**
	 * Constructs an IndexOutOfBoundsException detail message.
	 * Of the many possible refactorings of the error handling code,
	 * this "outlining" performs best with both server and client VMs.
	 */
	private String outOfBoundsMsg(int index) {
		return "Index: "+index+", Size: "+size;
	}

	private void checkElementIndex(int index) {
		if (!isElementIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private void checkPositionIndex(int index) {
		if (!isPositionIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * Returns the (non-null) Node at the specified element index.
	 */
	Node<E> node(int index) {
		// assert isElementIndex(index);

		if (index < (size >> 1)) {
			Node<E> x = first;
			for (int i = 0; i < index; i++)
				x = x.next;
			return x;
		} else {
			Node<E> x = last;
			for (int i = size - 1; i > index; i--)
				x = x.prev;
			return x;
		}
	}

	// Search Operations

	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 * More formally, returns the lowest index {@code i} such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 *
	 * @param o element to search for
	 * @return the index of the first occurrence of the specified element in
	 *         this list, or -1 if this list does not contain the element
	 */
	public int indexOf(Object o) {
		int index = 0;
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null)
					return index;
				index++;
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (o.equals(x.item))
					return index;
				index++;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 * More formally, returns the highest index {@code i} such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 *
	 * @param o element to search for
	 * @return the index of the last occurrence of the specified element in
	 *         this list, or -1 if this list does not contain the element
	 */
	public int lastIndexOf(Object o) {
		int index = size;
		if (o == null) {
			for (Node<E> x = last; x != null; x = x.prev) {
				index--;
				if (x.item == null)
					return index;
			}
		} else {
			for (Node<E> x = last; x != null; x = x.prev) {
				index--;
				if (o.equals(x.item))
					return index;
			}
		}
		return -1;
	}

	// Queue operations.

	/**
	 * Retrieves, but does not remove, the head (first element) of this list.
	 *
	 * @return the head of this list, or {@code null} if this list is empty
	 * @since 1.5
	 */
	public E peek() {
		final Node<E> f = first;
		return (f == null) ? null : f.item;
	}

	/**
	 * Retrieves, but does not remove, the head (first element) of this list.
	 *
	 * @return the head of this list
	 * @throws NoSuchElementException if this list is empty
	 * @since 1.5
	 */
	public E element() {
		return getFirst();
	}

	/**
	 * Retrieves and removes the head (first element) of this list.
	 *
	 * @return the head of this list, or {@code null} if this list is empty
	 * @since 1.5
	 */
	public E poll() {
		final Node<E> f = first;
		return (f == null) ? null : unlinkFirst(f);
	}

	/**
	 * Retrieves and removes the head (first element) of this list.
	 *
	 * @return the head of this list
	 * @throws NoSuchElementException if this list is empty
	 * @since 1.5
	 */
	public E remove() {
		return removeFirst();
	}

	/**
	 * Adds the specified element as the tail (last element) of this list.
	 *
	 * @param e the element to add
	 * @return {@code true} (as specified by {@link Queue#offer})
	 * @since 1.5
	 */
	public boolean offer(E e) {
		return add(e);
	}

	// Deque operations
	/**
	 * Inserts the specified element at the front of this list.
	 *
	 * @param e the element to insert
	 * @return {@code true} (as specified by {@link Deque#offerFirst})
	 * @since 1.6
	 */
	public boolean offerFirst(E e) {
		addFirst(e);
		return true;
	}

	/**
	 * Inserts the specified element at the end of this list.
	 *
	 * @param e the element to insert
	 * @return {@code true} (as specified by {@link Deque#offerLast})
	 * @since 1.6
	 */
	public boolean offerLast(E e) {
		addLast(e);
		return true;
	}

	/**
	 * Retrieves, but does not remove, the first element of this list,
	 * or returns {@code null} if this list is empty.
	 *
	 * @return the first element of this list, or {@code null}
	 *         if this list is empty
	 * @since 1.6
	 */
	public E peekFirst() {
		final Node<E> f = first;
		return (f == null) ? null : f.item;
	}

	/**
	 * Retrieves, but does not remove, the last element of this list,
	 * or returns {@code null} if this list is empty.
	 *
	 * @return the last element of this list, or {@code null}
	 *         if this list is empty
	 * @since 1.6
	 */
	public E peekLast() {
		final Node<E> l = last;
		return (l == null) ? null : l.item;
	}

	/**
	 * Retrieves and removes the first element of this list,
	 * or returns {@code null} if this list is empty.
	 *
	 * @return the first element of this list, or {@code null} if
	 *     this list is empty
	 * @since 1.6
	 */
	public E pollFirst() {
		final Node<E> f = first;
		return (f == null) ? null : unlinkFirst(f);
	}

	/**
	 * Retrieves and removes the last element of this list,
	 * or returns {@code null} if this list is empty.
	 *
	 * @return the last element of this list, or {@code null} if
	 *     this list is empty
	 * @since 1.6
	 */
	public E pollLast() {
		final Node<E> l = last;
		return (l == null) ? null : unlinkLast(l);
	}

	/**
	 * Pushes an element onto the stack represented by this list.  In other
	 * words, inserts the element at the front of this list.
	 *
	 * <p>This method is equivalent to {@link #addFirst}.
	 *
	 * @param e the element to push
	 * @since 1.6
	 */
	public void push(E e) {
		addFirst(e);
	}

	/**
	 * Pops an element from the stack represented by this list.  In other
	 * words, removes and returns the first element of this list.
	 *
	 * <p>This method is equivalent to {@link #removeFirst()}.
	 *
	 * @return the element at the front of this list (which is the top
	 *         of the stack represented by this list)
	 * @throws NoSuchElementException if this list is empty
	 * @since 1.6
	 */
	public E pop() {
		return removeFirst();
	}

	/**
	 * Removes the first occurrence of the specified element in this
	 * list (when traversing the list from head to tail).  If the list
	 * does not contain the element, it is unchanged.
	 *
	 * @param o element to be removed from this list, if present
	 * @return {@code true} if the list contained the specified element
	 * @since 1.6
	 */
	public boolean removeFirstOccurrence(Object o) {
		return remove(o);
	}

	/**
	 * Removes the last occurrence of the specified element in this
	 * list (when traversing the list from head to tail).  If the list
	 * does not contain the element, it is unchanged.
	 *
	 * @param o element to be removed from this list, if present
	 * @return {@code true} if the list contained the specified element
	 * @since 1.6
	 */
	public boolean removeLastOccurrence(Object o) {
		if (o == null) {
			for (Node<E> x = last; x != null; x = x.prev) {
				if (x.item == null) {
					unlink(x);
					return true;
				}
			}
		} else {
			for (Node<E> x = last; x != null; x = x.prev) {
				if (o.equals(x.item)) {
					unlink(x);
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private LinkedList<E> superClone() {
		try {
			return (LinkedList<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}

	/**
	 * Returns a shallow copy of this {@code LinkedList}. (The elements
	 * themselves are not cloned.)
	 *
	 * @return a shallow copy of this {@code LinkedList} instance
	 */
	public Object clone() {
		NodeList<E, WEIGHT> clone = new NodeList<>();

		// Put clone into "virgin" state
		clone.first = clone.last = null;
		clone.size = 0;
		clone.modCount = 0;

		// Initialize clone with our elements
		for (Node<E> x = first; x != null; x = x.next)
			clone.add(x.item);

		return clone;
	}

	/**
	 * Returns an array containing all of the elements in this list
	 * in proper sequence (from first to last element).
	 *
	 * <p>The returned array will be "safe" in that no references to it are
	 * maintained by this list.  (In other words, this method must allocate
	 * a new array).  The caller is thus free to modify the returned array.
	 *
	 * <p>This method acts as bridge between array-based and collection-based
	 * APIs.
	 *
	 * @return an array containing all of the elements in this list
	 *         in proper sequence
	 */
	public Object[] toArray() {
		Object[] result = new Object[size];
		int i = 0;
		for (Node<E> x = first; x != null; x = x.next)
			result[i++] = x.item;
		return result;
	}

	/**
	 * Returns an array containing all of the elements in this list in
	 * proper sequence (from first to last element); the runtime type of
	 * the returned array is that of the specified array.  If the list fits
	 * in the specified array, it is returned therein.  Otherwise, a new
	 * array is allocated with the runtime type of the specified array and
	 * the size of this list.
	 *
	 * <p>If the list fits in the specified array with room to spare (i.e.,
	 * the array has more elements than the list), the element in the array
	 * immediately following the end of the list is set to {@code null}.
	 * (This is useful in determining the length of the list <i>only</i> if
	 * the caller knows that the list does not contain any null elements.)
	 *
	 * <p>Like the {@link #toArray()} method, this method acts as bridge between
	 * array-based and collection-based APIs.  Further, this method allows
	 * precise control over the runtime type of the output array, and may,
	 * under certain circumstances, be used to save allocation costs.
	 *
	 * <p>Suppose {@code x} is a list known to contain only strings.
	 * The following code can be used to dump the list into a newly
	 * allocated array of {@code String}:
	 *
	 * <pre>
	 *     String[] y = x.toArray(new String[0]);</pre>
	 *
	 * Note that {@code toArray(new Object[0])} is identical in function to
	 * {@code toArray()}.
	 *
	 * @param a the array into which the elements of the list are to
	 *          be stored, if it is big enough; otherwise, a new array of the
	 *          same runtime type is allocated for this purpose.
	 * @return an array containing the elements of the list
	 * @throws ArrayStoreException if the runtime type of the specified array
	 *         is not a supertype of the runtime type of every element in
	 *         this list
	 * @throws NullPointerException if the specified array is null
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			a = (T[])java.lang.reflect.Array.newInstance(
					a.getClass().getComponentType(), size);
		int i = 0;
		Object[] result = a;
		for (Node<E> x = first; x != null; x = x.next)
			result[i++] = x.item;

		if (a.length > size)
			a[size] = null;

		return a;
	}

	private static final long serialVersionUID = 876323262645176354L;

	/**
	 * Saves the state of this {@code LinkedList} instance to a stream
	 * (that is, serializes it).
	 *
	 * @serialData The size of the list (the number of elements it
	 *             contains) is emitted (int), followed by all of its
	 *             elements (each an Object) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		// Write out any hidden serialization magic
		s.defaultWriteObject();

		// Write out size
		s.writeInt(size);

		// Write out all elements in the proper order.
		for (Node<E> x = first; x != null; x = x.next)
			s.writeObject(x.item);
	}

	/**
	 * Reconstitutes this {@code LinkedList} instance from a stream
	 * (that is, deserializes it).
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in any hidden serialization magic
		s.defaultReadObject();

		// Read in size
		int size = s.readInt();

		// Read in all elements in the proper order.
		for (int i = 0; i < size; i++)
			linkLast((E)s.readObject());
	}
}
