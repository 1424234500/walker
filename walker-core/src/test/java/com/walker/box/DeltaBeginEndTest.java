package com.walker.box;

import com.walker.core.mode.DeltaBeginEnd;
import com.walker.core.util.Tools;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class DeltaBeginEndTest extends TestCase {

    public void test() {
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
        Tools.out(DeltaBeginEnd.merge(list, String::compareTo));
    }
}