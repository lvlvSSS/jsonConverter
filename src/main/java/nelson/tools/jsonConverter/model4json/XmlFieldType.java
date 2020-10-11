package nelson.tools.jsonConverter.model4json;

/**
 * The 'Type' of the 'field', means that the field needs the type of the value.
 *
 */
public enum XmlFieldType {
	NONE("none"), STRING("string"), INT("int"), FLOAT("float"), BOOLEAN("boolean"), LIST("list"), MAP("map");

	private XmlFieldType(String name) {
		this.fieldType = name;
	}

	private String fieldType;

	@Override
	public String toString() {
		return this.fieldType;
	}
}
