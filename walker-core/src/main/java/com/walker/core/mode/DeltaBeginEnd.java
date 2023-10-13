package com.walker.core.mode;


import com.walker.core.util.Tools;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 多区间连续性合并工具
 *         list.add(new DeltaBeginEnd<String>().setBegin("01:00").setEnd("01:55"));
 *         list.add(new DeltaBeginEnd<String>().setBegin("01:20").setEnd("01:20"));
 *         list.add(new DeltaBeginEnd<String>().setBegin("02:00").setEnd("08:55"));
 *         list.add(new DeltaBeginEnd<String>().setBegin("10:00").setEnd("12:55"));
 *         return 01:00~01:20, 02:00~08:55, 10:00~12:55
 *
 */
@Data
@Accessors(chain = true)
public class DeltaBeginEnd<T> {
    T begin;
    T end;

    public static <T> List<DeltaBeginEnd<T>> merge(List<DeltaBeginEnd<T>> list, Comparator<T> comparator) {
        int size = list.size();
        if (size <= 1)
            return list;
        list.sort((o1, o2) -> comparator.compare(o1.begin, o2.begin));
        List<DeltaBeginEnd<T>> res = new ArrayList<>();
        List<DeltaBeginEnd<T>> copy = new ArrayList<>();
        copy.addAll(list);

        DeltaBeginEnd<T> now = list.get(0);
        copy.remove(now);
        for (int i = 1; i < size; i++) {
            DeltaBeginEnd<T> item = list.get(i);
            if (comparator.compare(now.begin, item.end) > 0 || comparator.compare(now.end, item.begin) < 0) {
            } else {//交集 now 匹配合并 所有其他的 命中则合并移除
                String info = now + " + " + item;
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


    @Override
    public String toString() {
        return "[" + begin + ", " + end + "}";
    }

}