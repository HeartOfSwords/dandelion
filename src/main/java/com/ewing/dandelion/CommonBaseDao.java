package com.ewing.dandelion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 基本数据访问类。
 *
 * @author Ewing
 * @since 2017-03-04
 **/
@Repository
public class CommonBaseDao implements CommonDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonBaseDao.class);

    private JdbcOperations jdbcOperations;
    private NamedParameterJdbcOperations namedParamOperations;

    /**
     * 快速初始化的构造方法。
     */
    public CommonBaseDao() {
    }

    /**
     * 获取操作数据库的JdbcOperations。
     */
    @Override
    public JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    /**
     * 设置操作数据库的JdbcOperations。
     */
    @Override
    @Autowired
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    /**
     * 获取操作数据库的命名JdbcOperations。
     */
    @Override
    public NamedParameterJdbcOperations getNamedParamOperations() {
        return namedParamOperations;
    }

    /**
     * 设置操作数据库的命名JdbcOperations。
     */
    @Override
    @Autowired
    public void setNamedParamOperations(NamedParameterJdbcOperations namedParamOperations) {
        this.namedParamOperations = namedParamOperations;
    }

    /**
     * 把对象实例的所有属性插入到数据库。
     */
    @Override
    public boolean save(Object object) {
        if (object == null)
            throw new DaoException("实例对象为空！");
        String sql = SqlGenerator.getInsertValues(object);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 把配置对象积极属性对应的对象实例属性插入到数据库。
     */
    @Override
    public boolean savePositive(Object object, Object config) {
        if (object == null || config == null || !object.getClass().equals(config.getClass()))
            throw new DaoException("实例对象或配置对象为空或类型不匹配！");
        String sql = SqlGenerator.getInsertPositiveValues(object, config);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 把配置对象消极属性对应的对象实例属性插入到数据库。
     */
    @Override
    public boolean saveNegative(Object object, Object config) {
        if (object == null || config == null || !object.getClass().equals(config.getClass()))
            throw new DaoException("实例对象或配置对象为空或类型不匹配！");
        String sql = SqlGenerator.getInsertNegativeValues(object, config);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 把对象实例的所有属性更新到数据库。
     */
    @Override
    public boolean update(Object object) {
        if (object == null)
            throw new DaoException("实例对象为空！");
        String sql = SqlGenerator.getUpdateSetWhereIdEquals(object.getClass());
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 把配置对象积极属性对应的对象实例属性更新到数据库。
     */
    @Override
    public boolean updatePositive(Object object, Object config) {
        if (object == null || config == null || !object.getClass().equals(config.getClass()))
            throw new DaoException("实例对象或配置对象为空或类型不匹配！");
        String sql = SqlGenerator.getUpdatePositiveSetWhereIdEquals(config);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 把配置对象消极属性对应的对象实例属性更新到数据库。
     */
    @Override
    public boolean updateNegative(Object object, Object config) {
        if (object == null || config == null || !object.getClass().equals(config.getClass()))
            throw new DaoException("实例对象或配置对象为空或类型不匹配！");
        String sql = SqlGenerator.getUpdateNegativeSetWhereIdEquals(config);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 根据ID获取指定类型的对象的所有属性。
     */
    @Override
    public <T> T getObject(Class<T> clazz, Object id) {
        if (clazz == null || id == null)
            throw new DaoException("对象类型或对象ID为空！");
        String sql = SqlGenerator.getSelectFromWhereIdEquals(clazz);
        LOGGER.info(sql);
        try {
            return this.getJdbcOperations().queryForObject(sql, BeanPropertyRowMapper.newInstance(clazz), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据ID获取配置对象积极属性对应的对象属性。
     */
    @Override
    public <T> T getPositive(Object id, T config) {
        if (config == null || id == null)
            throw new DaoException("配置对象或对象ID为空！");
        String sql = SqlGenerator.getSelectPositiveFromWhereIdEquals(config);
        LOGGER.info(sql);
        try {
            return this.getJdbcOperations().queryForObject(sql, BeanPropertyRowMapper.newInstance((Class<T>) config.getClass()), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据ID获取配置对象消极属性对应的对象属性。
     */
    @Override
    public <T> T getNegative(Object id, T config) {
        if (config == null || id == null)
            throw new DaoException("配置对象或对象ID为空！");
        String sql = SqlGenerator.getSelectNegativeFromWhereIdEquals(config);
        LOGGER.info(sql);
        try {
            return this.getJdbcOperations().queryForObject(sql, BeanPropertyRowMapper.newInstance((Class<T>) config.getClass()), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据对象的ID属性删除对象。
     */
    @Override
    public boolean delete(Object object) {
        if (object == null)
            throw new DaoException("实例对象为空！");
        String sql = SqlGenerator.getDeleteFromWhereIdEquals(object.getClass(), true);
        LOGGER.info(sql);
        return this.getNamedParamOperations().update(sql, new BeanPropertySqlParameterSource(object)) > 0;
    }

    /**
     * 根据对象的ID属性删除指定类型的对象。
     */
    @Override
    public boolean deleteById(Class<?> clazz, Object id) {
        if (clazz == null || id == null)
            throw new DaoException("对象类型或ID对象为空！");
        String sql = SqlGenerator.getDeleteFromWhereIdEquals(clazz, false);
        LOGGER.info(sql);
        return this.getJdbcOperations().update(sql, id) > 0;
    }

    /**
     * 查询一个整数并封装成长整数。
     */
    @Override
    public long queryLong(String sql, Object... params) {
        if (sql == null)
            throw new DaoException("查询语句为空！");
        LOGGER.info(sql);
        return this.getJdbcOperations().queryForObject(sql, Long.class, params);
    }

    /**
     * 查询一条记录并封装成指定类型的对象。
     */
    @Override
    public <T> T queryObject(Class<T> clazz, String querySql, Object... params) {
        if (clazz == null || querySql == null)
            throw new DaoException("对象类型或查询语句为空！");
        LOGGER.info(querySql);
        try {
            return this.getJdbcOperations().queryForObject(querySql, BeanPropertyRowMapper.newInstance(clazz), params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询多条记录并封装成指定类型的对象集合。
     */
    @Override
    public <T> List<T> queryObjectList(Class<T> clazz, String querySql, Object... params) {
        if (clazz == null || querySql == null)
            throw new DaoException("对象类型或查询语句为空！");
        LOGGER.info(querySql);
        return this.getJdbcOperations().query(querySql, BeanPropertyRowMapper.newInstance(clazz), params);
    }

    /**
     * 查询一条记录并封装成Map。
     */
    @Override
    public Map queryMap(String querySql, Object... params) {
        if (querySql == null)
            throw new DaoException("查询语句为空！");
        LOGGER.info(querySql);
        try {
            return this.getJdbcOperations().queryForMap(querySql, params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询多条记录并封装成Map集合。
     */
    @Override
    public List<Map<String, Object>> queryMapList(String querySql, Object... params) {
        if (querySql == null)
            throw new DaoException("查询语句为空！");
        LOGGER.info(querySql);
        return this.getJdbcOperations().queryForList(querySql, params);
    }

}
