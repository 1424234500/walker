package com.walker.dao;

import com.walker.mode.MessageUser;
import com.walker.mode.MessageUserPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * sharding 分表
 */
@Repository
public interface MessageUserRepository extends JpaRepository<MessageUser, MessageUserPK> {//实体类 主键类型
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
     * 分页查询 native    同时获取分页信息
     */
    @Query(value = "SELECT * FROM W_SHARDING_MSG_USER WHERE ID=?1 AND TIME < ?2 order by TIME desc ",
            countQuery = "SELECT count(1) FROM W_SHARDING_MSG_USER WHERE ID=?1 AND TIME < ?2 ",
            nativeQuery = true)
    Page<MessageUser> selfFindPageOnceSql(String id, long time, Pageable pageable);

    /**
     * 分页查询 JPQL    同时获取分页信息
     */
    @Query(value = "select t from MessageUser t WHERE ID=?1 AND TIME < ?2 order by TIME desc  ")
    Page<MessageUser> selfFindPageOnceJpql(String id, long time, Pageable pageable);


}
