package com.walker.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JnaFactory {
    private static final Logger log = LoggerFactory.getLogger(JnaFactory.class);
    public static JnaTest jnaSC;

    static {
        jnaSC = loadDll(JnaTest.class, Arrays.asList(/*"StreamConvertor", */"StreamConvertor.dll", "libStreamConvertor.so"));
    }

    public static JnaTest getJnaTest() {
        return jnaSC;
    }

    private static <T extends Library> T loadDll(Class<T> clz, List<String> names) {
        T res = null;
        int ic = 0;
        URL url = JnaFactory.class.getResource("/");
//        多路径兼容动态寻址
        List<String> dirs = new ArrayList<>(Arrays.asList(
                new File("").getAbsolutePath()
                , new File("").getAbsolutePath() + File.separator + "lib"
                , File.separator
                , File.separator + "lib"
                , "C:\\Windows\\System32"
                , "C:\\Windows\\SysWOW64"
        ));
        if (url != null) {
            dirs.add(0, url.getPath());
            dirs.add(1, url.getPath() + File.separator + "lib");
        } else {
            log.error("========JnaFactory.class.getResource(\"/\") is null");
        }

        List<String> dfiles = new ArrayList<>();
        for (String dir : dirs) {
            for (String name : names) {
                String file = dir + File.separator + name;
                if (new File(file).isFile()) {
                    dfiles.add(file);
                    log.warn(">>>>>>>>> dll file point at   " + file);
                } else {
                    log.info("========= dll file not exists " + file);
                }
            }
        }

        for (String file : dfiles) {
            try {
                res = Native.loadLibrary(file, clz);
                if (res == null) {
                    throw new Exception("load file res is null ? ");
                }
                log.warn("----------------------------------");
                log.warn(ic++ + ".load file ok " + file + " " + clz);
                log.warn("----------------------------------");

                return res;
            } catch (Throwable t) {
                log.error(ic++ + ".load file error Native.loadLibrary(" + file + ", " + clz + "); " + t.getMessage(), t);
            }
        }

//        早异常 早退出 早发现!!!!!!!!!!!
        throw new RuntimeException("dll/so load " + clz + " by file in " + dirs + " of" + names);
    }

    public interface JnaTest extends Library {
        String hello();
    }


}
