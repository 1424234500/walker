package com.walker.service;

import com.walker.core.mode.Page;
import com.walker.core.mode.sys.Action;

import java.util.List;

public interface ActionService {


    List<Action> saveAll(List<Action> obj);
    Integer[] deleteAll(List<String> ids);

    Action get(Action obj);
    Integer delete(Action obj);

    List<Action> finds(Action obj, Page page);
    Integer count(Action obj);

}
