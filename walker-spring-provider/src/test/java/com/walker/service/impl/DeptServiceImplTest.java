package com.walker.service.impl;

import com.walker.ApplicationProviderTests;
import com.walker.core.mode.school.Dept;
import com.walker.service.DeptService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class DeptServiceImplTest extends ApplicationProviderTests {

    @Autowired
    DeptService deptService;

    @Test
    public void saveAll() {
        Dept root = new Dept().setID("D0").setPATH("").setNAME("root").setP_ID("");
        Dept d11 = new Dept().setID("D11").setPATH("").setNAME("d11").setP_ID("D0");
        Dept d12 = new Dept().setID("D12").setPATH("").setNAME("d12").setP_ID("D0");

        Dept d21 = new Dept().setID("D21").setPATH("").setNAME("d21").setP_ID("D11");
        Dept d22 = new Dept().setID("D22").setPATH("").setNAME("d22").setP_ID("D11");

        deptService.saveAll(Arrays.asList(root));
        deptService.saveAll(Arrays.asList(d11, d12, d21));

        deptService.saveAll(Arrays.asList(d11, d12, d21, d22));
    }
    @Test
    public void delete(){
        deptService.deleteAll(Arrays.asList("D11"));
    }


}