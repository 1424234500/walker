package com.walker.box;

import com.walker.core.util.Tools;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DirAndroidClear {

    ThreadPoolExecutor pool = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public DirAndroidClear() {
        action(0, new File("/"));
    }

    public static void main(String[] argv) {
        new DirAndroidClear();
    }

    private void action(int level, File file) {
        if (level >= 2) {
            return;
        }
        if (file != null) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();

                if (files == null || files.length == 0) {
                    pool.submit(new Runnable() {
                        @Override
                        public void run() {
                            Tools.out("rm " + file.getAbsolutePath());
                        }
                    });
                } else {
                    for (File listFile : files) {
                        action(level + 1, listFile);
                    }
                }
            }
        }
    }


}
