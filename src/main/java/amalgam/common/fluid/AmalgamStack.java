package amalgam.common.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import amalgam.common.Amalgam;
import amalgam.common.properties.Property;
import amalgam.common.properties.PropertyList;

public class AmalgamStack extends FluidStack {

    private static final String PROPERTY_KEY = "Tag";
    private static final String AMOUNT_KEY   = "Amount";

    public AmalgamStack(int amount, PropertyList pList) {
        super(Amalgam.fluidAmalgam.getID(), amount, null);
        this.tag = new NBTTagCompound();
        if (pList == null) {
            new PropertyList().writeToNBT(this.tag);
        } else {
            pList.writeToNBT(this.tag);
        }
    }

    public AmalgamStack(AmalgamStack resource, int amount) {
        super(Amalgam.fluidAmalgam.getID(), amount, resource.tag);
    }

    public static AmalgamStack combine(AmalgamStack stackA, AmalgamStack stackB) {
        Property[] allProperties = Property.getAll();
        PropertyList aList = stackA.getProperties();
        PropertyList bList = stackB.getProperties();
        PropertyList newList = new PropertyList();
        for (Property p : allProperties) {
            float aValue = aList.getValue(p);
            float bValue = bList.getValue(p);
            float result = 0;
            switch (p.getComboType()) {
                case AVERAGE:
                    result = (aValue * stackA.amount + bValue * stackB.amount) / (stackA.amount + stackB.amount);
                    break;
                case QUADAVERAGE:
                    result = (float) Math.sqrt((aValue * aValue * stackA.amount + bValue * bValue * stackB.amount) / (stackA.amount + stackB.amount));
                    break;
                case MAX:
                    result = Math.max(aValue, bValue);
                    break;
                case MIN:
                    result = Math.min(aValue, bValue);
                    break;
                default:
                    result = (aValue * stackA.amount + bValue * stackB.amount) / (stackA.amount + stackB.amount);
                    break;
            }
            newList.add(p, result);
        }

        return new AmalgamStack(stackA.amount + stackB.amount, newList);
    }

    public PropertyList getProperties() {
        PropertyList pList = new PropertyList();
        pList.readFromNBT(this.tag);
        return pList;
    }

    public static AmalgamStack loadAmalgamStackFromNBT(NBTTagCompound nbtComp) {
        int amount = nbtComp.getInteger(AMOUNT_KEY);
        PropertyList pList = new PropertyList();
        pList.readFromNBT(nbtComp.getCompoundTag(PROPERTY_KEY));
        return new AmalgamStack(amount, pList);
    }

}
