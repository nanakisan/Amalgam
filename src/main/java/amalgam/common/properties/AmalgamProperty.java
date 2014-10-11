package amalgam.common.properties;

import java.util.HashMap;
import java.util.Map;

public class AmalgamProperty {

    public enum ComboType {
        MAX, MIN, AVERAGE, QUADAVERAGE, COLOR
    }

    private static Map<String, AmalgamProperty> properties = new HashMap<String, AmalgamProperty>();

    private final ComboType              comboType;
    private final float                  defaultValue;
    private final String                 name;

    public AmalgamProperty(String name) {
        this.name = name;
        this.comboType = ComboType.QUADAVERAGE;
        this.defaultValue = 0;
        properties.put(this.name, this);
    }

    public AmalgamProperty(String name, float dValue) {
        this.name = name;
        this.defaultValue = dValue;
        this.comboType = ComboType.QUADAVERAGE;
        properties.put(this.name, this);
    }

    public AmalgamProperty(String name, float dValue, ComboType cType) {
        this.name = name;
        this.defaultValue = dValue;
        this.comboType = cType;
        properties.put(this.name, this);
    }

    public static AmalgamProperty getProperty(String name) {
        return (AmalgamProperty) properties.get(name);
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

    public static AmalgamProperty[] getAll() {
        return (AmalgamProperty[]) properties.values().toArray(new AmalgamProperty[] {});
    }

}
