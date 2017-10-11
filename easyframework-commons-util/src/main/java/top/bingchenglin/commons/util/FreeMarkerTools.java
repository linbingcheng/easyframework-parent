package top.bingchenglin.commons.util;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.StringWriter;
import java.util.Map;

public class FreeMarkerTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(FreeMarkerTools.class);

    public static String parseText(String text, Map<String, Object> params) {
        if (text.contains("<#") || text.contains("${")) {
            Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate("textTemplate", text);
            configuration.setTemplateLoader(templateLoader);
            configuration.setDefaultEncoding("UTF-8");
            // 设置标签类型([]、<>),[]这种标记解析要快些
            configuration.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
            // 设置允许属性为空
            // cfg.setClassicCompatible(true);
            try {
                Template template = configuration.getTemplate("textTemplate");
                StringWriter writer = new StringWriter();
                template.process(params, writer);
                text = writer.toString();
                LOGGER.debug("FreeMarker模板解析结果：" + text);
            } catch (Exception ex) {
                LOGGER.error("FreeMarker模板解析出错",ex);
            }
        }
        return text;
    }
}
