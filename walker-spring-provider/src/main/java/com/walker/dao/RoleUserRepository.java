package com.walker.dao;

import com.walker.core.mode.school.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * sharding 分库 & 分表
 */
@Repository
public interface RoleUserRepository extends JpaRepository<RoleUser, String>, JpaSpecificationExecutor {//实体类 主键类型 自定义查询
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
    @Query("delete from RoleUser t where t.ID in (?1) ")
    Integer selfDeleteAll(List<String> ids);

    @Query("select t from RoleUser t where t.USER_ID=?1 ")
    List<RoleUser> findByUserId(String id);


    @Transactional
    @Modifying
    @Query("delete from RoleUser t where t.USER_ID in (?1) ")
    Integer deleteAllByUserId(List<String> userIds);

    @Transactional
    @Modifying
    @Query("delete from RoleUser t where t.ROLE_ID in (?1) ")
    Integer deleteAllByRoleId(List<String> roleIds);


}
