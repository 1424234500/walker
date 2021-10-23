package com.walker.util;

import org.slf4j.Logger;

import java.util.*;

/**
 * 耗时监控 提示信息工具
 * new Watch("sql").set(sql).set("size", size).exception(e).toString()
 * new Watch("sql").set(sql).set("size", size).res(count).toString()
 * <p>
 * sd
 *
 * @author Walker
 */
public class Watch {
	List<Long> times;
	StringBuilder sb;
	Map<String, Long> keyCost;

	public Watch() {
		this("");
	}

	public Watch(Object... infos) {
		this.times = new ArrayList<>();
		this.times.add(System.currentTimeMillis());
		sb = new StringBuilder(Arrays.toString(infos));
		this.keyCost = new LinkedHashMap<>();
	}

	public Watch put(String str) {
		this.sb.append(str);
		return this;
	}

	public Watch putln(String str) {
		this.sb.append(str).append("\n");
		return this;
	}

	public Watch put(Object key, Object... values) {
		this.sb.append(" " + key + ">" + Arrays.toString(values));
		return this;
	}

	public Watch put(String key, Long dtime) {
		put(key, "" + dtime);
		this.keyCost.put(key, dtime);
		return this;
	}

	public Watch putln(Object key, Object... values) {
		put(key, values);
		this.put("\n");
		return this;
	}

	/**
	 * 分key耗时 查询操作耗时50ms
	 */
	public long cost(Object... keys) {
		long now = System.currentTimeMillis();
		long last = times.get(0);
		long deta = now - last;
		this.put((keys.length > 0 ? Arrays.toString(keys) + " " : "") + "", deta);
		times.add(0, now);
		return deta;
	}

	public long costln(Object... keys) {
		long res = cost(keys);
		this.put("\n");
		return res;
	}

	/**
	 * 获取分片耗时
	 */
	public long getTimeSplit() {
		long now = times.get(0);
		long last = times.get(times.size() > 1 ? 1 : 0);
		long deta = now - last;
		return deta;
	}

	/**
	 * 获取总耗时
	 */
	public long getTimeAll() {
		long now = times.get(0);
		long last = times.get(times.size() - 1);
		long deta = now - last;
		return deta;
	}

	public Watch res() {
		return res(null);
	}

	/**
	 * 总耗时存入
	 * 若传入log则记录日志
	 */
	public Watch res(Logger log) {
		long now = System.currentTimeMillis();
		long last = times.get(times.size() - 1);
		long deta = now - last;
		this.put("total", deta + "");
		if (log != null)
			log.info(this.toString());
		return this;
	}

	public Watch resSlf4j() {
		return resSlf4j(null);
	}

	/**
	 * 总耗时存入
	 * 若传入log则记录日志
	 */
	public Watch resSlf4j(org.slf4j.Logger log) {
		long now = System.currentTimeMillis();
		long last = times.get(times.size() - 1);
		long deta = now - last;
		this.put("total", deta + "");

		if (log != null)
			log.info(this.toPrettyString());
		return this;
	}

	public Watch res(Object res) {
		return res(res, null);
	}

	/**
	 * 正常结果
	 *
	 * @param res
	 */
	public Watch res(Object res, Logger logs) {
		this.put("res", res);
		if (logs != null)
			this.res(logs);
		return this;
	}

	public Watch resSlf4j(Object res) {
		return resSlf4j(res, null);
	}

	/**
	 * 正常结果
	 *
	 * @param res
	 */
	public Watch resSlf4j(Object res, org.slf4j.Logger logs) {
		this.put("res", res);
		this.resSlf4j(logs);
		return this;
	}

	public Watch exceptionWithThrow(Throwable e) {
		return exceptionWithThrow(e, null);
	}

	/**
	 * 异常结果
	 *
	 * @param e
	 */
	public Watch exceptionWithThrow(Throwable e, Logger log) {
		this.put("exception", Tools.toString(e));
		this.res(log);
		throw new RuntimeException(this.toString(), e);
	}

	/**
	 * 异常结果 不抛出异常
	 *
	 * @param e
	 */
	public Watch exception(Throwable e) {
		this.put("exception", Tools.toString(e));
//		throw new ErrorException(this);
		return this;
	}

	/**
	 * 异常结果 不抛出异常
	 *
	 * @param e
	 */
	public Watch exception(Throwable e, Logger log) {
		this.put("exception", Tools.toString(e));
		this.res(log);
//		throw new ErrorException(this);
		return this;
	}

	public String toString() {
		return this.sb.toString();
	}

	public String toPrettyString() {
		String str = this.toString();
		StringBuilder sb2 = new StringBuilder(str);

		Long total = 0L;
		for (String key : keyCost.keySet()) {
			Long time = keyCost.get(key);
			total += time;
		}
		sb2.append("\n----total " + total + "----\n");
		sb2.append("ms\t%\tKey\n");
		sb2.append("----------------------\n");

		for (String key : keyCost.keySet()) {
			Long time = keyCost.get(key);
			float percent = (int) (1f * time / total * 1000) / 10f;
			sb2.append(time + "\t" + percent + "%\t" + key + "\n");
		}

		sb2.append("---------end-------------\n");
		return sb2.toString();
	}

}
