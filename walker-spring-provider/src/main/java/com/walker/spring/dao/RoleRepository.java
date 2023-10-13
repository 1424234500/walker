package com.walker.spring.dao;

import com.walker.core.mode.school.Role;
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
public interface RoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor {//实体类 主键类型 自定义查询
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
    @Query("delete from Role t where t.ID in (?1) ")
    Integer selfDeleteAll(List<String> ids);


    /**
     * 查询角色
     *
     * @param id    传入user id则查user的role 传入dept id则查dept的role
     * @param sFlag
     */
//    @Query("select r.ID as ID,r.LEVEL as LEVEL,r.NAME as NAME,r.NUM as NUM,r.S_ATIME as S_ATIME,r.S_MTIME as S_MTIME, COALESCE(ru.S_FLAG,'0') as S_FLAG from Role r " +
//            "left join RoleUser ru " +
//            "on ru.USER_ID=?1 and r.ID=ru.ROLE_ID and ru.S_FLAG like concat('%', ?2, '%')  " +
//            " ")
//    第二张表名字自动小写异常!!!???
    @Query(value = "select * from ( " +
            "select r.ID,r.LEVEL,r.NAME,r.NUM,r.s_ATIME,r.s_MTIME,ifnull(ru.S_FLAG,'0') S_FLAG " +
            "from W_ROLE r " +
            "left join W_ROLE_USER ru " +
            "on ru.USER_ID=?1 and r.ID=ru.ROLE_ID " +
            " ) t " +
            "where S_FLAG like concat('%', ?2, '%') ", nativeQuery = true)
    List<Role> getRoles(String id, String sFlag);

//
//    @Query(value = "select t.* from W_TEACHER t where t.NAME like CONCAT('%', CONCAT(?1, '%'))",
//            countQuery = "select count(t.id) from W_TEACHER t where t.NAME like CONCAT('%', CONCAT(?1, '%'))",
//            nativeQuery = true)


    @Query("select t from Role t where t.ID in (?1) ")
    List<Role> findByIds(List<String> ids);


}
