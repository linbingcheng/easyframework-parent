package top.bingchenglin.commons.validator;

import org.apache.commons.lang3.StringUtils;
import top.bingchenglin.commons.annotation.AttrDefinition;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttrDefinitionValidatorImpl implements AttrValidator {

    @Override
    public void doProcess(Annotation annotation, String fieldName, Object obj) throws AttrValidationException {

        AttrDefinition anno = (AttrDefinition) annotation;
        String occurrences = anno.occurrences();
        String length = anno.length();

        boolean needNodeExist = isNeedNodeExist(occurrences);
        if (obj == null) {
            if (needNodeExist) {
                throw new AttrValidationException(fieldName, anno.retcode(), String.format(DEFINITION_PARAM_ILLEGAL_BLANK, fieldName));
            }
        } else {
            if (obj instanceof String) {

                String fieldValue = String.valueOf(obj);
                if (needNodeExist && StringUtils.isBlank(fieldValue)) {
                    throw new AttrValidationException(fieldName, anno.retcode(), String.format(DEFINITION_PARAM_ILLEGAL_BLANK, fieldName));
                }

                if (StringUtils.isNotBlank(length)) {
                    AttrLengthVO lengthVO = null;
                    AttrLengthVO minLengthVO = null;
                    String[] lengthArray = length.split("\\,");

                    if (lengthArray.length == 1) {
                        lengthVO = getLengthVO(fieldValue, lengthArray[0]);
                    } else if (lengthArray.length == 2) {
                        minLengthVO = getLengthVO(fieldValue, lengthArray[0]);
                        lengthVO = getLengthVO(fieldValue, lengthArray[1]);
                    }

                    boolean isF = length.charAt(0) == 'F';

                    if (isF && StringUtils.length(fieldValue) != lengthVO.getTotalFormateLength() && minLengthVO == null) {
                        throw new AttrValidationException(fieldName, anno.retcode(), String.format(DEFINITION_PARAM_ILLEGAL_LENGTH, fieldName, lengthVO.getTotalFormateLength(), length));
                    }

                    if (lengthVO.getValueTotalLength() > lengthVO.getTotalFormateLength()) {
                        throw new AttrValidationException(fieldName, anno.retcode(), String.format(DEFINITION_PARAM_ILLEGAL_MAXLENGTH, fieldName, lengthVO.getTotalFormateLength(), length));
                    }

                    //--add by liuting 增加对F(N,M)的支持
                    boolean flag = minLengthVO != null && minLengthVO.getValueTotalLength() < minLengthVO.getTotalFormateLength();
                    if (isF && minLengthVO != null && lengthVO != null) {
                        if (!(minLengthVO.getValueTotalLength() == minLengthVO.getTotalFormateLength() || lengthVO.getValueTotalLength() == lengthVO.getTotalFormateLength())) {
                            throw new AttrValidationException(fieldName, anno.retcode(), String.format(DEFINITION_PARAM_ILLEGAL_LENGTH2, fieldName, minLengthVO.getTotalFormateLength(), lengthVO.getTotalFormateLength(), length));
                        }
                    } else if (flag) {
                        throw new AttrValidationException(fieldName, anno.retcode(), String.format(DEFINITION_PARAM_ILLEGAL_MINLENGTH, fieldName, minLengthVO.getTotalFormateLength(), length));
                    }

                    if (lengthVO.isFloatValue()) {
                        // 带小数位
                        if (lengthVO.getValueDecimalLength() > lengthVO.getFormateDecimalLength()) {
                            throw new AttrValidationException(fieldName, anno.retcode(), String.format(DEFINITION_PARAM_ILLEGAL_DECIMALLENGTH, fieldName, lengthVO.getFormateDecimalLength(), length));
                        }
                    }
                }


            } else if (obj instanceof Collection) {
                Collection collection = (Collection) obj;
                if (needNodeExist && collection.isEmpty()) {
                    throw new AttrValidationException(fieldName, anno.retcode(), String.format(DEFINITION_PARAM_ILLEGAL_BLANK, fieldName));
                }
            }
        }
    }

    private String getPatternValue(String patternString, String source) {
        String retStr = null;
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            retStr = matcher.group(0);
        }
        return retStr;
    }

    /**
     * <p>Description: 得到元数据最基本的长度信息<p>
     *
     * @param source
     * @param oneLength
     * @return
     */
    private AttrLengthVO getLengthVO(String source, String oneLength) {
        String vpatternString = "[0-9]+";
        String vdoublePatternString = "[0-9]+\\.[0-9]+";

        AttrLengthVO lengthVO = new AttrLengthVO();
        String retFormateLength = getPatternValue(vdoublePatternString, oneLength);

        if (StringUtils.isNotBlank(retFormateLength)) {
            // 有小数
            lengthVO.setFloatValue(true);

            String[] doubleFormateArray = retFormateLength.split("\\.");
            if (doubleFormateArray.length == 1) {
                lengthVO.setTotalFormateLength(Integer.parseInt(doubleFormateArray[0].trim()));
                lengthVO.setFormateDecimalLength(0);
            } else if (doubleFormateArray.length == 2) {
                lengthVO.setTotalFormateLength(Integer.parseInt(doubleFormateArray[0].trim()));
                lengthVO.setFormateDecimalLength(Integer.parseInt(doubleFormateArray[1].trim()));
            }
        } else {
            // 整数
            lengthVO.setFloatValue(false);
            String retIntLength = getPatternValue(vpatternString, oneLength);
            if (StringUtils.isNotBlank(retIntLength)) {
                lengthVO.setTotalFormateLength(Integer.parseInt(retIntLength));
                lengthVO.setFormateDecimalLength(0);
            }
        }

        if (lengthVO.isFloatValue()) {
            if (StringUtils.isNotBlank(source)) {
                String[] doubleValueArray = source.split("\\.");
                if (doubleValueArray.length == 1) {
                    lengthVO.setValueIntegerLength(StringUtils.length(doubleValueArray[0]));
                    lengthVO.setValueDecimalLength(lengthVO.getFormateDecimalLength());// 补取格式要求的小数位数做计算总长度(数据库里会默认补0）,小数位不算长度
                } else if (doubleValueArray.length == 2) {
                    lengthVO.setValueIntegerLength(StringUtils.length(doubleValueArray[0]));
                    lengthVO.setValueDecimalLength(StringUtils.length(doubleValueArray[1]));
                }
            }
        } else {
            lengthVO.setValueIntegerLength(source != null ? StringUtils.length(source) : 0);
            lengthVO.setValueDecimalLength(0);
        }

        return lengthVO;
    }

    /**
     * ? 0..1，可选项
     * * 0..n，可以没有，
     * 也可以有多项 + 1..n，至少有1项，也可以有多项
     * 1 数字1，代表必须且只能填1项 ^
     * Chioce节点，同一个父结点的多个Choice节点必须且只能填一项
     * <p/>
     * Description:
     * <p/>
     *
     * @param occurrences
     * @return
     */
    private static boolean isNeedNodeExist(String occurrences) {
        boolean flag = false;
        if (StringUtils.isNotBlank(occurrences)) {
            occurrences = occurrences.trim();
            if ("+".equals(occurrences) || "1".equals(occurrences)) {
                flag = true;
            }
        }
        return flag;
    }

    public static void main(String[] args) {
        System.out.println(Integer.class instanceof Object);
    }
}
