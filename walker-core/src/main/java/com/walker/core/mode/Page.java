package com.walker.core.mode;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page implements Serializable{
	private static final long serialVersionUID = 1L;
	public final static int showNumDefault = 10;
	/**
	 * 总数据条数
	 */
	private long total = 0;
	/**
	 * 每页数量
	 */
	private int shownum = showNumDefault;
	/**
	 * 当前页码
	 */
	private int nowpage = 1;
	/**
	 * 总页数
	 */
	private int pagenum = 0;
	/**
	 * 排序	id, name desc, time asc 空则不排序
	 */
	private String order = "";
	public String toString() {
		return this.toBean().toString();
	}
	public Page(){
	}

	public Page(int shownum, long allNum){
		this.shownum = shownum;
		this.setTotal(allNum);
	}
	
	public Bean toBean(){
		return new Bean().put("num", total).put("shownum", shownum).put("nowpage", nowpage).put("order", order);
	}


	public int getStart(){
		return (nowpage -1) * shownum;
	}
	public int getStop(){
		return nowpage * shownum;
	}
	public long getTotal() {
		return total;
	} 
	/**
	 * 设置预期数据的总数量 并根据页显示数量更新总页数 
	 * @param total
	 */
	public Page setTotal(long total) {
		this.total = total;
		this.pagenum = (int) Math.ceil( 1.0 * total / this.shownum);
		return this;
	}

	public int getShownum() {
		return shownum;
	}

	public Page setShownum(int eachPageNum) {
		this.shownum = eachPageNum;
		if(this.shownum <= 0){
			this.shownum = showNumDefault;
		}
		return this;
	}

	public int getNowpage() {
		return nowpage;
	}

	public Page setNowpage(int nowPage){
		this.nowpage = nowPage;
		return this;
	}

	public int getPagenum() {
		return pagenum;
	}

	public Page setPagenum(int pageNum) {
		this.pagenum = pageNum;return this;
	}
	public String getOrder(String defaultValue) {
		String res = this.getOrder();
		if(res.length() <= 0) {
			res = defaultValue;
		}
		return res;
	}
	public String getOrder() {
		return order;
	}

	/**
	 * 过滤非法字符串 sql注入
	 * @param order
	 */
	public Page setOrder(String order) {
		order = order.replace('\'', ' ');
		order = order.replace('&', ' ');
		order = order.replace('|', ' ');
		order = order.replace('"', ' ');
		this.order = order;
		return this;
	}


	/**
	 * 分页回调
	 * @param collection
	 * @param batchSize
	 * @param fun
	 * @param <T>
	 */
	public static <T, RES> List<RES> batch(List<T> collection, int batchSize, FunArgsReturn<List<T>, RES> fun){
		List<RES> res = new ArrayList<>();
		assert collection != null;
		assert collection.size() > 0;
		assert batchSize > 0;

		if(batchSize <= 0){
//			Tools.out("batch batchSize is null ");
		}else if(collection == null || collection.size() <= 0){
//			Tools.out("batch collection is null ");
		}else{
			int s = (int) Math.ceil(1d * collection.size() / batchSize);
			if(fun != null){
				for(int pageNow = 0; pageNow < s; pageNow++){
					res.add(fun.make(collection.subList(pageNow*batchSize, Math.min(pageNow*batchSize+batchSize, collection.size())), pageNow));
				}
			}
		}
		return res;
	}
	public interface FunArgsReturn<A, RES>{
		RES make(A obj, Integer nowPage) ;
	}

}