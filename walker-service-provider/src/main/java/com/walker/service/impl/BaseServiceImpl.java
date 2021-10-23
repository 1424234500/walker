package com.walker.service.impl;

import com.walker.dao.JdbcTemplateDao;
import org.springframework.transaction.annotation.Transactional;


@org.springframework.stereotype.Service("baseService")
@com.alibaba.dubbo.config.annotation.Service
@Transactional
public class BaseServiceImpl extends JdbcTemplateDao {

}