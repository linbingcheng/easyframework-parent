package top.bingchenglin.easyframework.commons.validator;

import java.lang.annotation.Annotation;

public interface AttrValidator {

	String ENUM_PARAM_ILLEGAL = "入参:[%s] 应从下列枚举取值:[%s]";

	String DATETIME_PARAM_ILLEGAL = "入参:[%s] 时间格式不对，格式应为:[%s]";

	String DEFINITION_PARAM_ILLEGAL_BLANK = "入参:[%s] 不能为空";

	String DEFINITION_PARAM_ILLEGAL_LENGTH = "入参:[%s] 长度必须为:[%s]位，格式为:[%s]";

	String DEFINITION_PARAM_ILLEGAL_LENGTH2 = "入参:[%s] 长度必须为:[%s]位 或者为:[%s]位，格式为:[%s]";

	String DEFINITION_PARAM_ILLEGAL_MAXLENGTH = "入参:[%s] 长度不能超过最大长度:[%s]位，格式为:[%s]";

	String DEFINITION_PARAM_ILLEGAL_MINLENGTH = "入参:[%s] 长度不能少于最少长度:[%s]位，格式为:[%s]";

	String DEFINITION_PARAM_ILLEGAL_DECIMALLENGTH = "入参:[%s] 小数位超过规定的精度数:[%s]位，格式为:[%s]";

	void doProcess(Annotation annotation, String fieldName, Object fieldValue) throws AttrValidationException;
}
