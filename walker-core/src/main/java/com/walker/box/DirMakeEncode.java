package com.walker.box;

import com.walker.core.encode.IOEncode;
import com.walker.util.FileUtil;

import java.io.File;
import java.util.List;

public class DirMakeEncode {
    public static String ext = ".dme.walker";

    public static void main(String[] argv){

        String dirFrom = "D:\\workspace\\walker\\walker-core\\src";
        String dirTo = "D:\\workspace\\walker-encode";

        List<File> list = FileUtil.showDir(dirFrom,null);

        new TaskThreadPie(list.size()){

            @Override
            public void onStartThread(int threadNo) throws Exception {
                File file = list.get(threadNo);
                if(file.isDirectory())return;

                String from = file.getAbsolutePath();
                String to = dirTo + from.substring(dirFrom.length(), from.length())
                         + ext
                        ;


                if(from.endsWith(".class")
                        || from.endsWith(".log")
                        || from.endsWith(".jar")
                ){

                    return ;
                }
                if(from.endsWith(".jar")
                        || from.endsWith(".java")
                        || from.endsWith(".xml")
                        || from.endsWith(".yml")
                        || from.endsWith(".properties")
                        || from.endsWith(".c")
                        || from.endsWith(".txt")
                        || from.endsWith(".vue")
                        || from.endsWith(".html")
                        || from.endsWith(".json")

                )

                IOEncode.encode(from, to);
            }
        }.setThreadSize(4).start();


    }




}
