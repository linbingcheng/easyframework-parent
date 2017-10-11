package top.bingchenglin.commons.validator;

public class AttrLengthVO {
	private int valueIntegerLength = 0;
	private int valueDecimalLength = 0;

	private int totalFormateLength = 0;
	private int formateDecimalLength = 0;

	private boolean isFloatValue = true;

	public int getValueTotalLength() {
		return this.valueIntegerLength + this.valueDecimalLength;
	}

	public int getValueIntegerLength() {
		return valueIntegerLength;
	}

	public void setValueIntegerLength(int integerLength) {
		this.valueIntegerLength = integerLength;
	}

	public int getValueDecimalLength() {
		return valueDecimalLength;
	}

	public void setValueDecimalLength(int decimalLength) {
		this.valueDecimalLength = decimalLength;
	}

	public int getTotalFormateLength() {
		return totalFormateLength;
	}

	public void setTotalFormateLength(int totalFormateLength) {
		this.totalFormateLength = totalFormateLength;
	}

	public int getFormateDecimalLength() {
		return formateDecimalLength;
	}

	public void setFormateDecimalLength(int formateDecimalLength) {
		this.formateDecimalLength = formateDecimalLength;
	}

	public boolean isFloatValue() {
		return isFloatValue;
	}

	public void setFloatValue(boolean isIntegerValue) {
		this.isFloatValue = isIntegerValue;
	}
}
