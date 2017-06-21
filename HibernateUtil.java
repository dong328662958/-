package com.huawei.test1.util;

import com.huawei.test1.pojo.User;
import com.huawei.test1.pojo.User1;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import java.util.List;

/*
 * 文 件 名:  HibernateUtil
 * 描    述:  Hibernate工具封装类
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().
                    addAnnotatedClass(User.class).
                    addAnnotatedClass(User1.class).
                    configure().
                    buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session;
    }

    /**
     * 查询所有数据,应用于一次查询所有数据情况
     * Criteria 查询关联表时有重复数据问题.不能作为分页查询
     *使用方法
     *  List<User1> user1s1 = HibernateUtil.queryAll(User1.class);
     */
    public static <T> List<T> queryAll(Class<T> class1) {
        List<T> result = null;
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Criteria criteria = session.createCriteria(class1);
            //去重操作
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            result = criteria.list();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * 根据 ID组 查询一组数据
     * 使用方式:
     * List<Integer> ids = new ArrayList<Integer>();
     * ids.add(1);
     * ids.add(2);
     * List<User1> user1s = HibernateUtil.queryFilter(User1.class, ids);
     */
    public static <T> List<T> queryFilter(Class<T> class1, List<Integer> ids) {
        List<T> result = null;
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {

            Criteria criteria = session.createCriteria(class1);
            criteria.add(Restrictions.in("id", ids));
            //去重
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            result = criteria.list();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * 根据ID查询一条数据
     * 使用方式:
     * User result = HibernateUtil.query(User.class, 2);
     */
    public static <T> T query(Class<T> class1, Integer id) {
        T result = null;
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            result = session.get(class1, id);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }


    /**
     * 分页查询
     * 规则: 表名必须和类名保持一致
     * 使用方式:
     * List<User1> result = HibernateUtil.queryPage(User1.class,1,3);
     *
     * @param class1
     * @param pageNum 页数,第几页
     * @param limit   查询多少个
     */
    public static <T> List<T> queryPage(Class<T> class1, int pageNum, int limit) {
        List<T> result = null;
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        String hql = "FROM " + class1.getSimpleName();
        try {
            Query query = session.createQuery(hql);
            query.setFirstResult((pageNum - 1) * limit);
            query.setMaxResults(limit);
            result = query.list();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }


    /**
     * 添加数据
     * 使用方法:
     * integer  = HibernateUtil.add(user1);
     */
    public static Integer add(Object object) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Integer id = null;
        try {
            id = (Integer) session.save(object);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return id;
    }


    /**
     * 更新数据
     * 使用方式
     * HibernateUtil.update(User.class, 2, new HibernateUtil.OnUpdateListen<User>() {
     *
     * @Override public User onUpdate(User result) {
     * result.setName("new name");
     * result.setAge("new age");
     * return result;
     * }
     * });
     */

    public static <T> void update(Class<T> class1, Integer id, OnUpdateListen onUpdateListen) {
        T result = null;
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            result = session.get(class1, id);
            if (onUpdateListen != null) {
                Object obj = onUpdateListen.onUpdate(result);
                session.update(obj);
                transaction.commit();
            }

        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public interface OnUpdateListen<T> {
        T onUpdate(T result);
    }

    /**
     * 根据id删除数据
     * 使用方法
     * HibernateUtil.delete(User.class,3);
     */
    public static <T> void delete(Class<T> class1, Integer id) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            T result = session.get(class1, id);
            session.delete(result);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * 删除所有数据
     */

    /**
     * 删除指定的几条数据
     */

}
