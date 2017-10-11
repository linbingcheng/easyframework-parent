package top.bingchenglin.commons.c2n;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Code2NameFactory {
    private static final Logger LOGGER = LogManager.getLogger(Code2NameFactory.class);

    private static final String TRANSLATION_XSD = "top/bingchenglin/commons/c2n/translation-config.xsd";

    private static final String TRANSLATION_XML = "META-INF/translation.xml";

    private static final Map<String, Handler> HANDLER_MAP = new HashMap<String, Handler>();

    private static final Map<String, Dictionary> DICTIONARY_MAP = new HashMap<String, Dictionary>();

    private static Code2NameFactory _INSTANCE;

    static {
        LOGGER.info("Code2NameFactory.init");
        try {
            JAXBContext context = JAXBContext.newInstance(Translation.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            unmarshaller.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(classLoader.getResource(TRANSLATION_XSD)));
            JAXBElement<Translation> root = unmarshaller.unmarshal(
                    new StreamSource(classLoader.getResource(TRANSLATION_XML).openStream()), Translation.class);
            Translation translation = root.getValue();

            List<Handler> handlerList = translation.getHandler();
            if (handlerList != null && !handlerList.isEmpty()) {
                for (Handler handler : handlerList) {
                    HANDLER_MAP.put(handler.getSymbol(), handler);
                }
            }

            List<Dictionary> dictionaryList = translation.getDictionary();
            if (dictionaryList != null && !dictionaryList.isEmpty()) {
                for (Dictionary dictionary : dictionaryList) {
                    DICTIONARY_MAP.put(dictionary.getGroup(), dictionary);
                }
            }
        } catch (Exception e) {
            LOGGER.error("TranslationUtil init failure!", e);
        }
    }

    private Code2NameFactory() {
        // private
    }

    public static Code2NameFactory getInstance() {
        if (_INSTANCE == null) {
            synchronized (Code2NameFactory.class) {
                if (_INSTANCE == null) {
                    _INSTANCE = new Code2NameFactory();
                }
            }
        }
        return _INSTANCE;
    }

    public Code2Name textCode2Name(String group, String code) {
        Code2Name code2Name = null;
        List<Code2Name> code2NameList = listCode2Name(group);
        if (code2NameList != null && !code2NameList.isEmpty()) {
            for (Code2Name c2n : code2NameList) {
                if (c2n.getCode().equals(code)) {
                    code2Name = c2n;
                    break;
                }
            }
        }
        return code2Name;
    }

    public List<Code2Name> listCode2Name(String group) {
        List<Code2Name> code2NameList = new ArrayList<Code2Name>();
        Dictionary dictionary = DICTIONARY_MAP.get(group);
        if (dictionary != null) {
            Translatable translatable = getTranslatable(group);
            if (translatable != null) {
                code2NameList = translatable.listCode2Name(dictionary);
            } else {
                if (null != dictionary.getItem()) {
                    for (Item item : dictionary.getItem()) {
                        Code2Name code2Name = new Code2Name();
                        code2Name.setCode(item.getCode());
                        code2Name.setValue(item.getValue());
                        code2NameList.add(code2Name);
                    }
                }
            }
        }
        return code2NameList;
    }

    private Translatable getTranslatable(String group) {
        String symbol = String.valueOf(group.charAt(0));
        if (HANDLER_MAP.containsKey(symbol)) {
            Handler handler = HANDLER_MAP.get(symbol);
            try {
                Class<?> clazz = Class.forName(handler.getClazz());
                if (Translatable.class.isAssignableFrom(clazz)) {
                    Translatable translatable = (Translatable) clazz.newInstance();
                    return translatable;
                } else {
                    // todo logger.warn
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                // ignore
            }
        }
        return null;
    }
}