package com.walker.mode;



import com.walker.util.Tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


class DeltaBeginEnd<T> {
    T begin;
    T end;

    @Override
    public String toString() {
        return "[" + begin + ", " + end + "}";
    }

    public DeltaBeginEnd<T> setBegin(T begin) {
        this.begin = begin;
        return this;
    }

    public DeltaBeginEnd<T> setEnd(T end) {
        this.end = end;
        return this;
    }

    public static <T> List<DeltaBeginEnd<T>> merge(List<DeltaBeginEnd<T>> list, Comparator<T> comparator) {
        int size = list.size();
        if(size <= 1)
            return list;
        list.sort((o1, o2)-> comparator.compare(o1.begin, o2.begin) );
        List<DeltaBeginEnd<T>> res = new ArrayList<>();
        List<DeltaBeginEnd<T>> copy = new ArrayList<>();
        copy.addAll(list);

        DeltaBeginEnd<T> now = list.get(0);
        copy.remove(now);
        for(int i = 1; i < size; i++){
            DeltaBeginEnd<T> item = list.get(i);
            if(comparator.compare(now.begin, item.end ) > 0|| comparator.compare(now.end, item.begin) < 0) {
            }else{//交集 now 匹配合并 所有其他的 命中则合并移除
                String info = now.toString() + " + " + item.toString();
                now.begin = comparator.compare(item.begin, now.begin) < 0 ? item.begin : now.begin;
                now.end = comparator.compare(item.end, now.end) > 0 ? item.end : now.end;
                copy.remove(item);
                Tools.out("merge " + info + " -> " + now);
            }
        }
        res.add(now);
//        copy中省下的都是跟now无交集的
        res.addAll(merge(copy, comparator));
        return res;
    }

    public static void main(String[] argvs){
        List<DeltaBeginEnd<String>> list = new ArrayList<>();
        list.add(new DeltaBeginEnd<String>().setBegin("01:00").setEnd("01:55"));
        list.add(new DeltaBeginEnd<String>().setBegin("01:20").setEnd("01:20"));
        list.add(new DeltaBeginEnd<String>().setBegin("02:00").setEnd("08:55"));
        list.add(new DeltaBeginEnd<String>().setBegin("10:00").setEnd("12:55"));
        list.add(new DeltaBeginEnd<String>().setBegin("06:00").setEnd("09:55"));
        list.add(new DeltaBeginEnd<String>().setBegin("15:00").setEnd("16:55"));
        list.add(new DeltaBeginEnd<String>().setBegin("16:55").setEnd("17:55"));
        list.add(new DeltaBeginEnd<String>().setBegin("40:00").setEnd("42:55"));

        Tools.out(list);
        Tools.out(merge(list, String::compareTo));
    }
}