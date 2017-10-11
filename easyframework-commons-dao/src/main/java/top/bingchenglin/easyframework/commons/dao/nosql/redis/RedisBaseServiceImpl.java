package top.bingchenglin.easyframework.easyframwork.commons.dao.nosql.redis;


import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.ClassUtils;
import top.bingchenglin.easyframework.easyframwork.commons.dao.param.DBQueryParam;
import top.bingchenglin.easyframework.commons.util.DateUtil;

import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class RedisBaseServiceImpl<T, ID extends Serializable> implements RedisBaseService<T, ID>{

	
	@Autowired
	public RedisTemplate<String, ?> redisTemplate;
	
	@Autowired
	public ValueOperations<String, T> valueOperations;
	
	@Autowired
	RedisCacheManager cacheManager;
	
	private  Class<?> domainType;
	
	private  Class<? extends Serializable> idType;
	
	private String tableName;
	
	private String tableFieldSeparator = ":";
	
	public RedisBaseServiceImpl() {
		super();
		this.domainType = resolveDomainType(getClass());
		this.idType = resolveIdType(getClass());
		Table annotation = (Table)this.domainType.getAnnotation(Table.class);
		this.tableName = annotation!=null?annotation.name():"";
	}

	public void  save(ID id,T entity) {
		String key = getKey(id);
		valueOperations.set(key, entity);
	}
	
	public void save(Map<ID, T> entitys) {
		Map<String, T> map = new HashMap<String, T>();
		if(entitys!=null && !entitys.isEmpty()){
			for(Entry<ID, T> entry:entitys.entrySet()){
				ID id = entry.getKey();
				String key = getKey(id);
				map.put(key, entry.getValue());
			}
		}
		if(!map.isEmpty()){
			valueOperations.multiSet(map);
		}
	}
	
	public void delete(ID id) {
		String key = getKey(id);
		redisTemplate.delete(key);
	}
	
	public void delete(Iterable<ID> ids) {
		List<String> keys = getKeys(ids);
		if(!keys.isEmpty()){
			redisTemplate.delete(keys);
		}
	}

	public T findOne(ID id) {
		String key = getKey(id);
		T value = (T) valueOperations.get(key);
		return value;
	}
	
	public T findOne(ID id, DBQueryParam filterQueryParam) throws Exception{
		T value = findOne(id);
		if(value!=null){
			value = filterByParam(value,filterQueryParam);
		}
		return value;
	}
	
	public List<T> findAll(Iterable<ID> ids) {
		List<T> list = new ArrayList<T>();
		List<String> keys = getKeys(ids);
		if(!keys.isEmpty()){
			list = valueOperations.multiGet(keys);	
		}
		return list;
	}
	
	public List<T> findAll(Iterable<ID> ids, DBQueryParam filterQueryParam) throws Exception {
		List<T> list = findAll(ids);
		List<T> retList = new ArrayList<T>();
		if(list!=null){
			for(T t:list){
				T value = filterByParam(t,filterQueryParam);
				if(value!=null){
					retList.add(value);
				}
			}
		}
		return retList;
	}
	
	public List<T> findAll(String idPattern) {
		List<T> list = new ArrayList<T>();
		if(StringUtils.isNotBlank(idPattern)){
			String keyPattern =  StringUtils.isNotBlank(getTableName()) ? getTableName() + tableFieldSeparator + idPattern : idPattern;
			Set<String> keys = redisTemplate.keys(keyPattern);
			if(keys!=null && !keys.isEmpty()){
				list = valueOperations.multiGet(keys);	
			}
		}
		return list;
	}
	
	public List<T> findAll(String idPattern, DBQueryParam filterQueryParam) throws Exception {
		List<T> list = findAll(idPattern);
		List<T> retList = new ArrayList<T>();
		if(list!=null){
			for(T t:list){
				T value = filterByParam(t,filterQueryParam);
				if(value!=null){
					retList.add(value);
				}
			}
		}
		return retList;
	}
	
	@SuppressWarnings("unchecked")
	private T filterByParam(T t,DBQueryParam dbQueryParam) throws Exception{
		T result = null;
		boolean conditionFlag = true;
		if(t!=null && dbQueryParam!=null){
			Map<?, ?> props = getConditions(dbQueryParam);
			if(props!=null){
				String key, field;
				Object pvalue;
				SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter();
				simpleTypeConverter.useConfigValueEditors();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DateFormatType.DATE_FORMAT_STR.getValue());
				CustomDateEditor customDateEditor = new CustomDateEditor(simpleDateFormat,false);
				 simpleTypeConverter.registerCustomEditor(Date.class,customDateEditor);
				for (Iterator<?> keys = props.keySet().iterator(); keys.hasNext();) {
					 key = (String) keys.next();
					 pvalue = props.get(key);
					// 忽略无值的参数
					if (pvalue == null) {
						continue;
					}
					
					if(!conditionFlag){
						break;
					}
					
					// 对于余下的检查是否符合规则
					//忽略不是VO的字段,不用考虑复合主键，因为查询时就是根据主皱起查出来的。
						field = key.substring(key.indexOf('_', 1) + 1);
						
						if(StringUtils.isEmpty(field)){
							continue;
						}
						
						Class<?> tisValueType = PropertyUtils.getPropertyType(t, field);
						if(tisValueType!=null){
							Object tisValue = PropertyUtils.getProperty(t, field);
								
								if(pvalue instanceof Collection){
									Collection<?> pvalueCollection = (Collection<?>) pvalue;
									if (key.startsWith("_sin_") || key.startsWith("_din_") || key.startsWith("_nin_")){
										for(Object pv:pvalueCollection){
											Object pValueConverted = simpleTypeConverter.convertIfNecessary(pv, tisValueType);
											if(tisValue!=null && tisValue.equals(pValueConverted)){
												conditionFlag = true;
												break;
											}else{
												conditionFlag = false;
											}
										}
									}else if (key.startsWith("_snin_") || key.startsWith("_dnin_") || key.startsWith("_nnin_")){
										for(Object pv:pvalueCollection){
											Object pValueConverted = simpleTypeConverter.convertIfNecessary(pv, tisValueType);
											if(tisValue!=null && tisValue.equals(pValueConverted)){
												conditionFlag = false;
												break;
											}else{
												conditionFlag = true;
											}
										}
									}
								}else{
									Object pValueConverted = simpleTypeConverter.convertIfNecessary(pvalue, tisValueType);
									if (key.startsWith("_se_") || key.startsWith("_de_") || key.startsWith("_ne_")){
										if(tisValue!=null && tisValue.equals(pValueConverted)){
											conditionFlag = true;
										}else{
											conditionFlag = false;
										}
									} else if (key.startsWith("_sne_") || key.startsWith("_dne_") || key.startsWith("_nne_")){
										if(tisValue!=null && tisValue.equals(pValueConverted)){
											conditionFlag = false;
										}else{
											conditionFlag = true;
										}
									}else if (key.startsWith("_sei_")){
										if(tisValue!=null && tisValue.toString().toLowerCase().startsWith(pValueConverted.toString().toLowerCase())){
											conditionFlag = true;
										}else{
											conditionFlag = false;
										}
									}else if (key.startsWith("_snei_")) {
										if(tisValue!=null && tisValue.toString().toLowerCase().startsWith(pValueConverted.toString().toLowerCase())){
											conditionFlag = false;
										}else{
											conditionFlag = true;
										}
									}else {
										if(tisValue==null || tisValue instanceof Comparable){
											Comparable<Comparable<?>>   tisValueCp = null;
											if(tisValue!=null){
												tisValueCp = (Comparable<Comparable<?>>) tisValue;
											}
											Comparable<?>   pValueConvertedCp = (Comparable<?>) pValueConverted;
											
											 if (key.startsWith("_snl_") || key.startsWith("_dnl_") || key.startsWith("_nnl_")){
												 if(tisValueCp!=null && tisValueCp.compareTo(pValueConvertedCp)>=0){
														conditionFlag = true;
													}else{
														conditionFlag = false;
													}
											 }else if (key.startsWith("_sm_") || key.startsWith("_dm_") || key.startsWith("_nm_")){
												 if(tisValueCp!=null && tisValueCp.compareTo(pValueConvertedCp)>0){
														conditionFlag = true;
													}else{
														conditionFlag = false;
													}
											 }else if (key.startsWith("_snm_") || key.startsWith("_dnm_") || key.startsWith("_nnm_")){
												 if(tisValueCp!=null && tisValueCp.compareTo(pValueConvertedCp)<=0){
														conditionFlag = true;
													}else{
														conditionFlag = false;
													}
											 }else if (key.startsWith("_sl_") || key.startsWith("_dl_") || key.startsWith("_nl_")){
												 if(tisValueCp!=null && tisValueCp.compareTo(pValueConvertedCp)<0){
														conditionFlag = true;
													}else{
														conditionFlag = false;
													}
											 }
										}
									}
								}
							
						}
						
				}
			}
		}
		
		if(conditionFlag){
			result = t;
		}
		
		return result;
	}
	
	private Map<String, Object> getConditions(Object param) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		// 静态条件部分
		Map<String, Object> props = PropertyUtils.describe(param);

		// new 动态条件部分 add by hekun
		if (param instanceof DBQueryParam) {
			DBQueryParam listVO = (DBQueryParam) param;
			Map<String, Object> queryConditions = (Map<String, Object>) listVO.getQueryConditions();

			if (queryConditions != null && queryConditions.size() > 0) {
				// 将静态条件加入动态条件中，重复的动态条件及其值将被覆盖。
				for (Iterator<String> keys = props.keySet().iterator(); keys.hasNext();) {
					String key = (String) keys.next();
					Object value = props.get(key);
					if (key.startsWith("_") && value != null)
						queryConditions.put(key, value);
				}
				props = queryConditions;
			}
		}
		return props;
	}
	
	private List<String> getKeys(Iterable<ID> ids){
		List<String> keys = new ArrayList<String>();
		if(ids!=null){
			for(ID id:ids){
				String key = getKey(id);
				keys.add(key);
			}
		}
		return keys;
	}
	
	private String getKey(ID id){
		String idStr = (id.getClass().equals(String.class) || ClassUtils.isPrimitiveOrWrapper(id.getClass())) ? id.toString() : ReflectionToStringBuilder.toString(id, ToStringStyle.SIMPLE_STYLE);
		String key = StringUtils.isNotBlank(getTableName()) ? getTableName() + tableFieldSeparator + idStr:idStr;
		return key;
	}
	
	private Class<?> resolveDomainType(Class<?> repositoryInterface) {

		TypeInformation<?> information = ClassTypeInformation.from(repositoryInterface);
		List<TypeInformation<?>> arguments = information.getSuperTypeInformation(RedisBaseService.class).getTypeArguments();

		if (arguments.isEmpty() || arguments.get(0) == null) {
			throw new IllegalArgumentException(String.format("Could not resolve domain type of %s!", repositoryInterface));
		}

		return arguments.get(0).getType();
	}

	@SuppressWarnings("unchecked")
	private Class<? extends Serializable> resolveIdType(Class<?> repositoryInterface) {

		TypeInformation<?> information = ClassTypeInformation.from(repositoryInterface);
		List<TypeInformation<?>> arguments = information.getSuperTypeInformation(RedisBaseService.class).getTypeArguments();

		if (arguments.size() < 2 || arguments.get(1) == null) {
			throw new IllegalArgumentException(String.format("Could not resolve id type of %s!", repositoryInterface));
		}

		return (Class<? extends Serializable>) arguments.get(1).getType();
	}

	public Class<?> getDomainType() {
		return domainType;
	}

	public Class<? extends Serializable> getIdType() {
		return idType;
	}
	
	

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}



	/**
	 * 
	 * @职责 添加缓存，可配置超时失效,重载方法：public void save(ID id, T entity);
	 *
	 * @anthor liangzf3<liangzf3@asiainfo.com>
	 * @since 2016年4月22日 下午15:50:06
	 * @param ID 主键，键值  泛型
	 * @param entity 实体，值  泛型
	 * @param timeout 超时数 长整型
	 * @param unit 超时单位
	 */
	public void save(ID id, T entity, long timeout, TimeUnit unit) {
		String key = getKey(id);
		valueOperations.set(key, entity, timeout, unit);
	}

	/**
	 * 
	 * @职责 添加缓存，可配置超时失效,重载方法：public void save(ID id, T entity);
	 *
	 * @anthor liangzf3<liangzf3@asiainfo.com>
	 * @since 2016年4月22日 下午15:50:06
	 * @param ID 主键，键值  泛型
	 * @param entity 实体，值  泛型
	 * @param timeout 超时数 长整型
	 * @param unit 超时单位
	 */
	public void save(Map<ID, T> entitys, long timeout, TimeUnit unit) {
//		Map<String, T> map = new HashMap<String, T>();
		if(entitys!=null && !entitys.isEmpty()){
			for(Entry<ID, T> entry:entitys.entrySet()){
				ID id = entry.getKey();
				String key = getKey(id);
				valueOperations.set(key, entry.getValue(), timeout, unit);
			}
		}
	}
}
