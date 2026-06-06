package com.bank.core.daoimpl;

import com.bank.common.pages.Pager;
import com.bank.core.dao.BaseDao;
import org.apache.logging.log4j.util.StringBuilders;
import org.hibernate.*;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.jdbc.object.SqlQuery;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.InstantiationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseHapiDaoimpl<Obj, PK extends Serializable> extends
        HibernateDaoSupport implements BaseDao<Obj, PK>{
    private static final Logger logger = LoggerFactory.getLogger(BaseHapiDaoimpl.class);
    private Class<Obj> persistentClass;

    public BaseHapiDaoimpl(Class<Obj> persistentClass) {
        this.persistentClass = persistentClass;
    }

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private void setSuperSessionFactory(EntityManagerFactory entityManagerFactory) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        super.setSessionFactory(sessionFactory);
    }

    /**
     * description 通过逐渐获取对象， getHibernateTemplate().load 会检索缓存，如果查不到信息会抛出异常
     * @references getHibernateTimple().get, 直接从数据库去，查不到信息返回null，不会检索缓存
     */
    public Obj getByPK(PK id){
        logger.debug("get: "+ id);
//        return (Obj) this.getHibernateTemplate().get(persistentClass, id);
        return (Obj) getSession().get(persistentClass, id);
    }
    /**
     * 查出所有对象
     */
    public List<Obj> list(){
        logger.debug("list: "+ persistentClass.getName());
        Obj obj = null;
        try{
            obj = persistentClass.newInstance();
        }catch (InstantiationException e){
            logger.error(e.getMessage(), e);
        }catch (IllegalAccessException e){
            logger.error(e.getMessage(), e);
        }
        return this.getHibernateTemplate().findByExample(obj);
    }

    public int listCount(){
        logger.debug("list:" + persistentClass.getName());
        long count = (long) getSession().createQuery(
                "select count(distinct model) from "
                        + persistentClass.getName() + " as model").list()
                .iterator().next();
        return Integer.parseInt(count + "");
    }

    public List<Obj> listPager(Pager pager){
        logger.debug("listPage:"+ persistentClass.getName());
        List list = getSession().createQuery(
                "from " + persistentClass.getName()).setFirstResult(
                        pager.getStartRow()).setMaxResults(pager.getPageSize()).list();
        return list;
    }

    /**
     * 删除对象
     */
    public void remove(Obj o){
        logger.debug("list:" + persistentClass.getName());
//        this.getHibernateTemplate().delete(o);
        getSession().delete(o);
    }

    /**
     * 保存或更新对象
     */
    public Object save(Obj o){
        logger.debug("save:" + persistentClass.getName());
//        Object obj = this.getHibernateTemplate().merge(o);
        Object obj = getSession().merge(o);
        return obj;
    }

    /**
     * 使用hql语句进行分页查询
     * @param hql 需要查询的hql语句
     * @param  offset 第一条记录索引
     * @param pageSize 每页需要显示的记录数
     * @return 当前所有记录
     */
    public List<Obj> findByPage(final String hql, final int offset, final int pageSize){
        List<Obj> list = getHibernateTemplate().execute(new HibernateCallback<List<Obj>>(){
            @Override
            public List<Obj> doInHibernate(Session session) throws HibernateException {
                List<Obj> result = session.createQuery(hql)
                        .setFirstResult(offset).setMaxResults(pageSize)
                        .list();
                return result;
            }
        });
        return list;
    }

    /**
     * 使用hql语句查询操作，不分页
     * @param hql 需要查询的hql语句
     */
    public List<Obj> findNoPage(final String hql){
        List<Obj> list = getHibernateTemplate().execute(new HibernateCallback<List<Obj>>(){
            @Override
            public List<Obj> doInHibernate(Session session) throws HibernateException {
                List<Obj> result = session.createQuery(hql).list();
                return result;
            }
        });
        return list;
    }

    /**
     * 使用hql语句进行分页查询操作
     * @param hql 需要查询的hql语句
     * @param value hql语句中传入参数的占位符
     * @param offset 第一条记录查询
     * @param pageSize 每页需要显示的记录数
     * @return 当页的所有记录
     */
    public List<Obj> findByPage(final String hql, final Object value, final int offset, final int pageSize){
        List<Obj> list = getHibernateTemplate().execute(new HibernateCallback<List<Obj>>(){
            @Override
            public List<Obj> doInHibernate(Session session) throws HibernateException {
                List<Obj> result = session.createQuery(hql).setProperties(value).setFirstResult(offset)
                        .setMaxResults(pageSize).list();
                return result;
            }
        });
        return list;
    }

    /**
     * 使用hql语句进行分页查询操作
     * @param hql 需要查询的hql语句
     * @param values hql语句中传入多个参数的占位符
     * @param offset 第一条记录查询
     * @param pageSize 每页需要显示的记录数
     * @return 当页的所有记录
     */
    public List<Obj> findByPage(final String hql, final Object[] values, final int offset, final int pageSize){
        List<Obj> list = getHibernateTemplate().execute(new HibernateCallback<List<Obj>>(){
            @Override
            public List<Obj> doInHibernate(Session session) throws HibernateException {
                Query query = session.createQuery(hql);
                for(int i=0; i<values.length; i++){
                    query.setParameter(i, values[i]);
                }
                List<Obj> result = query.setFirstResult(offset).setMaxResults(pageSize).list();
                return result;
            }
        });
        return list;
    }
    /**
     * 通过单个对象属性查找对象
     * @param propertyName
     * @param value
     * @return
     */
    public List<Obj> findByProperty(String propertyName, Object value){
        String queryString = "from " + persistentClass.getName()
                + " as model where model." + propertyName + "= ?0";
        Query queryObject = getSession().createQuery(queryString);
        queryObject.setParameter(0, value);
        return queryObject.list();
    }

    public int findByPropertyCount(String propertyName, Object value) {
        String queryString = "select count(distinct model) from "
                + persistentClass.getName() + " as model where model."
                + propertyName + "= ?0";
        long count = (Long) getSession().createQuery(queryString).setParameter(
                0, value).list().iterator().next();
        return Integer.parseInt(count + "");
    }

    /**
     * 通过多个对象属性查找对象，封装成数组
     * @param propertyNames
     * @param values
     * @return
     */
    public List<Obj> findByProperty(String[] propertyNames, Object[] values){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model ");

        int size = propertyNames.length;
        if(size > 0){
            queryString.append("where 1=1 ");
        }
        for(int i =0; i<size; i++){
            queryString.append("and model."+propertyNames[i] + "= ?"+i);
        }
        Query queryObject = getSession().createQuery(queryString.toString());
        for(int i=0; i<size; i++){
            queryObject.setParameter(i, values[i]);
        }
        return queryObject.list();
    }

    public int findByPropertyCount(String[] propertyNames, Object[] values){
        StringBuffer queryString = new StringBuffer();
        queryString.append("select count(distinct model) from "
                + persistentClass.getSimpleName() + " as model ");
        int size = propertyNames.length;
        if(size > 0){
            queryString.append("where 1=1 ");
        }

        for (int i=0; i<size; i++){
            queryString.append("and model."+propertyNames[i] + "= ?"+i);
        }

        Query queryObject = getSession().createQuery(queryString.toString());
        for(int i=0; i<size; i++){
            queryObject.setParameter(i, values[i]);
        }
        long count = (Long) queryObject.list().iterator().next();
        return Integer.parseInt(count + "");
    }

    /**
     * 通过单个对象属性模糊查找
     * @param propertyName
     * @para value
     * @return
     */
    public List<Obj> findByPropertyFuzzy(String propertyName, Object value){
        String queryString = "from " + persistentClass.getSimpleName()
                + " as model where model." + propertyName + " like ?0";
        Query queryObject = getSession().createQuery(queryString);
        queryObject.setParameter(0, "%"+value+"%");
        return queryObject.list();
    }

    public int findByPropertyFuzzyCount(String propertyName, Object value){
        String queryString = "select count(distinct model) from "
                + persistentClass.getName() + " as model where model."
                + propertyName + " like ?0";
        Query queryObject = getSession().createQuery(queryString);
        queryObject.setParameter(0, "%"+value+"%");
        long count = (Long) queryObject.list().iterator().next();
        return Integer.parseInt(count + "");
    }

    /**
     * 通过多个对象属性模糊查找，封装成数组
     * @param propertyNames
     * @param values
     * @return
     */
    public List<Obj> findByPropertyFuzzy(String[] propertyNames, Object[] values){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model ");

        int size = propertyNames.length;
        if(size > 0){
            queryString.append("where 1=1 ");
        }

        for(int i=0; i<size; i++){
            queryString.append("and model."+propertyNames[i] + " like ?"+i);
        }

        Query queryObject = getSession().createQuery(queryString.toString());
        for(int i=0; i<size; i++){
            queryObject.setParameter(i, "%"+values[i]+"%");
        }
        return queryObject.list();
    }

    public int findByPropertyFuzzyCount(String[] propertyNames, Object[] values){
        StringBuffer queryString = new StringBuffer();
        queryString.append("select count(distinct model) from "
                + persistentClass.getSimpleName() + " as model ");
        int size = propertyNames.length;
        if(size > 0){
            queryString.append("where 1=1 ");
        }

        for (int i=0; i<size; i++){
            queryString.append("and model."+propertyNames[i] + " like ?"+i);
        }

        Query queryObject = getSession().createQuery(queryString.toString());
        for(int i=0; i<size; i++){
            queryObject.setParameter(i, "%"+values[i]+"%");
        }
        long count = (Long) queryObject.list().iterator().next();
        return Integer.parseInt(count + "");
    }

    /**
     * 通过单个对象属性右模糊查找
     * @param propertyName
     * @param value
     * @return
     */
    public List<Obj> findByPropertyFuzzyRight(String propertyName, Object value){
        String queryString = "from " + persistentClass.getSimpleName()
                + " as model where model." + propertyName + " like ?0";
        Query queryObject = getSession().createQuery(queryString);
        queryObject.setParameter(0, value+"%");
        return queryObject.list();
    }

    public int findByPropertyFuzzyRightCount(String propertyName, Object value){
        String queryString = "select count(distinct model) from "
                + persistentClass.getSimpleName() + " as model where model."
                + propertyName + " like ?0";
        Query queryObject = getSession().createQuery(queryString);
        queryObject.setParameter(0, value+"%");
        long count = (Long) queryObject.list().iterator().next();
        return Integer.parseInt(count + "");
    }

    /**
     * 通过多个对象属性右查找，封装成数组
     * @param propertyNames
     * @param values
     * @return
     */
    public List<Obj> findByPropertyFuzzyRight(String[] propertyNames, Object[] values){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model ");

        int size = propertyNames.length;
        if(size > 0){
            queryString.append("where 1=1 ");
        }

        for(int i=0; i<size; i++){
            queryString.append("and model."+propertyNames[i] + " like ?"+i);
        }

        Query queryObject = getSession().createQuery(queryString.toString());
        for(int i=0; i<size; i++){
            queryObject.setParameter(i, values[i]+"%");
        }
        return queryObject.list();
    }

    public int findByPropertyFuzzyRightCount(String[] propertyNames, Object[] values){
        StringBuffer queryString = new StringBuffer();
        queryString.append("select count(distinct model) from "
                + persistentClass.getSimpleName() + " as model ");
        int size = propertyNames.length;
        if(size > 0){
            queryString.append("where 1=1 ");
        }

        for (int i=0; i<size; i++){
            queryString.append("and model."+propertyNames[i] + " like ?"+i);
        }

        Query queryObject = getSession().createQuery(queryString.toString());
        for(int i=0; i<size; i++){
            queryObject.setParameter(i, values[i]+"%");
        }
        long count = (Long) queryObject.list().iterator().next();
        return Integer.parseInt(count + "");
    }

    /**
     * 分页-通过单个对象属性查找对象
     * @param propertyName
     * @param value
     * @param pager
     * @return
     */
    public List<Obj> findByPropertyPage(String propertyName, Object value, Pager pager){
        String queryString = "from " + persistentClass.getSimpleName()
                + " as model where model." + propertyName + " = ?0";
        Query queryObject = getSession().createQuery(queryString);
        queryObject.setParameter(0, value);
        List list = queryObject.setFirstResult(pager.getStartRow()).setMaxResults(
                pager.getPageSize()).list();
        return list;
    }

    /**
     * 分页-通过多个对象属性查找对象，封装成数组
     * @param propertyNames
     * @param values
     * @param pager
     * @return
     */
    public List<Obj> findByPropertyPage(String[] propertyNames, Object[] values, Pager pager){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model ");

        int size = propertyNames.length;
        if(size > 0){
            queryString.append("where 1=1 ");
        }

        for(int i=0; i<size; i++){
            queryString.append("and model."+propertyNames[i] + " = ?"+i);
        }

        Query queryObject = getSession().createQuery(queryString.toString());
        for(int i=0; i<size; i++){
            queryObject.setParameter(i, values[i]);
        }
        List list = queryObject.setFirstResult(pager.getStartRow()).setMaxResults(
                pager.getPageSize()).list();
        return list;
    }

    /**
     * 分页-通过单个对象属性模糊查找对象
     * @param propertyName
     * @param value
     * @param pager
     * @return
     */
    public List<Obj> findByPropertyFuzzyPage(String propertyName, Object value, Pager pager){
        String queryString = "from " + persistentClass.getSimpleName()
                + " as model where model." + propertyName + " like ?0";
        Query queryObject = getSession().createQuery(queryString);
        queryObject.setParameter(0, "%"+value+"%");
        List list = queryObject.setFirstResult(pager.getStartRow()).setMaxResults(
                pager.getPageSize()).list();
        return list;
    }

    /**
     * 分页-通过多个对象属性模糊查找，封装成数组
     * @param propertyNames
     * @param values
     * @param pager
     * @return
     */
    public List<Obj> findByPropertyFuzzyPage(String[] propertyNames, Object[] values, Pager pager){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model ");

        int size = propertyNames.length;
        if(size > 0){
            queryString.append("where 1=1 ");
        }

        for(int i=0; i<size; i++){
            queryString.append("and model."+propertyNames[i] + " like ?"+i);
        }

        Query queryObject = getSession().createQuery(queryString.toString());
        for(int i=0; i<size; i++){
            queryObject.setParameter(i, "%"+values[i]+"%");
        }
        List list = queryObject.setFirstResult(pager.getStartRow()).setMaxResults(
                pager.getPageSize()).list();
        return list;
    }

    /**
     * 分页-通过单个对象属性右模糊查找对象
     * @param propertyName
     * @param value
     * @param pager
     * @return
     */
    public List<Obj> findByPropertyFuzzyRightPage(String propertyName, Object value, Pager pager){
        String queryString = "from " + persistentClass.getSimpleName()
                + " as model where model." + propertyName + " like ?0";
        Query queryObject = getSession().createQuery(queryString);
        queryObject.setParameter(0, value+"%");
        List list = queryObject.setFirstResult(pager.getStartRow()).setMaxResults(
                pager.getPageSize()).list();
        return list;
    }

    /**
     * 分页-通过多个对象属性右模糊查找对象，封装成数组
     * @param propertyNames
     * @param values
     * @param pager
     * @return
     */
    public List<Obj> findByPropertyFuzzyRightPage(String[] propertyNames, Object[] values, Pager pager){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model ");

        int size = propertyNames.length;
        if(size > 0){
            queryString.append("where 1=1 ");
        }

        for(int i=0; i<size; i++){
            queryString.append("and model."+propertyNames[i] + " like ?"+i);
        }

        Query queryObject = getSession().createQuery(queryString.toString());
        for(int i=0; i<size; i++){
            queryObject.setParameter(i, values[i]+"%");
        }
        List list = queryObject.setFirstResult(pager.getStartRow()).setMaxResults(
                pager.getPageSize()).list();
        return list;
    }

    /**
     * 分页-通过多个对象属性右模糊查找，封装成数组， 需要验证likeNames = likeValues 和 equalValues
     * @param likeNames
     * @param equalNames
     * @param likeValues
     * @param equalValues
     * @param pager
     * @return
     */
    public List<Obj> findByPropertyFuzzyRightPage(List<String> likeNames,
              List<String> equalNames, List<Object> likeValues,
              List<Integer> equalValues, Pager pager){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model ");

        int totalsize = likeNames.size() + equalNames.size();
        if(totalsize > 0){
            queryString.append("where 1=1 ");
        }

        //附加非主键模糊查询
        for(String lnames: likeNames){
            queryString.append("and model."+lnames+" like ? ");
        }

        //附加主键模糊查询
        for(String eqnames: equalNames){
            queryString.append("and model."+eqnames+" = ? ");
        }

        Query queryObject = getSession().createQuery(queryString.toString());
       int index = 0;
       for(Object valueObj: likeValues){
           queryObject.setParameter(index,
                   (valueObj instanceof String) ? valueObj + "%": valueObj);
           index++;
       }
       for (Integer valueObj: equalValues){
           queryObject.setParameter(index, valueObj);
           index++;
       }
       List list = queryObject.setFirstResult(pager.getStartRow()).setMaxResults(
                pager.getPageSize()).list();
       return list;
    }

    public int findByPropertyFuzzyRightCount(List<String> likeNames,
            List<String> equalNames, List<Object> likeValues,
            List<Integer> equalValues, String otherHql){
        StringBuffer queryString = new StringBuffer();
        queryString.append("select count(distinct model) from "
                + persistentClass.getSimpleName() + " as model ");
        int totalsize = likeNames.size() + equalNames.size();
        if(totalsize > 0){
            queryString.append("where 1=1 ");
        }

        //附加非主键模糊查询
        for(String lnames: likeNames){
            queryString.append("and model."+lnames+" like ? ");
        }

        //附加主键模糊查询
        for(String eqnames: equalNames){
            queryString.append("and model."+eqnames+" = ? ");
        }

        Query queryObject = getSession().createQuery(queryString.toString());
        int index = 0;
        for(Object valueObj: likeValues){
            queryObject.setParameter(index,
                    (valueObj instanceof String) ? valueObj + "%": valueObj);
            index++;
        }
        for (Integer valueObj: equalValues){
            queryObject.setParameter(index, valueObj);
            index++;
        }

        long count = (Long) queryObject.list().iterator().next();
        return Integer.parseInt(count + "");
    }

    /**
     * 分页-通过多个对象属性模糊查找，封装成数组， 需要验证likeNames = likeValues 和 equalValues
     * @param likeNames
     * @param equalNames
     * @param likeValues
     * @param equalValues
     * @param otherHql
     * @param pager
     * @return
     */
    public List<Obj> findByPropertyFuzzyPage(List<String> likeNames,
            List<String> equalNames, List<Object> likeValues,
            List<Integer> equalValues, String otherHql, Pager pager){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model ");

        int totalsize = likeNames.size() + equalNames.size();
        if(totalsize > 0){
            queryString.append("where 1=1 ");
        }
        if(otherHql !=null && otherHql.length()>0){
            queryString.append(otherHql + " ");
        }

        //附加非主键模糊查询
        for(String lnames: likeNames){
            queryString.append("and model."+lnames+" like ? ");
        }

        //附加主键查询
        for(String eqnames: equalNames){
            queryString.append("and model."+eqnames+" = ? ");
        }

        Query queryObject = getSession().createQuery(queryString.toString());
        int index = 0;
        for(Object valueObj: likeValues){
            queryObject.setParameter(index,
                    (valueObj instanceof String) ? "%" + valueObj + "%": valueObj);
            index++;
        }
        for (Integer valueObj: equalValues){
            queryObject.setParameter(index, valueObj);
            index++;
        }
        List list = queryObject.setFirstResult(pager.getStartRow()).setMaxResults(
                pager.getPageSize()).list();
        return list;
    }

    public int findByPropertyFuzzyPageCount(List<String> likeNames,
            List<String> equalNames, List<Object> likeValues,
            List<Integer> equalValues){
        StringBuffer queryString = new StringBuffer();
        queryString.append("select count(distinct model) from "
                + persistentClass.getSimpleName() + " as model ");
        int totalsize = likeNames.size() + equalNames.size();
        if(totalsize > 0){
            queryString.append("where 1=1 ");
        }

        //附加非主键模糊查询
        for(String lnames: likeNames){
            queryString.append("and model."+lnames+" like ? ");
        }

        //附加主键查询
        for(String eqnames: equalNames){
            queryString.append("and model."+eqnames+" = ? ");
        }

        Query queryObject = getSession().createQuery(queryString.toString());
        int index = 0;
        for(Object valueObj: likeValues){
            queryObject.setParameter(index,
                    (valueObj instanceof String) ? "%" + valueObj + "%": valueObj);
            index++;
        }
        for (Integer valueObj: equalValues){
            queryObject.setParameter(index, valueObj);
            index++;
        }
        long count = (Long) queryObject.list().iterator().next();
        return Integer.parseInt(count + "");
    }

    /**
     * 获取数据总数
     * @param hqlWhere WHERE约束直接加model.xx = xxvalue即可，不需要加where
     * @return
     */
    public int findByHqlWhereCount(String hqlWhere){
        StringBuffer queryString = new StringBuffer();
        queryString.append("select count(*) from "
                + persistentClass.getSimpleName() + " as model where 1=1 ");
        if(hqlWhere != null && hqlWhere.length() > 0) {
            queryString.append(hqlWhere);
        }

        long count = (Long) getSession().createQuery(queryString.toString())
                .list().iterator().next();
        return Integer.parseInt(count + "");
    }

    public int findByPrepareHqlWhereCount(String hqlWhere, Object[] paraArr){
        StringBuffer queryString = new StringBuffer();
        queryString.append("select count(*) from "
                + persistentClass.getSimpleName() + " as model where 1=1 ");
        if(hqlWhere != null && hqlWhere.length() > 0) {
            queryString.append(hqlWhere);
        }

        org.hibernate.query.Query query = getSession().createQuery(queryString.toString());
        if(paraArr!=null && paraArr.length>0){
            for(int i=0; i<paraArr.length; i++){
                Object para = paraArr[i];
                query.setParameter(i, para);
            }
        }

        long count = (Long) query.list().iterator().next();
        return Integer.parseInt(count + "");
    }

    public int findByPrepareHqlWhereCount(String hqlWhere, Map<String, Object> paraMap){
        StringBuffer queryString = new StringBuffer();
        queryString.append("select count(*) from "
                + persistentClass.getSimpleName() + " as model where 1=1 ");
        if(hqlWhere != null && hqlWhere.length() > 0) {
            queryString.append(hqlWhere);
        }
        org.hibernate.query.Query query = getSession().createQuery(queryString.toString());
        if(paraMap!=null) {
            Set<Map.Entry<String, Object>> entries = paraMap.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                String key = entry.getKey();
                Object value = entry.getValue();
                query.setParameter(key, value);
            }
        }
        long count = (Long) query.list().iterator().next();
        return Integer.parseInt(count + "");
    }
    /**
     * 获取数据
     * @param hqlWhere
     * @param pager
     * @return
     */
    public List<Obj> findByHqlWherePage(String hqlWhere, Pager pager){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model where 1=1 ");
        if(hqlWhere != null && hqlWhere.length() > 0) {
            queryString.append(hqlWhere);
        }
        List list = getSession().createQuery(queryString.toString())
                .setFirstResult(pager.getStartRow()).setMaxResults(pager.getPageSize()).list();
        return list;
    }

    @Override
    public List<Obj> findByPrepareHqlWherePage(String hqlWhere, Object[] paraArr, Pager pager){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model where 1=1 ");
        if(hqlWhere != null && hqlWhere.length() > 0) {
            queryString.append(hqlWhere);
        }
        org.hibernate.query.Query query = getSession().createQuery(queryString.toString());
        if(paraArr!=null && paraArr.length>0){
            for(int i=0; i<paraArr.length; i++){
                Object para = paraArr[i];
                query.setParameter(i, para);
            }
        }
        query.setFirstResult(pager.getStartRow()).setMaxResults(pager.getPageSize());
        List list = query.list();
        return list;
    }

    @Override
    public List<Obj> findByPrepareSqlWherePageSelect(String hqlWhere, Object[] paraArr, Pager pager, Class entity){
        StringBuffer queryString = new StringBuffer();
        queryString.append(hqlWhere);
        SQLQuery query = getSession().createSQLQuery(queryString.toString());
        if(paraArr!=null && paraArr.length>0){
            for(int i=0; i<paraArr.length; i++){
                Object para = paraArr[i];
                query.setParameter(i, para);
            }
        }
        query.addEntity(entity).setFirstResult(pager.getStartRow()).setMaxResults(pager.getPageSize());
        List list = query.list();
        return list;
    }

    @Override
    public List<Obj> findByPrepareSqlWhereSelectNoPage(String hqlWhere, Object[] paraArr, Class entity){
        StringBuffer queryString = new StringBuffer();
        queryString.append(hqlWhere);
        SQLQuery query = getSession().createSQLQuery(queryString.toString());
        if(paraArr!=null && paraArr.length>0){
            for(int i=0; i<paraArr.length; i++){
                Object para = paraArr[i];
                query.setParameter(i, para);
            }
        }
        query.addEntity(entity);
        List list = query.list();
        return list;
    }

    @Override
    public List<Object[]> findBySqlObjListPrepareSqlPage(String sql, Pager pager, Object[] params){
        List<Object[]> objList = new ArrayList<Object[]>();
        SQLQuery queryObject = getSession().createSQLQuery(sql);
        queryObject.setFirstResult(pager.getStartRow()).setMaxResults(pager.getPageSize());
        if(params!=null){
            for(int i=0; i<params.length; i++){
                queryObject.setParameter(i+1, params[i]);
            }
        }
        objList = queryObject.list();
        return adjustResult(objList, pager);
    }

    @Override
    public int findCountByPrepareSql(String sql, Object[] params){
        SQLQuery queryObject = getSession().createSQLQuery(sql);
        if(params!=null){
            for(int i=0; i<params.length; i++){
                queryObject.setParameter(i+1, params[i]);
            }
        }
        String count = queryObject.list().iterator().next().toString();
        return Integer.valueOf(count);
    }

    @Override
    public List<Obj> findByPrepareHqlWherePage(String hqlWhere, Map<String, Object> paraMap, Pager pager){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model where 1=1 ");
        if(hqlWhere != null && hqlWhere.length() > 0) {
            queryString.append(hqlWhere);
        }
        org.hibernate.query.Query query = getSession().createQuery(queryString.toString());
        if(paraMap!=null) {
            Set<Map.Entry<String, Object>> entries = paraMap.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                String key = entry.getKey();
                Object value = entry.getValue();
                query.setParameter(key, value);
            }
        }
        query.setFirstResult(pager.getStartRow()).setMaxResults(pager.getPageSize());
        List list = query.list();
        return list;
    }

    /**
     * 获取数据
     * @param hqlWhere
     * @return
     */
    public List<Obj> findByHqlWhere(String hqlWhere){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model where 1=1 ");
        if(hqlWhere != null && hqlWhere.length() > 0) {
            queryString.append(hqlWhere);
        }
        return getSession().createQuery(queryString.toString()).list();
    }

    @Override
    public List<Obj> findByPrepareHqlWhere(String hqlWhere, Object[] paraArr){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model where 1=1 ");
        if(hqlWhere != null && hqlWhere.length() > 0) {
            queryString.append(hqlWhere);
        }
        org.hibernate.query.Query query = getSession().createQuery(queryString.toString());
        if(paraArr!=null && paraArr.length>0){
            for(int i=0; i<paraArr.length; i++){
                Object para = paraArr[i];
                query.setParameter(i, para);
            }
        }
        return query.list();
    }

    @Override
    public List<Obj> findByPrepareHqlWhere(String hqlWhere, Map<String, Object> paraMap){
        StringBuffer queryString = new StringBuffer();
        queryString.append("from " + persistentClass.getSimpleName()
                + " as model where 1=1 ");
        if(hqlWhere != null && hqlWhere.length() > 0) {
            queryString.append(hqlWhere);
        }
        org.hibernate.query.Query query = getSession().createQuery(queryString.toString());
        if(paraMap!=null) {
            Set<Map.Entry<String, Object>> entries = paraMap.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                String key = entry.getKey();
                Object value = entry.getValue();
                query.setParameter(key, value);
            }
        }
        return query.list();
    }

    @Override
    public List<?> findByPrepareSqlWhereSelect(String hqlWhere, Object[] paraArr){
        StringBuffer queryString = new StringBuffer();
        queryString.append(hqlWhere);
        SQLQuery query = getSession().createSQLQuery(queryString.toString());
        if(paraArr!=null && paraArr.length>0){
            for(int i=0; i<paraArr.length; i++){
                Object para = paraArr[i];
                query.setParameter(i+1, para);
            }
        }
        return query.list();
    }

    public void flushSession(){this.getHibernateTemplate().flush();}

    public List<?> findBySql(String sql){
        SQLQuery queryObject = getSession().createSQLQuery(sql);
        return queryObject.list();
    }

    public List<Obj> findBySqlPage(String sql, Pager pager){
        SQLQuery queryObject = getSession().createSQLQuery(sql);
        queryObject.setFirstResult(pager.getStartRow()).setMaxResults(pager.getPageSize());
        return queryObject.list();
    }

    public List<Object> findByPrepareSqlPage(String sql, Pager pager, Object[] params){
        NativeQuery queryObject = getSession().createSQLQuery(sql);
        if(params!=null){
            for(int i = 0; i<params.length; i++){
                queryObject.setParameter(i+1, params[i]);
            }
        }
        queryObject.setFirstResult(pager.getStartRow()).setMaxResults(pager.getPageSize());
        return queryObject.list();
    }

    public List<Obj> findBySql(String sql, Class entity){
        SQLQuery queryObject = getSession().createSQLQuery(sql);
        return queryObject.addEntity(entity).list();
    }

    public List<Object[]> findBySqlObjList(String sql){
        List<Object[]> objList = new ArrayList<Object[]>();
        SQLQuery queryObject = getSession().createSQLQuery(sql);
        objList = queryObject.list();
        return objList;
    }

    public List<Object[]> findBySqlObjListByPager(String sql, Pager pager){
        List<Object[]> objList = new ArrayList<Object[]>();
        SQLQuery queryObject = getSession().createSQLQuery(sql);
        queryObject.setFirstResult(pager.getStartRow()).setMaxResults(pager.getPageSize());
        objList = queryObject.list();
        return adjustResult(objList, pager);
    }

    /**
     * 将查询结果中后置的行号调整为前置，以消除hibernate3升级5的影响
     * @param objList
     * @return
     */
    private List<Object[]> adjustResult(List<Object[]> objList, Pager pager){
        List<Object[]> newList = new ArrayList<Object[]>();
        if(pager.getStartRow()>0){
            for(Object[] objects: objList){
                Object[] newObjects = new Object[objects.length];
                for(int i=0; i<objects.length-1; i++){
                    newObjects[i+1] = objects[i];
                }
                newObjects[0] = objects[objects.length-1];
                newList.add(newObjects);
            }
        }else{
            for(int j=0; j<objList.size(); j++){
                Object[] objects = objList.get(j);
                Object[] newObjects = new Object[objects.length+1];
                for(int i=0; i<objects.length; i++){
                    newObjects[i+1] = objects[i];
                }
                newObjects[0] = j+1;
                newList.add(newObjects);
            }
        }
        return newList;
    }

    public List<Obj> findBySqlPage(String sql, Class entity, Pager pager){
        SQLQuery queryObject = getSession().createSQLQuery(sql);
        queryObject.addEntity(entity).setFirstResult(pager.getStartRow()).setMaxResults(pager.getPageSize());
        return queryObject.list();
    }

    public int findBySqlCount(String sql){
        SQLQuery queryObject = getSession().createSQLQuery("select count(*) from ( "+ sql+") usertable");
        String count = queryObject.list().iterator().next().toString();
        return Integer.valueOf(count);
    }

    public int findByPrepareSqlCount(String sql, Object[] params){
        SQLQuery queryObject = getSession().createSQLQuery("select count(*) from ( "+ sql+") usertable");
        if(params!=null){
            for(int i=0; i<params.length; i++){
                queryObject.setParameter(i+1, params[i]);
            }
        }
        String count = queryObject.list().iterator().next().toString();
        return Integer.valueOf(count);
    }

    public int executeSql(String sql){
        SQLQuery queryObject = getSession().createSQLQuery(sql);
        return queryObject.executeUpdate();
    }

    //执行sql，返回执行成功条数，只适用于update和delete
    public int executeSqlByPrepareSqlPage(String sql, Object[] params){
        NativeQuery queryObject = getSession().createSQLQuery(sql);
        if(params!=null){
            for(int i=0; i<params.length; i++){
                queryObject.setParameter(i+1, params[i]);
            }
        }
        return queryObject.executeUpdate();
    }
    public String generatePK(String pk, String sign, String len){
        int leng = Integer.valueOf(len);
        StringBuffer sql = new StringBuffer("select max(a.").append(pk)
                .append(") from ")
                .append(persistentClass.getSimpleName())
                .append(" as a where a.")
                .append(pk)
                .append(" like '")
                .append(sign)
                .append("%'");
        List ls = getSession().createQuery(sql.toString()).list();
        String max = (String)ls.get(0);
        int i = 0;
        //首次添加记录，记录类似BT000000000001
        if(max==null || "".equals(max.trim())){
            max = "1";
            for(;i<leng-sign.length()-1;i++){
                max = "0" + max;
            }
            i=0;
            return sign+max;
        } else if (max!=null && max.length()<=leng) { // 非第一次操作，记录没有超出读取配置文件长度
            max = max.replaceAll(sign, "");
            Long imax = Long.parseLong(max)+1;
            String returnnum = String.valueOf(imax);
            int zero = leng - sign.length() - returnnum.length();
            for(;i<zero;i++){
                returnnum = "0" + returnnum;
            }
            i=0;
            return sign+returnnum;
        }else{ // 非第一次操作，记录超出读取配置文件长度
            leng = max.length();
            max = max.replaceAll(sign, "");
            Long imax = Long.parseLong(max)+1;
            String returnnum = String.valueOf(imax);
            int zero = leng - sign.length() - returnnum.length();
            for(;i<zero;i++){
                returnnum = "0" + returnnum;
            }
            return sign+returnnum;
        }
    }

    @Override
    public Object executeQuery(String sql){
        SQLQuery queryObject = getSession().createSQLQuery(sql);
        return queryObject.list().iterator().next();
    }

    public Session getSession(){
//        Session session = entityManagerFactory.unwrap(SessionFactory.class).getCurrentSession();
        Session session = entityManager.unwrap(Session.class);
        return session;
    }
}
