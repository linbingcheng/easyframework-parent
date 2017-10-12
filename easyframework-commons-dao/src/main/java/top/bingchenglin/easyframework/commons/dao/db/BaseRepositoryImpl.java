package top.bingchenglin.easyframework.commons.dao.db;


import top.bingchenglin.easyframework.commons.dao.db.BaseRepository;
import top.bingchenglin.easyframework.commons.dao.db.query.DBQueryConverter;
import top.bingchenglin.easyframework.commons.util.FreeMarkerTools;
import top.bingchenglin.easyframework.commons.dao.param.DBQueryParam;
import top.bingchenglin.easyframework.commons.dao.param.DataPackage;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private static final Logger LOGGER = LogManager.getLogger(BaseRepositoryImpl.class);

    private EntityManager entityManager;

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public DataPackage query(DBQueryParam queryParam) {
        // todo 后续使用 org.springframework.data.jpa.domain.Specification 看性能是否有提升？
        DataPackage dp = new DataPackage();

        DBQueryConverter queryConverter = new DBQueryConverter(getDomainClass());
        queryConverter.perform(queryParam);

        // 获取总数量
        long total = queryCount(queryConverter);
        // 当查询页大于总页数时，更新查询页为总页数
        int totalPages = dp.getTotalPages(total, queryParam.getLimit());
        if (queryParam.getPage() > totalPages) {
            queryParam.setPage(totalPages);
        }
        dp.setMeta(total, queryParam.getPage(), queryParam.getLimit());

        // 获取分页数据
        List data = queryData(queryConverter, queryParam);
        dp.setData(data);
        return dp;
    }

    @Override
    public long queryCount(DBQueryParam queryParam) {
        DBQueryConverter queryConverter = new DBQueryConverter(getDomainClass());
        queryConverter.perform(queryParam);

        long total = queryCount(queryConverter);
        return total;
    }

    @Override
    public List queryData(DBQueryParam queryParam) {
        DBQueryConverter queryConverter = new DBQueryConverter(getDomainClass());
        queryConverter.perform(queryParam);

        List data = queryData(queryConverter, queryParam);
        return data;
    }

    /**
     * 查询总数量
     *
     * @param queryConverter
     * @return
     */
    private long queryCount(DBQueryConverter queryConverter) {
        String countStatement = queryConverter.getCountStatement();
        LOGGER.debug("CountStatement: {}", countStatement);

        javax.persistence.Query query = entityManager.createQuery(countStatement);
        Map<String, Object> properties = queryConverter.getParameters();
        for (Map.Entry<String, Object> prop : properties.entrySet()) {
            query.setParameter(prop.getKey(), prop.getValue());
        }
        setQueryCache(query, "QUERY_COUNT_" + queryConverter.toString());

        long total = (Long) query.getSingleResult();
        return total;
    }

    /**
     * 查询分页数据
     *
     * @param queryConverter
     * @param queryParam
     * @return
     */
    private List queryData(DBQueryConverter queryConverter, DBQueryParam queryParam) {
        String dataStatement = queryConverter.getDataStatement(queryParam);
        LOGGER.debug("DataStatement: {}", dataStatement);

        javax.persistence.Query query = entityManager.createQuery(dataStatement);
        Map<String, Object> properties = queryConverter.getParameters();
        for (Map.Entry<String, Object> prop : properties.entrySet()) {
            query.setParameter(prop.getKey(), prop.getValue());
        }
        query.setFirstResult(queryParam.getStart());
        query.setMaxResults(queryParam.getLimit());
        setQueryCache(query, "QUERY_DATA_" + queryConverter.toString());

        List data = query.getResultList();
        return data;
    }

    /**
     * 设置查询缓存
     *
     * @param query
     * @param cacheRegion
     */
    private void setQueryCache(javax.persistence.Query query, String cacheRegion) {
        if (query instanceof org.hibernate.jpa.internal.QueryImpl) {
            ((org.hibernate.jpa.internal.QueryImpl) query).getHibernateQuery().setCacheable(true);
            ((org.hibernate.jpa.internal.QueryImpl) query).getHibernateQuery().setCacheRegion(cacheRegion);
            LOGGER.debug("set query cache region: {}", cacheRegion);
        }
    }

    @Override
    public DataPackage queryByNamed(DBQueryParam queryParam, String namedQuery) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.getNamedQuery(namedQuery);
        String querySql = query.getQueryString();
        querySql = FreeMarkerTools.parseText(querySql, queryParam.getQueryConditions());

        SQLQuery sqlQuery = session.createSQLQuery(querySql);
        String[] namedParameters = sqlQuery.getNamedParameters();

        DBQueryConverter queryConverter = new DBQueryConverter(getDomainClass());
        queryConverter.perform(queryParam);
        queryConverter.setNamedParameters(queryParam, namedParameters, namedQuery);

        return queryBySql(queryConverter, queryParam, querySql);
    }

    @Override
    public long queryCountByNamed(DBQueryParam queryParam, String namedQuery) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.getNamedQuery(namedQuery);
        String querySql = query.getQueryString();
        querySql = FreeMarkerTools.parseText(querySql, queryParam.getQueryConditions());

        SQLQuery sqlQuery = session.createSQLQuery(querySql);
        String[] namedParameters = sqlQuery.getNamedParameters();

        DBQueryConverter queryConverter = new DBQueryConverter(getDomainClass());
        queryConverter.perform(queryParam);
        queryConverter.setNamedParameters(queryParam, namedParameters, namedQuery);

        long total = querySqlCount(queryConverter, querySql);
        return total;
    }

    @Override
    public List queryDataByNamed(DBQueryParam queryParam, String namedQuery) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.getNamedQuery(namedQuery);
        String querySql = query.getQueryString();
        querySql = FreeMarkerTools.parseText(querySql, queryParam.getQueryConditions());

        SQLQuery sqlQuery = session.createSQLQuery(querySql);
        String[] namedParameters = sqlQuery.getNamedParameters();

        DBQueryConverter queryConverter = new DBQueryConverter(getDomainClass());
        queryConverter.perform(queryParam);
        queryConverter.setNamedParameters(queryParam, namedParameters, namedQuery);

        List data = querySqlData(queryConverter, queryParam, querySql);
        return data;
    }

    private DataPackage queryBySql(DBQueryConverter queryConverter, DBQueryParam queryParam, String querySql) {
        DataPackage dp = new DataPackage();

        // 获取总数量
        long total = querySqlCount(queryConverter, querySql);
        // 当查询页大于总页数时，更新查询页为总页数
        int totalPages = dp.getTotalPages(total, queryParam.getLimit());
        if (queryParam.getPage() > totalPages) {
            queryParam.setPage(totalPages);
        }
        dp.setMeta(total, queryParam.getPage(), queryParam.getLimit());

        // 获取分页数据
        List data = querySqlData(queryConverter, queryParam, querySql);
        dp.setData(data);
        return dp;
    }

    private long querySqlCount(DBQueryConverter queryConverter, String querySql) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String sqlCountStatement = queryConverter.getSQLCountStatement(querySql);
        LOGGER.debug("SqlCountStatement: {}", sqlCountStatement);

        SQLQuery sqlQuery = session.createSQLQuery(sqlCountStatement);
        Map<String, Object> properties = queryConverter.getParameters();
        for (Map.Entry<String, Object> prop : properties.entrySet()) {
            Object value = prop.getValue();
            if (value instanceof Collection) {
                sqlQuery.setParameterList(prop.getKey(), (Collection) value);
            } else {
                sqlQuery.setParameter(prop.getKey(), value);
            }
        }
        setQueryCache(sqlQuery, "QUERY_COUNT_" + queryConverter.getNamedQuery());

        sqlQuery.addScalar(queryConverter.getAsCount(), StandardBasicTypes.LONG);
        long total = (Long) sqlQuery.uniqueResult();
        return total;
    }

    private List querySqlData(DBQueryConverter queryConverter, DBQueryParam queryParam, String querySql) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String sqlDataStatement = queryConverter.getSQLDataStatement(queryParam, querySql);
        LOGGER.debug("SqlDataStatement: {}", sqlDataStatement);

        SQLQuery sqlQuery = session.createSQLQuery(sqlDataStatement);
        Map<String, Object> properties = queryConverter.getParameters();
        for (Map.Entry<String, Object> prop : properties.entrySet()) {
            Object value = prop.getValue();
            if (value instanceof Collection) {
                sqlQuery.setParameterList(prop.getKey(), (Collection) value);
            } else {
                sqlQuery.setParameter(prop.getKey(), value);
            }
        }
        sqlQuery.setFirstResult(queryParam.getStart());
        sqlQuery.setMaxResults(queryParam.getLimit());
        setQueryCache(sqlQuery, "QUERY_DATA_" + queryConverter.getNamedQuery());

        // 设置返回类型
        if (queryParam.getSelectFields() == null) {
            sqlQuery.addEntity(queryConverter.getAsTable(), getDomainClass());
        } else if (queryParam.isSelectFieldsUseDomainType()) {
            sqlQuery.setResultTransformer(Transformers.aliasToBean(getDomainClass()));
        } else {
            // 设置参数类型
            if (queryParam.hasSelectFieldsClass()) {
                addScalar(queryParam, sqlQuery);
            }

            if (queryParam.getSelectFields().size() >= 2) {
                sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
        }

        List data = sqlQuery.list();
        return data;
    }

    private void addScalar(DBQueryParam queryParam, SQLQuery query) {
        Map<String, Class> selectFieldsClass = queryParam.getSelectFieldsClass();
        Collection<String> selectFields = queryParam.getSelectFields();
        for (String field : selectFields) {
            Class clazz = selectFieldsClass.get(field);
            // todo 以后优化！
            if (clazz == null) {
                try {
                    T object = getDomainClass().newInstance();
                    PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(object, field);
                    if (pd != null) {
                        clazz = pd.getPropertyType();
                    } else {
                        pd = PropertyUtils.getPropertyDescriptor(object, toFieldName(field));
                        if (pd != null) {
                            clazz = pd.getPropertyType();
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
            // --end--
            query.addScalar(field, toHibernateType(clazz));
        }
    }

    private void setQueryCache(org.hibernate.Query query, String cacheRegion) {
        query.setCacheable(true);
        query.setCacheRegion(cacheRegion);
        LOGGER.debug("set query cache region: {}", cacheRegion);
    }

    public <R> R uniqueByNamed(String namedQuery, Map<String, Object> queryParameters, Class<R> returnType) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.getNamedQuery(namedQuery);
        String querySql = query.getQueryString();
        querySql = FreeMarkerTools.parseText(querySql, queryParameters);

        SQLQuery sqlQuery = session.createSQLQuery(querySql);
        String[] namedParameters = sqlQuery.getNamedParameters();
        for (String name : namedParameters) {
            if (queryParameters.containsKey(name)) {
                Object param = queryParameters.get(name);
                if (param instanceof Collection) {
                    sqlQuery.setParameterList(name, (Collection) param);
                } else {
                    sqlQuery.setParameter(name, param);
                }
            }
        }
        setQueryCache(query, "UNIQUE_" + namedQuery);

        sqlQuery.addScalar("val", toHibernateType(returnType));
        R val = (R) sqlQuery.uniqueResult();
        return val;
    }

    private Type toHibernateType(Class returnType) {
        if (returnType != null) {
            if (returnType.equals(String.class)) {
                return StandardBasicTypes.STRING;
            } else if (returnType.equals(Long.class)) {
                return StandardBasicTypes.LONG;
            } else if (returnType.equals(Integer.class)) {
                return StandardBasicTypes.INTEGER;
            } else if (returnType.equals(Short.class)) {
                return StandardBasicTypes.SHORT;
            } else if (returnType.equals(Byte.class)) {
                return StandardBasicTypes.BYTE;
            } else if (returnType.equals(java.sql.Timestamp.class)) {
                return StandardBasicTypes.TIMESTAMP;
            } else if (returnType.equals(java.sql.Date.class)) {
                return StandardBasicTypes.DATE;
            } else if (returnType.isAssignableFrom(java.util.Date.class)) {
                return StandardBasicTypes.DATE;
            }
        }
        return StandardBasicTypes.STRING;
    }

    public static String toFieldName(String columnName) {
        StringTokenizer tokenizer = new StringTokenizer(columnName, "_");
        StringBuilder builder = new StringBuilder();
        builder.append(tokenizer.nextToken());
        while (tokenizer.hasMoreTokens()) {
            String text = StringUtils.uncapitalize(tokenizer.nextToken());
            builder.append(text);
        }
        return builder.toString();
    }
}
