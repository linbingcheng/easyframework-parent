package top.bingchenglin.easyframework.easyframwork.commons.dao.db.query;

import top.bingchenglin.easyframework.easyframwork.commons.dao.param.DBQueryParam;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBQueryConverter {
    private static final String STATEMENT_AS_TABLE = "this_";
    private static final String STATEMENT_AS_COUNT = "cnt_";
    private static final String STATEMENT_VAR_PARAM = "p_";

    private static final String TEMPLATE_STATEMENT_COUNT = "SELECT COUNT(*) FROM %s %s";
    private static final String TEMPLATE_STATEMENT_SELECT_FROM = "FROM %s %s";
    private static final String TEMPLATE_STATEMENT_SELECT_FIELDS = "SELECT NEW %s(%s) ";

    private static final String TEMPLATE_SQL_STATEMENT_COUNT = "SELECT COUNT(*) " + STATEMENT_AS_COUNT + " FROM (%s) " + STATEMENT_AS_TABLE;
    private static final String TEMPLATE_SQL_STATEMENT_SELECT = "SELECT %s FROM (%s) " + STATEMENT_AS_TABLE;

    private static final Pattern REGEXP_TYPE_FIELD = Pattern.compile("_([a-z]+)_(\\w+)");

    private static final Map<String, String> CONDITION_FIELD_MAP = new HashMap<String, String>() {{
        put("n", "(%s.%s is null)");
        put("nn", "(%s.%s is not null)");
    }};

    private static final Map<String, String> CONDITION_FIELD_PH_MAP = new HashMap<String, String>() {{
        put("lt", "(%s.%s < :%s)");
        put("lte", "(%s.%s <= :%s)");
        put("eq", "(%s.%s = :%s)");
        put("gte", "(%s.%s >= :%s)");
        put("gt", "(%s.%s > :%s)");
        put("neq", "(%s.%s <> :%s)");
        put("in", "(%s.%s in(:%s))");
        put("nin", "(%s.%s not in(:%s))");

        put("k", "(%s.%s like :%s)");//%value%
        put("nk", "(%s.%s not like :%s)");//%value%
        put("ki", "(lower(%s.%s) like lower(:%s))");//%value%
        put("nki", "(lower(%s.%s) not like lower(:%s))");//%value%
        put("sw", "(%s.%s like :%s)");//value%
        put("ew", "(%s.%s like :%s)");//%value
        put("eqi", "(lower(%s.%s) = lower(:%s))");
        put("neqi", "(lower(%s.%s) <> lower(:%s))");
    }};

    private static final Set<String> CONDITION_VALUE_PER_SET = new HashSet<String>() {{
        add("k");
        add("nk");
        add("ki");
        add("nki");
    }};

    private StringBuilder whereStatement;
    private Map<String, Object> parameters;
    private String asTable;
    private Class domainClass;
    private Object domainBean;
    private String namedQuery;

    public DBQueryConverter(Class domainClass) {
        this.whereStatement = new StringBuilder();
        this.parameters = new HashMap<String, Object>();
        this.asTable = STATEMENT_AS_TABLE;
        this.domainClass = domainClass;
        try {
            this.domainBean = domainClass.newInstance();
        } catch (Exception e) {
            // ignore
        }
    }

    public void perform(DBQueryParam queryParam) {
        // 读取查询条件
        Map<String, Object> queryConditions = getQueryConditions(queryParam);
        // 查询条件排序
        Collection<String> sortConditions = getSortConditions(queryConditions, queryParam);

        // 构造查询条件
        for (String cond : sortConditions) {
            Matcher typeFieldMatcher = REGEXP_TYPE_FIELD.matcher(cond);
            if (!typeFieldMatcher.find()) {
                continue;
            }
            String type = typeFieldMatcher.group(1);
            String field = typeFieldMatcher.group(2);

            Object value = queryConditions.get(cond);
            if (value != null && value.getClass().isArray()) {
                value = Arrays.asList((Object[]) value);
            }
            // 值类型转换
            if (value instanceof String) {
                value = convertType(field, value);
            } else if (value instanceof Collection) {
                value = convertCollection(field, (Collection) value);
            }

            // 增加查询条件
            appendCondition(type, field, value);
        }
    }

    public void setNamedParameters(DBQueryParam queryParam, String[] namedParameters, String namedQuery) {
        Map<String, Object> queryConditions = queryParam.getQueryConditions();
        for (String name : namedParameters) {
            if (queryConditions.containsKey(name)) {
                Object value = queryConditions.get(name);
                if (value != null && value.getClass().isArray()) {
                    value = Arrays.asList((Object[]) value);
                }
                parameters.put(name, value);
            }
        }
        this.namedQuery = namedQuery;
    }

    public String getCountStatement() {
        StringBuilder countBuilder = new StringBuilder();
        countBuilder.append(String.format(TEMPLATE_STATEMENT_COUNT, getDomainName(), asTable));
        if (whereStatement.length() >= 1) {
            countBuilder.append(" WHERE ").append(whereStatement.toString());
        }
        return countBuilder.toString();
    }

    public String getSQLCountStatement(String tableSql) {
        StringBuilder countBuilder = new StringBuilder();
        countBuilder.append(String.format(TEMPLATE_SQL_STATEMENT_COUNT, tableSql));
        if (whereStatement.length() >= 1) {
            countBuilder.append(" WHERE ").append(whereStatement.toString());
        }
        return countBuilder.toString();
    }

    public String getDataStatement(DBQueryParam queryParam) {
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append(getSelectFields(queryParam));
        selectBuilder.append(String.format(TEMPLATE_STATEMENT_SELECT_FROM, getDomainName(), asTable));
        if (whereStatement.length() >= 1) {
            selectBuilder.append(" WHERE ").append(whereStatement.toString());
        }
        selectBuilder.append(getSelectOrderBy(queryParam));
        return selectBuilder.toString();
    }

    public String getSQLDataStatement(DBQueryParam queryParam, String tableSql) {
        String selectFields = getSelectFields2(queryParam);
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append(String.format(TEMPLATE_SQL_STATEMENT_SELECT, selectFields, tableSql));
        if (whereStatement.length() >= 1) {
            selectBuilder.append(" WHERE ").append(whereStatement.toString());
        }
        selectBuilder.append(getSelectOrderBy(queryParam));
        return selectBuilder.toString();
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getAsTable() {
        return STATEMENT_AS_TABLE;
    }

    public String getAsCount() {
        return STATEMENT_AS_COUNT;
    }

    public String getNamedQuery() {
        return namedQuery;
    }

    public String toString() {
        return getDomainName();
    }


    private Object convertCollection(String field, Collection value) {
        PropertyDescriptor pd = null;
        try {
            pd = PropertyUtils.getPropertyDescriptor(domainBean, field);
        } catch (Exception e) {
            // ignore
        }
        if (pd != null && !String.class.equals(pd.getPropertyType())) {
            List newValue = new ArrayList();
            for (Object item : value) {
                newValue.add(ConvertUtils.convert(item, pd.getPropertyType()));
            }
            value = newValue;
        }
        return value;
    }

    /**
     * 领域对象字段值类型转换
     *
     * @param field
     * @param value
     * @return
     */
    private Object convertType(String field, Object value) {
//        try {
//            PropertyDescriptor[] props = java.beans.Introspector.getBeanInfo(domainClass).getPropertyDescriptors();
//        } catch (IntrospectionException e) {
//            e.printStackTrace();
//        }
        PropertyDescriptor pd = null;
        try {
            pd = PropertyUtils.getPropertyDescriptor(domainBean, field);
        } catch (Exception e) {
            // ignore
        }
        if (pd != null && !String.class.equals(pd.getPropertyType())) {
            value = ConvertUtils.convert(value, pd.getPropertyType());
        }
        return value;
    }

    /**
     * 增加查询条件
     *
     * @param type
     * @param field
     * @param value
     */
    private void appendCondition(String type, String field, Object value) {
        if (whereStatement.length() >= 1) {
            whereStatement.append(" and ");
        }
        if (CONDITION_FIELD_MAP.containsKey(type)) {
            whereStatement.append(String.format(CONDITION_FIELD_MAP.get(type), asTable, field));
        } else if (CONDITION_FIELD_PH_MAP.containsKey(type)) {
            String ph = STATEMENT_VAR_PARAM + parameters.size();
            whereStatement.append(String.format(CONDITION_FIELD_PH_MAP.get(type), asTable, field, ph));
            if (CONDITION_VALUE_PER_SET.contains(type)) {
                value = "%" + value + "%";
            } else if ("sw".equals(type)) {
                value = value + "%";
            } else if ("ew".equals(type)) {
                value = "%" + value;
            }
            parameters.put(ph, value);
        } else {
            throw new RuntimeException("参数类型错误！");
        }
    }

    /**
     * 构造排序后的查询条件
     *
     * @param queryConditions
     * @return
     */
    private Collection<String> getSortConditions(Map<String, Object> queryConditions, DBQueryParam queryParam) {
        Collection<String> conditions = new ArrayList<String>(queryConditions.size());
        Collection<String> sortConditions = queryParam.getSortConditions();
        if (sortConditions != null) {
            for (String cond : sortConditions) {
                if (queryConditions.containsKey(cond)) {
                    conditions.add(cond);
                }
            }
        }
        for (String cond : queryConditions.keySet()) {
            if (!conditions.contains(cond)) {
                conditions.add(cond);
            }
        }
        return conditions;
    }

    /**
     * 构造查询条件
     *
     * @return
     */
    private Map<String, Object> getQueryConditions(DBQueryParam queryParam) {
        Map<String, Object> queryConditions = new LinkedHashMap<String, Object>();
        try {
            Map<String, Object> description = PropertyUtils.describe(queryParam);
            for (Map.Entry<String, Object> entry : description.entrySet()) {
                String condition = entry.getKey();
                Object value = entry.getValue();
                if (condition.startsWith("_") && value != null) {
                    queryConditions.put(condition, value);
                }
            }
        } catch (Exception e) {
            // ignore
        }
        if (queryParam.hasQueryConditions()) {
            queryConditions.putAll(queryParam.getQueryConditions());
        }
        return queryConditions;
    }

    private String getSelectFields2(DBQueryParam queryParam) {
        if (queryParam.getSelectFields() != null) {
            StringBuilder fieldsBuilder = new StringBuilder();
            Collection<String> selectFields = queryParam.getSelectFields();
            for (String field : selectFields) {
                fieldsBuilder.append(",").append(asTable).append(".").append(field);
            }
            fieldsBuilder.deleteCharAt(0);
            return fieldsBuilder.toString();
        }
        return "*";
    }

    /**
     * 构造查询语句字段部分
     *
     * @return
     */
    private String getSelectFields(DBQueryParam queryParam) {
        if (queryParam.getSelectFields() != null) {
            StringBuilder fieldsBuilder = new StringBuilder();
            Collection<String> selectFields = queryParam.getSelectFields();
            for (String field : selectFields) {
                fieldsBuilder.append(",").append(asTable).append(".").append(field);
            }
            fieldsBuilder.deleteCharAt(0);
            return String.format(TEMPLATE_STATEMENT_SELECT_FIELDS, getDomainName(), fieldsBuilder.toString());
        }
        return "";
    }

    /**
     * 构造查询语句排序部分
     *
     * @return
     */
    private String getSelectOrderBy(DBQueryParam queryParam) {
        String orderBy = queryParam.getSort();
        String orderDesc = queryParam.getOrder();
        if (StringUtils.isNotBlank(orderBy)) {
            StringBuilder orderByBuilder = new StringBuilder();
            StringTokenizer orderByTokenizer = new StringTokenizer(orderBy, ",");
            StringTokenizer orderDescTokenizer = new StringTokenizer(orderDesc != null ? orderDesc : "", ",");
            while (orderByTokenizer.hasMoreTokens()) {
                String field = orderByTokenizer.nextToken().trim();
                String desc = null;
                if (orderDescTokenizer.hasMoreTokens()) {
                    desc = orderDescTokenizer.nextToken().trim();
                }
                orderByBuilder.append(",").append(asTable).append(".").append(field);
                if ("1".equals(desc) || "desc".equalsIgnoreCase(desc)) {
                    orderByBuilder.append(" DESC");
                }
            }
            orderByBuilder.replace(0, 1, " ORDER BY ");
            return orderByBuilder.toString();
        }
        return "";
    }

    /**
     * 获取持久化对象名称
     *
     * @return
     */
    private String getDomainName() {
        return domainClass.getName();
    }
}
