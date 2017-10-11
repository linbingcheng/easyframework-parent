//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.08 at 05:56:54 PM CST 
//


package top.bingchenglin.commons.c2n;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.asiainfo.gdm.easyframework.common.c2n package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Translation_QNAME = new QName("http://www.bingchenglin.top/easyframework/translation", "translation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.asiainfo.gdm.easyframework.common.c2n
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Translation }
     * 
     */
    public Translation createTranslation() {
        return new Translation();
    }

    /**
     * Create an instance of {@link Item }
     * 
     */
    public Item createItem() {
        return new Item();
    }

    /**
     * Create an instance of {@link Dictionary }
     * 
     */
    public Dictionary createDictionary() {
        return new Dictionary();
    }

    /**
     * Create an instance of {@link Handler }
     * 
     */
    public Handler createHandler() {
        return new Handler();
    }

    /**
     * Create an instance of {@link Entity }
     * 
     */
    public Entity createEntity() {
        return new Entity();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Translation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.bingchenglin.top/easyframework/translation", name = "translation")
    public JAXBElement<Translation> createTranslation(Translation value) {
        return new JAXBElement<Translation>(_Translation_QNAME, Translation.class, null, value);
    }

}