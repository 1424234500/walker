package com.walker.box;


import com.walker.core.mode.Response;
import com.walker.core.system.Pc;
import com.walker.core.util.Tools;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class TaskAdbFileTrans {

    public TaskAdbFileTrans() {

        String dir = "/sdcard/1cph";
        action(dir);


    }

    public static void main(String[] args) {
        new TaskAdbFileTrans();
    }

    public void action(String dir) {
        List<String> list = ls(dir);
        for (String s : list) {
            if (s.indexOf(".") > 0) {
                Tools.out("file", s);

            } else {
                Tools.out("dir", s);
                action(dir + "/" + s);
            }
        }

    }

    public List<String> ls(String dir) {
        List<String> res = new ArrayList<>();
        Response<String> response = Pc.doCmdString("adb shell ls  " + dir, "C:\\Users\\walker");
        if (response.getSuccess() && response.getRes().indexOf("No such") < 0) {
            for (String s : StringUtils.trim(response.getRes()).split(" +")) {
                s = StringUtils.trim(s);
                if (StringUtils.isNotEmpty(s)) {
                    res.add(s);
                }
            }
        } else {
            Tools.out(dir, response.getRes(), response.getTip());
        }
        return res;
    }


}