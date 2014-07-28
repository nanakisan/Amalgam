package amalgam.common.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import amalgam.common.Amalgam;
import amalgam.common.properties.Property;
import amalgam.common.properties.PropertyList;

public class AmalgamStack extends FluidStack {
	
	public AmalgamStack(int amount, PropertyList pList) {
		super(Amalgam.fluidAmalgam.getID(), amount, null);
		// here we store the property list in the FluidStack's "tag" variable, which is an NBTTagCompound
		if(pList == null){
			pList = new PropertyList();
		}
		this.tag = new NBTTagCompound();
		pList.writeToNBT(this.tag);
		
	}
	
	public AmalgamStack(AmalgamStack resource, int amount) {
		super(Amalgam.fluidAmalgam.getID(), amount, resource.tag);
	}

	public static AmalgamStack combine(AmalgamStack a, AmalgamStack b){
		Property[] allProperties = Property.getAll();
		PropertyList aList = a.getProperties();
		PropertyList bList = b.getProperties();
		PropertyList newList = new PropertyList();
		for(Property p : allProperties){
			float aValue = aList.getValue(p);
			float bValue = bList.getValue(p);
			float result = 0;
			switch(p.getComboType()){
				case AVERAGE:
					result = (aValue * a.amount + bValue * b.amount) / (a.amount + b.amount);
					break;
				case QUADAVERAGE:
					result = (float) Math.sqrt((aValue*aValue*a.amount + bValue*bValue*b.amount) / (a.amount + b.amount));
					break;
				case MAX:
					result = Math.max(aValue, bValue);
					break;
				case MIN:
					result = Math.min(aValue, bValue);
					break;
				default: // default to average
					result = (aValue * a.amount + bValue * b.amount) / (a.amount + b.amount);
					break;
			}
			newList.add(p, result);
		}
			
		return new AmalgamStack(a.amount + b.amount, newList);
	}
	
	public PropertyList getProperties(){
		PropertyList pList = new PropertyList();
		// property list is stored in FluiStack's tag variable
		pList.readFromNBT(this.tag);
		return pList;
	}

	public static AmalgamStack loadAmalgamStackFromNBT(NBTTagCompound nbtComp){
		int amount = nbtComp.getInteger("Amount");
		PropertyList pList = new PropertyList();
		// Storing propertyList info in the base class 'FluidStack's tag variable. This variable is saved to the "Tag" tag in NBT
		pList.readFromNBT(nbtComp.getCompoundTag("Tag"));
        AmalgamStack stack = new AmalgamStack(amount, pList);
		return stack;
	}

}
