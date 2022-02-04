package com.walker.dao;

import com.walker.mode.sys.FileIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * sharding 分库 & 分表
 */
@Repository
public interface FileIndexRepository extends JpaRepository<FileIndex, String>, JpaSpecificationExecutor {//实体类 主键类型 自定义查询
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
     *
     * @Cacheable 缓存方法操作
     */
    @Transactional
    @Modifying
    @Query("delete from FileIndex t where t.ID in (?1) ")
    Integer selfDeleteAll(List<String> ids);

    @Query("select t from FileIndex t where t.PATH in (?1) ")
    List<FileIndex> findsAllByPath(List<String> ids);

    @Query("select t from FileIndex t where t.CHECKSUM in (?1) ")
    List<FileIndex> findsAllByChecksum(Collection<String> checksum);

    @Query("delete from FileIndex t where t.CHECKSUM in (?1) ")
    Integer deleteAllByChecksum(Collection<String> checksum);


    @Query("select t from FileIndex t where t.PATH like CONCAT(?1, '%')  ")
    List<FileIndex> findsAllByStartPath(String startPath);


    @Transactional
    @Modifying
    @Query("delete from FileIndex t where t.PATH like CONCAT(?1, '%') ")
    Integer deleteAllByStartPath(String startPath);

    @Transactional
    @Modifying
    @Query("delete from FileIndex t where t.PATH in (?1) ")
    Integer deleteAllByPath(List<String> paths);

}
