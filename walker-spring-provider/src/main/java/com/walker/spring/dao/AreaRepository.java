package com.walker.spring.dao;

import com.walker.core.mode.school.Area;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * sharding 分库 & 分表
 */
@Repository
public interface AreaRepository extends JpaRepository<Area, String>, JpaSpecificationExecutor {//实体类 主键类型 自定义查询
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
     * JPQL查询 删除
     */
    @Transactional
    @Modifying
    @Query("delete from Area t where t.ID in (?1) ")
    Integer selfDeleteAll(Set<String> ids);

    @Query("select t from Area t where t.ID in (?1) ")
    List<Area> findAllByID(Set<String> ids);

    @Query("select t from Area t where t.P_ID in (?1) ")
    List<Area> findAllByP_ID(Set<String> pids);


    @Query("select t from Area t where t.PATH like CONCAT('%', CONCAT(?1, '%')) ")
    List<Area> findAllByPATH(String path);

    @Query("select t from Area t where t.P_ID is null or t.P_ID='' or t.P_ID=t.ID ")
    Page<Area> findsRoot(Pageable page);


}
