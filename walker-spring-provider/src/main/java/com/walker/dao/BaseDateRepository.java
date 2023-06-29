package com.walker.dao;

import com.walker.core.mode.sys.BaseDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * sharding 分库 & 分表
 */
@Repository
public interface BaseDateRepository extends JpaRepository<BaseDate, String>, JpaSpecificationExecutor {//实体类 主键类型 自定义查询
//    List<T> findAll();
//    List<T> findAll(Sort var1);
//    List<T> findAllById(Iterable<ID> var1);
//    <S extends T> List<S> saveAll(Iterable<S> var1);
//    void flush();
//    <S extends T> S saveAndFlush(S var1);
//    void deleteInBatch(Iterable<T> var1);
//    void deleteAllInBatch();
//    T getOne(ID var1);
//    <S extends T> List<S> findAll(Example<S> var1);
//    <S extends T> List<S> findAll(Example<S> var1, Sort var2);

    /**
     * 删除时间之前
     */
    @Transactional
    @Modifying
    @Query("delete from BaseDate t where t.S_MTIME < ?1 ")
    Integer delete(String time);

    @Query("select t from BaseDate t where 1=1 order by S_MTIME DESC ")
    Page<BaseDate> findsRecently(Pageable page);


}
