package com.walker.dao;

import com.walker.ApplicationProviderTests;
import com.walker.spring.dao.RoleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RoleRepositoryTest extends ApplicationProviderTests{

    @Autowired
    RoleRepository roleRepository;

    @Test
    public void getRoles() {

        out(roleRepository.getRoles("001", ""));




    }
}