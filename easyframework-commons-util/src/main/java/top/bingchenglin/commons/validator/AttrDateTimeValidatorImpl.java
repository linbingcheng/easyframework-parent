package top.bingchenglin.commons.validator;

import org.apache.commons.lang3.StringUtils;
import top.bingchenglin.commons.annotation.AttrDateTime;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AttrDateTimeValidatorImpl implements AttrValidator {

    @Override
    public void doProcess(Annotation annotation, String fieldName, Object fieldValue) throws AttrValidationException {

        AttrDateTime anno = (AttrDateTime) annotation;
        if (fieldValue != null && fieldValue instanceof String && StringUtils.isNotBlank(anno.format())) {

            try {
                SimpleDateFormat format = new SimpleDateFormat(anno.format(), Locale.US);
                format.setLenient(false);
                format.parse(fieldValue.toString());
            } catch (Exception ex) {
                throw new AttrValidationException(fieldName, anno.retcode(), String.format(DATETIME_PARAM_ILLEGAL, fieldName, anno.format()), ex);
            }
        }

    }
}
