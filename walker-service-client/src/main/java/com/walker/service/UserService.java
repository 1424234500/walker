package com.walker.service;

import com.walker.mode.Page;
import com.walker.mode.User;

import java.util.List;

public interface UserService {


    List<User> saveAll(List<User> obj);
    Integer[] deleteAll(List<String> ids);

    User get(User obj);
    Integer delete(User obj);

    List<User> finds(User obj, Page page);
    Integer count(User obj);

    User auth(User obj);

}
