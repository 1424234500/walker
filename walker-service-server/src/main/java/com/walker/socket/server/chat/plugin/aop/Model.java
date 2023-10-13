package com.walker.socket.server.chat.plugin.aop;

public class Model{
	long count = 0;
	long cost = 0;

	public long getCount() {
		return count;
	}

	public Model setCount(long count) {
		this.count = count;
		return this;
	}

	public long getCost() {
		return cost;
	}

	public Model setCost(long cost) {
		this.cost = cost;
		return this;
	}

	public Model incr(int count, long cost) {
		this.count += count;
		this.cost += cost;
		return this;
	}
}
