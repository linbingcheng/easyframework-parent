package top.bingchenglin.commons.dao.db;


import top.bingchenglin.commons.dao.param.DBQueryParam;
import top.bingchenglin.commons.dao.param.DataPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    /**
     * <p>
     * 自定义查询条件，属性命名规则：_{代号}_{字段名}，例如：private String _eq_name;.
     * </p>
     * <table style="border:1px solid gray;">
     * <tr>
     * <td style="border:1px solid gray;">基础类型</td>
     * <td style="border:1px solid gray;">null</td>
     * <td style="border:1px solid gray;">not null</td>
     * <td style="border:1px solid gray;">&lt;</td>
     * <td style="border:1px solid gray;">&lt;=</td>
     * <td style="border:1px solid gray;">=</td>
     * <td style="border:1px solid gray;">&gt;=</td>
     * <td style="border:1px solid gray;">&gt;</td>
     * <td style="border:1px solid gray;">&lt;&gt;</td>
     * <td style="border:1px solid gray;">in</td>
     * <td style="border:1px solid gray;">not in</td>
     * </tr>
     * <tr>
     * <td style="border:1px solid gray;">代号</td>
     * <td style="border:1px solid gray;">n</td>
     * <td style="border:1px solid gray;">nn</td>
     * <td style="border:1px solid gray;">lt</td>
     * <td style="border:1px solid gray;">lte</td>
     * <td style="border:1px solid gray;">eq</td>
     * <td style="border:1px solid gray;">gte</td>
     * <td style="border:1px solid gray;">gt</td>
     * <td style="border:1px solid gray;">neq</td>
     * <td style="border:1px solid gray;">in</td>
     * <td style="border:1px solid gray;">nin</td>
     * </tr>
     * </table>
     * <br/>
     * <table style="border:1px solid gray;">
     * <tr>
     * <td style="border:1px solid gray;">高级类型</td>
     * <td style="border:1px solid gray;">like</td>
     * <td style="border:1px solid gray;">not like</td>
     * <td style="border:1px solid gray;">like ignore</td>
     * <td style="border:1px solid gray;">not like ignore</td>
     * <td style="border:1px solid gray;">starts with</td>
     * <td style="border:1px solid gray;">ends with</td>
     * <td style="border:1px solid gray;">equals ignore</td>
     * <td style="border:1px solid gray;">not equals ignore</td>
     * </tr>
     * <tr>
     * <td style="border:1px solid gray;">代号</td>
     * <td style="border:1px solid gray;">k</td>
     * <td style="border:1px solid gray;">nk</td>
     * <td style="border:1px solid gray;">ki</td>
     * <td style="border:1px solid gray;">nki</td>
     * <td style="border:1px solid gray;">sw</td>
     * <td style="border:1px solid gray;">ew</td>
     * <td style="border:1px solid gray;">eqi</td>
     * <td style="border:1px solid gray;">neqi</td>
     * </tr>
     * </table>
     *
     * @param queryParam
     * @return
     */
    DataPackage query(DBQueryParam queryParam);

    long queryCount(DBQueryParam queryParam);

    List queryData(DBQueryParam queryParam);

    DataPackage queryByNamed(DBQueryParam queryParam, String namedQuery);

    long queryCountByNamed(DBQueryParam queryParam, String namedQuery);

    List queryDataByNamed(DBQueryParam queryParam, String namedQuery);

    <R> R uniqueByNamed(String namedQuery, Map<String, Object> queryParameters, Class<R> returnType);
}
