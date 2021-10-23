package com.walker.service;

import com.walker.core.database.BaseDao;

/**
 * 基础数据库操作类 负责把上级传递的参数拼接sql递交baseDao查询
 * 统一使用Map<String, Object>对象
 * 参数都是Object...objs[] 占位符方式
 * 相比baseDao只在findPage时做了Page转换为页码条数处理
 * 使用baseService相比BaseDao加以事务管理
 */
public interface BaseService extends BaseDao {

}
