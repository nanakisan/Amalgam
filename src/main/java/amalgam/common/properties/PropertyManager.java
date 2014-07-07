package amalgam.common.properties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import amalgam.common.properties.Property.ComboType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class PropertyManager {

	private static final PropertyManager instance = new PropertyManager();
	
	private static HashMap<Item, List<Object>> registry = new HashMap<Item, List<Object>>();
    
	public static Property Maliablity; // used for weapon/armor strength 
	public static Property Density; // used for durability
	public static Property Luster; // used for enchantability
	public static Property Magnetism; // used for mining speed
	public static Property Hardness; // used for mining level
	
	public static final PropertyManager getInstance(){
		return instance;
	}
	 
	private PropertyManager(){
		Maliablity = new Property("Maliability", 4, ComboType.QUADAVERAGE);
	    Density = new Property("Density", 7, ComboType.QUADAVERAGE);
	    Luster = new Property("Luster", 10, ComboType.QUADAVERAGE);
	    Magnetism = new Property("Magnetism", 6, ComboType.QUADAVERAGE);
	    Hardness = new Property("Hardness", 1, ComboType.QUADAVERAGE);
	}

    // Pass null for AttributeList to use an Items NBT tags for attributes instead of preregistered values
    // Volume is 8 for an ingot, 1 for a nugget, -1 means use nbt
    public static void registerItemProperties(ItemStack stack){
    	Item item = stack.getItem();
    	List<Object> blob = Arrays.asList(new Object[]{null, Integer.valueOf(-1)});
    	registry.put(item, blob);	
    }
	
    // Pass null for AttributeList to use an Items NBT tags for attributes instead of preregistered values
    // Volume is 8 for an ingot, 1 for a nugget
    public static void registerItemProperties(ItemStack stack, PropertyList list, int amount){
    	Item item = stack.getItem();
    	List<Object> blob = Arrays.asList(new Object[]{list, Integer.valueOf(amount)});
    	registry.put(item, blob);	
    }
    
    // this is preferred over using ArmorMaterial
    public static void registerItemProperties(ItemStack stack, ToolMaterial mat, int amount){
    	Item item = stack.getItem();
    	PropertyList list = new PropertyList();
    	
    	list.add(Density, (float)Math.sqrt(mat.getMaxUses()));
    	list.add(Hardness, mat.getHarvestLevel());
    	list.add(Luster, mat.getEnchantability());
    	list.add(Magnetism, mat.getEfficiencyOnProperMaterial());
    	list.add(Maliablity, mat.getDamageVsEntity()*2 + 2);
    	
    	List<Object> blob = Arrays.asList(new Object[]{list, Integer.valueOf(amount)});
    	registry.put(item, blob);	
    }
    
    public static void registerItemProperties(ItemStack stack, ArmorMaterial mat, int amount){
    	Item item = stack.getItem();
    	PropertyList list = new PropertyList();
    	
    	list.add(Density, mat.getDurability(0)/2);
    	list.add(Hardness, mat.getDamageReductionAmount(1)/2 - 1);
    	list.add(Luster, mat.getEnchantability());
    	list.add(Magnetism, mat.getEnchantability()/2);
    	list.add(Maliablity, mat.getDamageReductionAmount(1));
    	
    	List<Object> blob = Arrays.asList(new Object[]{list, Integer.valueOf(amount)});
    	registry.put(item, blob);	
    }
    
    public static PropertyList getProperties(ItemStack stack){
    	Item item = stack.getItem();
    	List<Object> blob = (List<Object>) registry.get(item);
		PropertyList attrList = (PropertyList)blob.get(0);
    	return attrList;
    }
    
    public static int getVolume(ItemStack stack){
    	Item item = stack.getItem();
    	List<Object> blob = (List<Object>) registry.get(item);
		int volume = (Integer)blob.get(1);
    	return volume;
    }
    
    public static boolean itemIsAmalgable(ItemStack stack){
    	Item item = stack.getItem();
    	if(registry.containsKey(item)){
    		return true;
    	}
    	return false;   	
    }
}
