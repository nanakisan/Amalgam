package amalgam.common.properties;

import java.util.HashMap;
import java.util.Map;

public class Property {

    public enum ComboType {
        MAX, // this should be used sparingly
        MIN, // this should be used sparingly
        AVERAGE, // a basic average of all the values
        QUADAVERAGE // this is the default method of combination, it shifts
                    // towards higher values
    }

    private static Map<String, Property> properties = new HashMap<String, Property>();

    private final ComboType comboType;
    private final float defaultValue;
    private final String name;

    public Property(String n) {
        this.name = n;
        this.comboType = ComboType.QUADAVERAGE;
        this.defaultValue = 0;
        properties.put(this.name, this);
    }

    public Property(String n, float dValue) {
        this.name = n;
        this.defaultValue = dValue;
        this.comboType = ComboType.QUADAVERAGE;
        properties.put(this.name, this);
    }

    public Property(String n, float dValue, ComboType cType) {
        this.name = n;
        this.defaultValue = dValue;
        this.comboType = cType;
        properties.put(this.name, this);
    }

    public static Property getProperty(String n) {
        return (Property) properties.get(n);
    }

    public float getDefaultValue() {
        return this.defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public ComboType getComboType() {
        return this.comboType;
    }

    public static Property[] getAll() {
        return (Property[]) properties.values().toArray(new Property[] {});
    }

}
