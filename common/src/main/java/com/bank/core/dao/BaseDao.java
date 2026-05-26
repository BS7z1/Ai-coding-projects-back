package com.bank.core.dao;

import com.bank.common.pages.Pager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseDao<Obj, PK extends Serializable> {
    public Object save(Obj o);

    public void remove(Obj o);

    public Obj getByPK(PK id);

    public List<Obj> list();

    public List<Obj> listPager(Pager pager);

    public List<Obj> findNoPage(final String hql);

    public List<Obj> findByPage(final String hql, final int offset, final int pageSize);

    public List<Obj> findByPage(final String hql, final Object value, final int offset, final int pageSize);

    public List<Obj> findByPage(final String hql, final Object[] values, final int offset, final int pageSize);

    public List<Obj> findByProperty(String propertyName, Object value);

    public List<Obj> findByProperty(String[] propertyNames, Object[] values);

    public List<Obj> findByPropertyFuzzy(String propertyName, Object value);

    public List<Obj> findByPropertyFuzzy(String[] propertyNames, Object[] values);

    public List<Obj> findByPropertyFuzzyRight(String propertyName, Object value);

    public List<Obj> findByPropertyFuzzyRight(String[] propertyNames, Object[] values);

    public int listCount();

    public int findByPropertyCount(String propertyName, Object value);

    public int findByPropertyCount(String[] propertyNames, Object[] values);

    public int findByPropertyFuzzyCount(String propertyName, Object value);

    public int findByPropertyFuzzyCount(String[] propertyNames, Object[] values);

    public int findByPropertyFuzzyRightCount(String propertyName, Object value);

    public int findByPropertyFuzzyRightCount(String[] propertyNames, Object[] values);

    public List<Obj> findByPropertyPage(String propertyName, Object value, Pager pager);

    public List<Obj> findByPropertyPage(String[] propertyNames, Object[] values, Pager pager);

    public List<Obj> findByPropertyFuzzyPage(String propertyName, Object value, Pager pager);

    public List<Obj> findByPropertyFuzzyPage(String[] propertyNames, Object[] values, Pager pager);

    public List<Obj> findByPropertyFuzzyRightPage(String propertyName, Object value, Pager pager);

    public List<Obj> findByPropertyFuzzyRightPage(String[] propertyNames, Object[] values, Pager pager);

    public List<Obj> findByHqlWhere(String hqlWhere);

    /**
     * 预编译hql条件查询,?0为hql占位符
     * @param hqlWhere
     * @param paraArr
     */
    public List<Obj> findByPrepareHqlWhere(String hqlWhere, Object[] paraArr);

    /**
     * 预编译hql条件查询,:paraName为hql占位符
     * @param hqlWhere
     * @param paraMap
     */
    public List<Obj> findByPrepareHqlWhere(String hqlWhere, Map<String, Object> paraMap);

    /**
     * 自定义sql预编译条件查询，无分页条件，返回object[]的list
     */

    List<?> findByPrepareSqlWhereSelect(String hqlWhere, Object[] paraArr);

    public List<Obj> findByHqlWherePage(String hqlWhere, Pager pager);

    /**
     * 预编译sql分页查询
     * @param hqlWhere
     * @param paraArr
     * @param pager
     * @return
     */
    public List<Obj> findByPrepareHqlWherePage(String hqlWhere, Object[] paraArr, Pager pager);

    /**
     * 自定义预编译sql分页查询，返回实体类的List
     * @param hqlWhere
     * @param paraArr
     * @param pager
     * @param entity
     * @return
     */
    List<Obj> findByPrepareSqlWherePageSelect(String hqlWhere, Object[] paraArr, Pager pager, Class entity);

    /**
     * 自定义预编译hql查询，返回实体类的List，无分类
     * @param hqlWhere
     * @param paraArr
     * @param entity
     * @return
     */
    List<Obj> findByPrepareSqlWhereSelectNoPage(String hqlWhere, Object[] paraArr, Class entity);

    /**
     * 自定义预编译sql查询，返回Object类的List
     * @param sql
     * @param pager
     * @param params
     * @return
     */
    List<Obj> findBySqlObjListPrepareSqlPage(String sql, Pager pager, Object[] params);

    /**
     * 自定义预编译sql查询行数
     * @param sql
     * @param params
     * @return
     */
    int findCountByPrepareSql(String sql, Object[] params);

    /**
     * 预端hql分页查海，:paraName 为hql占位符
     * @param hqlWhere
     * @param paraMap
     * @param pager
     * @return
     */
    public List<Obj> findByPrepareHqlWherePage (String hqlWhere, Map<String, Object> paraMap, Pager pager);

    public int findByHqWhereCount(String hqlWhere);

    /**
     * 预编译hql条件查询数量统计
     * @param hqlWhere
     * @param paraArr
     * @return
     */
    public int findByprepareHqlWhereCount(String hqlWhere, Object[] paraArr);

    /**
     * 预编译hql条件查询数量统计
     * @param hqlWhere
     * @param paraMap
     * @return
     */
    public int findByprepareHqlWhereCount(String hqlWhere, Map<String, Object> paraMap);

    public void flushSession();

    public List findBySql(String sql);

    public List<Obj> findBySql(String sql, Class entity);

    public List findBySqlPage(String sql, Pager pager);

    public List<Obj> findBySqlPage(String sql, Class entity, Pager pager);

    public int executeSql(String sql);

    public List<Object[]> findBySqlObjList(String sql);

    public List<Object[]> findBySqlObjListByPager(String sql, Pager pager);

    public int findBySqlCount(String sql);

    public Object executeQuery(String sql);

    public List<Object> findByPrepareSqlPage(String sql, Pager pager, Object[] params);

    public int findByPrepareSqlCount(String sql, Object[] params);

    public int executeSqlByPrepareSqlPage(String sql, Object[] params);
}
