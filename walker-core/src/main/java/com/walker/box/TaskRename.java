package com.walker.box;


import com.walker.core.util.FileUtil;
import com.walker.core.util.Tools;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;


public class TaskRename {
    public static void main(String[] argv) throws InterruptedException {
        AtomicInteger i = new AtomicInteger(0);
        FileUtil.showDir("E:\\save_window\\mphoto1", new FileUtil.FunArgsReturnBool<File>() {
            @Override
            public Boolean make(File obj) {
                if (obj.isFile()) {
                    String dir = FileUtil.getFilePath(obj.getAbsolutePath());
                    String ddirName = FileUtil.getFileName(dir);
                    String ton = FileUtil.getFilePath(obj.getAbsolutePath()) + File.separator
                            + ddirName + "-" + Tools.fillStringBy(String.valueOf(i.addAndGet(1)), "0", 4, 0) + "-" + obj.getName();
                    Tools.out(obj.getAbsolutePath(), ton);
                    FileUtil.mv(obj.getAbsolutePath(), ton);
                }
                if (obj.isDirectory()) {
                    if (obj.listFiles() == null || obj.listFiles().length == 0) {
                        obj.delete();
                    }
                }
                return true;
            }
        });


    }

}