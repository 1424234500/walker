package com.walker.spring.dao;

import com.walker.core.mode.school.Teacher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * spring data jpa方法命名规则
 * <p>
 * 关键字	方法命名	sql where字句
 * And	findByNameAndPwd	where name= ? and pwd =?
 * Or	findByNameOrSex	where name= ? or sex=?
 * Is,Equals	findById,findByIdEquals	where id= ?
 * Between	findByIdBetween	where id between ? and ?
 * LessThan	findByIdLessThan	where id < ?
 * LessThanEquals	findByIdLessThanEquals	where id <= ?
 * GreaterThan	findByIdGreaterThan	where id > ?
 * GreaterThanEquals	findByIdGreaterThanEquals	where id > = ?
 * After	findByIdAfter	where id > ?
 * Before	findByIdBefore	where id < ?
 * IsNull	findByNameIsNull	where name is null
 * isNotNull,NotNull	findByNameNotNull	where name is not null
 * Like	findByNameLike	where name like ?
 * NotLike	findByNameNotLike	where name not like ?
 * StartingWith
 * <p>
 * findByNameStartingWith	where name like '?%'
 * EndingWith	findByNameEndingWith	where name like '%?'
 * Containing	findByNameContaining	where name like '%?%'
 * OrderBy	findByIdOrderByXDesc	where id=? order by x desc
 * Not	findByNameNot	where name <> ?
 * In	findByIdIn(Collection<?> c)	where id in (?)
 * NotIn	findByIdNotIn(Collection<?> c)	where id not  in (?)
 * True
 * findByAaaTue
 * <p>
 * where aaa = true
 * False	findByAaaFalse	where aaa = false
 * IgnoreCase	findByNameIgnoreCase	where UPPER(name)=UPPER(?)
 * <p>
 * 获取全部字段，返回的类型就是对应的实体类或者实体类的集合。
 * 当不是全部字段时，返回类型就不能是实体类了。
 * <p>
 * 单个字段单条记录时，返回类型最好用对应字段的类型或者Object。
 * 单个字段多条记录时，返回类型最好用List<Object>或者List<字段对应类型>。
 * 多个字段时，不论是多条记录还是单条记录，返回类型都应该是List<Object[]>
 * <p>
 * Example api的组成
 * Probe: 含有对应字段的实例对象。
 * ExampleMatcher：ExampleMatcher携带有关如何匹配特定字段的详细信息，相当于匹配条件。
 * Example：由Probe和ExampleMatcher组成，用于查询。
 * 属性不支持嵌套或者分组约束，比如这样的查询 firstname = ?0 or (firstname = ?1 and lastname = ?2)
 * 灵活匹配只支持字符串类型，其他类型只支持精确匹配
 */


@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String>, JpaSpecificationExecutor {//实体类 主键类型 自定义查询
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
     * JPQL更新 占位符 表对应的 类名 属性名
     *
     * @CachePut 每次都执行方法 删除缓存
     */
    @Transactional
    @Modifying
    @Query(value = "insert into W_TEACHER(ID,NAME) values(?1,?2)", nativeQuery = true)
    Integer saveWithCache(String id, String name);

    /**
     * JPQL删除 并删除缓存
     */
    @Transactional
    @Modifying
    @Query("delete from Teacher t where t.ID=?1 ")
    @CacheEvict(keyGenerator = "keyGenerator", value = "cache-teacher")
    Integer deleteJPQLWithCache(String id);

    /**
     * JPQL查询 缓存
     *
     * @Cacheable 缓存方法操作
     */
    @Cacheable(keyGenerator = "keyGenerator", value = "cache-teacher")
    @Query("select t from Teacher t where t.ID=?1")
    Teacher findOneJPQLWithCache(String id);


    /**
     * 保存缓存 默认key生成 覆盖父类save? 复用sql
     */
//    @CachePut(key="#root.targetClass.name + #p0",value="cache-teacher-sk")
//    "cache-teacher-sk::org.springframework.data.jpa.repository.support.SimpleJpaRepositoryTeacher{ID='4', S_MTIME='null', S_ATIME='2019-10-29 10:02:52', S_FLAG='null', NAME='name4', SEX='null', LEVEL='null'}"
    @CachePut(key = "#p0.ID", value = "cache-teacher-sk")
//    "cache-teacher-sk::org.springframework.data.jpa.repository.support.SimpleJpaRepositoryTeacher{ID='4', S_MTIME='null', S_ATIME='2019-10-29 10:02:52', S_FLAG='null', NAME='name4', SEX='null', LEVEL='null'}"
    Teacher save(Teacher teacher);

    /**
     * 删除缓存 默认key生成
     */
    @Transactional
    @Modifying
    @Query("delete from Teacher t where t.ID=?1 ")
    @CacheEvict(key = "#p0", value = "cache-teacher-sk")
//    @CacheEvict(key="#root.targetClass.name + #p0.ID",value="cache-teacher-sk")
    Integer deleteJPQLWithCacheSimpleKey(String id);

    /**
     * JPQL查询 缓存 默认key生成
     */
    @Cacheable(key = "#p0", value = "cache-teacher-sk")
//    @Cacheable(key="#root.targetClass.name + #p0.ID",value="cache-teacher-sk")
    @Query("select t from Teacher t where t.ID=?1")
    Teacher findOneJPQLWithCacheSimpleKey(String id);


    /**
     * JPQL查询 缓存?    in语句 如果缓存 那么  key? 缓存共用导致 查询异常！！！！！！！！！！
     */
    @Query("select t from Teacher t where t.ID in (?1) ")
    List<Teacher> selfFindListCacheJPQL(List<String> ids);

    /**
     * JPQL查询 删除
     */
    @Transactional
    @Modifying
    @Query("delete from Teacher t where t.ID in (?1) ")
    Integer selfDeleteAll(List<String> ids);

    /**
     * nativeQuery sql 原生sql方式 占位符 表名 字段名
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE W_TEACHER t SET t.NAME =?1 WHERE t.ID=?2", nativeQuery = true)
    //占位符传值形式
    int selfUpdateSql(String name, String id);

    /**
     * JPQL  别名
     */
    @Query("select t from Teacher t where t.NAME=:name")
    Teacher selfFindByName(@Param("name") String name);

    /**
     * JPQL查询数量
     */
    @Query("select count(t.id) from Teacher t where t.NAME like CONCAT('%', CONCAT(?1, '%')) ")
    int selfCount(String name);

    /**
     * JPQL 分页查询定制 只获取数据
     */
    @Query("select t from Teacher t where t.NAME like CONCAT('%', CONCAT(?1, '%')) ")
    List<Teacher> selfFindPage(String name, Pageable page);

    /**
     * 分页查询 native    同时获取分页信息  cost 900
     */
    @Query(value = "select t.* from W_TEACHER t where t.NAME like CONCAT('%', CONCAT(?1, '%'))",
            countQuery = "select count(t.id) from W_TEACHER t where t.NAME like CONCAT('%', CONCAT(?1, '%'))",
            nativeQuery = true)
    Page<Teacher> selfFindPageOnceSql(String name, Pageable pageable);

    /**
     * 分页查询 JPQL    同时获取分页信息    cost 500
     */
    @Query(value = "select t from Teacher t where t.NAME like CONCAT('%', CONCAT(?1, '%'))")
    Page<Teacher> selfFindPageOnceJpql(String name, Pageable pageable);


}
