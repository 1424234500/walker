package com.walker.box;

import com.walker.core.encode.IOEncode;
import com.walker.util.FileUtil;

import java.io.File;
import java.util.List;

public class DirMakeDecode {
    public static String ext = ".dme.walker";

    public static void main(String[] argv) {

        String dirFrom = "D:\\workspace\\walkerEncode\\walker-core";
        String dirTo = "D:\\workspace\\walkerDecode\\walker-core";

        List<File> list = FileUtil.showDir(dirFrom, null);

        new TaskThreadPie(list.size()) {

            @Override
            public void onStartThread(int threadNo) throws Exception {
                File file = list.get(threadNo);
                if (file.isDirectory()) return;

                String from = file.getAbsolutePath();
                String to = dirTo + from.substring(dirFrom.length());
                to = to.substring(0, to.length() - ext.length());

                IOEncode.encode(from, to);
            }
        }.setThreadSize(4).start();


    }


}
