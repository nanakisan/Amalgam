package amalgam.common.properties;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AmalgamPropertyList {

    private final Map<AmalgamProperty, Float> properties = new HashMap<AmalgamProperty, Float>();

    public AmalgamPropertyList() {
    }

    public AmalgamPropertyList(ItemStack stack) {
        AmalgamPropertyList temp = AmalgamPropertyManager.getProperties(stack);

        if (temp != null) {
            for (AmalgamProperty a : temp.getProperties()) {
                add(a, temp.getValue(a));
            }
        }
    }

    public AmalgamProperty[] getProperties() {
        AmalgamProperty[] q = new AmalgamProperty[1];
        return (AmalgamProperty[]) this.properties.keySet().toArray(q);
    }

    public float getValue(AmalgamProperty a) {
        if (this.properties.get(a) == null || this.properties.get(a).isNaN()) {
            return a.getDefaultValue();
        }

        return ((Float) this.properties.get(a)).floatValue();
    }

    public AmalgamPropertyList add(AmalgamProperty a, float value) {
        this.properties.put(a, value);

        return this;
    }

    public AmalgamPropertyList remove(AmalgamProperty a) {
        this.properties.remove(a);

        return this;
    }

    public void readFromNBT(NBTTagCompound nbtComp) {
        this.properties.clear();
        NBTTagList list = nbtComp.getTagList("AmalgamProperties", 10);

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound attr = list.getCompoundTagAt(i);

            if (attr.hasKey("name")) {
                add(AmalgamProperty.getProperty(attr.getString("name")), attr.getFloat("value"));
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbtComp) {
        NBTTagList list = new NBTTagList();
        nbtComp.setTag("AmalgamProperties", list);

        for (AmalgamProperty attr : getProperties()) {
            if (attr != null) {
                NBTTagCompound attrComp = new NBTTagCompound();
                attrComp.setString("name", attr.getName());
                attrComp.setFloat("value", getValue(attr));
                list.appendTag(attrComp);
            }
        }
    }

    public String toString() {
        StringBuffer returnString = new StringBuffer();

        for (AmalgamProperty attr : getProperties()) {
            if (attr != null) {
                returnString.append(attr.getName()).append(": ").append(this.getValue(attr)).append('\n');
            }
        }

        return returnString.toString();
    }
}
