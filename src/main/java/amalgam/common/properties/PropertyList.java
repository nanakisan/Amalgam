package amalgam.common.properties;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PropertyList{

	private HashMap<Property, Float> properties = new HashMap<Property, Float>();
	
	public PropertyList(){
		
	}
	
	public PropertyList(ItemStack stack){
		PropertyList temp = PropertyManager.getProperties(stack);
		if(temp != null){
			for(Property a : temp.getProperties()){
				add(a, temp.getValue(a));
			}
		}
	}
	
	public Property[] getProperties(){
		Property[] q = new Property[1];
		return (Property[])this.properties.keySet().toArray(q);
	}
	
	public float getValue(Property a){
		if(this.properties.get(a) == null || this.properties.get(a).isNaN()){
			return a.defaultValue;
		}
		return ((Float)this.properties.get(a)).floatValue();
	}
	
	public PropertyList add(Property a, float value){
		this.properties.put(a, value);
		return this;
	}
	
	public PropertyList remove(Property a){
		this.properties.remove(a);
		return this;
	}
	
	public void readFromNBT(NBTTagCompound nbtComp){
		this.properties.clear();
		NBTTagList list = nbtComp.getTagList("AmalgamProperties", 10);
		//Amalgam.log.info(list.toString());
		for(int i=0; i<list.tagCount(); i++){
			NBTTagCompound attr = list.getCompoundTagAt(i);
			if(attr.hasKey("name")){
				add(Property.getProperty(attr.getString("name")), attr.getFloat("value"));
			}
		}
	}
	
	public void writeToNBT(NBTTagCompound nbtComp){
		NBTTagList list = new NBTTagList();
		nbtComp.setTag("AmalgamProperties", list);
		for(Property attr : getProperties()){
			if(attr != null){
				NBTTagCompound attrComp = new NBTTagCompound();
				attrComp.setString("name",attr.name);
				attrComp.setFloat("value",getValue(attr));
				list.appendTag(attrComp);
			}
		}
	}
	
	public String toString(){
		String returnString = "";
		for(Property attr : getProperties()){
			if(attr != null){
				returnString = returnString + attr.getName() + ":" + this.getValue(attr) + "\n";
			}
		}
		return returnString;
	}
}
