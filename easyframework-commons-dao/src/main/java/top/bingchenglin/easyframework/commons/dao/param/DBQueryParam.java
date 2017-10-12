package top.bingchenglin.easyframework.commons.dao.param;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.*;

public class DBQueryParam implements Serializable {
    private static final int FIRST_PAGE = 1;

    private static final int MINIMUM_LIMIT = 0;
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAXIMUM_LIMIT = 10000;

    // 页码
    private int page = FIRST_PAGE;

    // 每页数量
    private int limit = DEFAULT_LIMIT;
    private int memo_limit;

    // 排序字段
    private String sort;

    // asc or desc
    private String order;

    // 设置动态查询条件
    @JsonIgnore
    private Map<String, Object> queryConditions;

    // 查询条件优先级排序
    @JsonIgnore
    private Collection<String> sortConditions;

    // 列举查询字段
    @JsonIgnore
    private Collection<String> selectFields;

    // 设置字段类型
    @JsonIgnore
    private Map<String, Class> selectFieldsClass;

    // 使用持久化对象返回
    @JsonIgnore
    private boolean selectFieldsUseDomainType;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        if (page < FIRST_PAGE) {
            page = FIRST_PAGE;
        }
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit < MINIMUM_LIMIT) {
            limit = MINIMUM_LIMIT;
        } else if (limit > MAXIMUM_LIMIT) {
            limit = MAXIMUM_LIMIT;
        }
        this.limit = limit;
    }

    public int getStart() {
        return limit * (page - 1);
    }

    public void setStart(int start) {}

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Map<String, Object> getQueryConditions() {
        if (queryConditions == null) {
            queryConditions = new LinkedHashMap<String, Object>();
        }
        return queryConditions;
    }

    public void setQueryConditions(Map<String, Object> queryConditions) {
        this.queryConditions = queryConditions;
    }

    public boolean hasQueryConditions() {
        return queryConditions != null;
    }

    public Collection<String> getSortConditions() {
        return sortConditions;
    }

    public void setSortConditions(String... conditions) {
        sortConditions = new LinkedHashSet<String>();
        for (String cond : conditions) {
            sortConditions.add(cond);
        }
    }

    public void setSortConditions(String conditions) {
        if(StringUtils.isNoneBlank(conditions)) {
            sortConditions = new LinkedHashSet<String>();
            StringTokenizer tokenizer = new StringTokenizer(conditions, ",");
            while (tokenizer.hasMoreTokens()) {
                sortConditions.add(tokenizer.nextToken().trim());
            }
        }
    }

    public Collection<String> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(String... fields) {
        selectFields = new LinkedHashSet<String>();
        for (String field : fields) {
            selectFields.add(field);
        }
    }

    public void setSelectFields(String fields) {
        if(StringUtils.isNotBlank(fields)) {
            selectFields = new LinkedHashSet<String>();
            StringTokenizer tokenizer = new StringTokenizer(fields, ",");
            while (tokenizer.hasMoreTokens()) {
                selectFields.add(tokenizer.nextToken().trim());
            }
        }
    }

    public Map<String, Class> getSelectFieldsClass() {
        if (selectFieldsClass == null) {
            selectFieldsClass = new LinkedHashMap<String, Class>();
        }
        return selectFieldsClass;
    }

    public void setSelectFieldsClass(Map<String, Class> selectFieldsClass) {
        this.selectFieldsClass = selectFieldsClass;
    }

    public boolean hasSelectFieldsClass() {
        return selectFieldsClass != null;
    }

    public boolean isSelectFieldsUseDomainType() {
        return selectFieldsUseDomainType;
    }

    public void setSelectFieldsUseDomainType(boolean selectFieldsUseDomainType) {
        this.selectFieldsUseDomainType = selectFieldsUseDomainType;
    }

    public void setQueryAll(boolean queryAll) {
        if (queryAll) {
            if (limit > 0) {
                memo_limit = limit;
            }
            limit = 0;
        } else {
            limit = memo_limit;
            memo_limit = 0;
        }
    }

}

