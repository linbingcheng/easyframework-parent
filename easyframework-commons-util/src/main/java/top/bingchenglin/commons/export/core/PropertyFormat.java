package top.bingchenglin.commons.export.core;

public class PropertyFormat {

    private int propertyId;
    private String propertyName;
    private String format;
    private String formatType;

    public PropertyFormat(int propertyId, String propertyName, String format,
                          String formatType) {
        this.propertyName = propertyName;
        this.format = format;
        this.formatType = formatType;
        this.propertyId = propertyId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}
