package top.bingchenglin.easyframework.commons.validator;

import org.apache.commons.lang3.StringUtils;
import top.bingchenglin.easyframework.commons.annotation.AttrEnum;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class AttrEnumValidatorImpl implements AttrValidator {

    @Override
    public void doProcess(Annotation annotation, String fieldName, Object fieldValue) throws AttrValidationException {

        AttrEnum anno = (AttrEnum) annotation;

        if (fieldValue != null && StringUtils.isNotBlank(anno.enumValue())) {
            String[] enumValues = anno.enumValue().split(",");
            if (enumValues != null && enumValues.length > 0) {
                List<String> enumList = Arrays.asList(enumValues);
                if (!enumList.contains(fieldValue.toString())) {
                    throw new AttrValidationException(fieldName, anno.retcode(), String.format(ENUM_PARAM_ILLEGAL, fieldName, anno.enumValue()));
                }
            }
        }
    }
}
