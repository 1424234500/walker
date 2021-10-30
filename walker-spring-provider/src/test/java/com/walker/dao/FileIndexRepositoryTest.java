
package com.walker.dao;

import com.walker.ApplicationProviderTests;
import com.walker.mode.FileIndex;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileIndexRepositoryTest extends ApplicationProviderTests {

    @Autowired
    FileIndexRepository fileIndexRepository;
    public void makeData(){
        List<FileIndex> list = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            list.add(new FileIndex().setID(i + "i").setPATH("/aaa/bbb/" + i));
        }
        fileIndexRepository.saveAll(list);
    }
    @Test
    public void findsAllByPath() {
        makeData();
        out(fileIndexRepository.findsAllByPath(Arrays.asList("/aaa/bbb/1", "")));

    }

    @Test
    public void findsAllByStartPath() {

        makeData();
        out(fileIndexRepository.findsAllByStartPath("/aaa/b"));


    }

    @Test
    public void deleteAllByStartPath() {

        makeData();
        out(fileIndexRepository.deleteAllByStartPath("/aaa/b"));
        out(fileIndexRepository.findsAllByStartPath("/aaa/b"));
    }

    @Test
    public void deleteAllByPath() {
        makeData();
        out(fileIndexRepository.deleteAllByPath(Arrays.asList("/aaa/bbb/1", "/aaa/bbb/2")));

        out(fileIndexRepository.findsAllByPath(Arrays.asList("/aaa/bbb/", "")));

    }
}