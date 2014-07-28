package amalgam.common.properties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import amalgam.common.Amalgam;
import amalgam.common.fluid.IAmalgableItem;
import amalgam.common.properties.Property.ComboType;

public class PropertyManager {

	private static final PropertyManager instance = new PropertyManager();
	
	private static HashMap<Item, List<Object>> registry = new HashMap<Item, List<Object>>();
	
	public static Property Maliablity; // used for weapon/armor strength 
	public static Property Density; // used for durability
	public static Property Luster; // used for enchantability
	public static Property Hardness; // used for mining level
	
	public static final PropertyManager getInstance(){
		return instance;
	}
	 
	private PropertyManager(){
		Maliablity = new Property("Maliability", 4, ComboType.QUADAVERAGE);
	    Density = new Property("Density", 6, ComboType.QUADAVERAGE);
	    Luster = new Property("Luster", 10, ComboType.QUADAVERAGE);
	    Hardness = new Property("Hardness", 1, ComboType.QUADAVERAGE);
	}
	
    // Pass null for AttributeList to use an Items NBT tags for attributes instead of pre-registered values
    // Volume is 8 for an ingot, 1 for a nugget, -1 to get from NBT
    public static void registerItemProperties(ItemStack stack, PropertyList list, int volume){
    	Item item = stack.getItem();
    	if(list !=null && volume > 0){
    		List<Object> blob = Arrays.asList(new Object[]{list, Integer.valueOf(volume)});
    		registry.put(item, blob);	
    	}else if(stack.getItem() instanceof IAmalgableItem){
    		registry.put(item, null);
    	}else{
    		Amalgam.log.error("Trying to register an amalgable item with improper values or not implementing IAmalgableItem");
    	}
    }
    
    public static PropertyList generatePropertiesFromToolMaterial(ToolMaterial mat){
    	PropertyList list = new PropertyList();
    	
    	list.add(Density, (float)Math.sqrt(mat.getMaxUses()/(mat.getHarvestLevel()+1)));
    	list.add(Hardness, mat.getHarvestLevel()+1);
    	list.add(Luster, mat.getEnchantability());
    	// mat.getEfficiencyOnProperMaterial();
    	list.add(Maliablity, mat.getDamageVsEntity()*2 + 2);
    	
    	return list;
    }
    
    public static PropertyList generatePropertiesFromArmorMaterial(ArmorMaterial mat){
    	PropertyList list = new PropertyList();

    	list.add(Density, mat.getDurability(1)/11);
    	list.add(Hardness, mat.getDamageReductionAmount(1)/2);
    	list.add(Luster, mat.getEnchantability());
    	list.add(Maliablity, mat.getDamageReductionAmount(1));
    	
    	return list;
    }
    
    public static PropertyList getProperties(ItemStack stack){
    	Item item = stack.getItem();
    	List<Object> blob = (List<Object>) registry.get(item);
    	
    	// if the blob is null we get the property list using this
    	if(blob == null){
    		return ((IAmalgableItem) item).getProperties(stack);
    	}

    	// otherwise we get the property list using the registered value
    	return (PropertyList)blob.get(0);
    }
    
    public static int getVolume(ItemStack stack){
    	Item item = stack.getItem();
    	List<Object> blob = (List<Object>) registry.get(item);

    	// if the blob is null we get the volume using this
    	if(blob == null){
    		return ((IAmalgableItem) item).getVolume(stack);
    	}
    	
    	// otherwise we get the volume using the registered value
		return (Integer)blob.get(1);
    }
    
    public static boolean itemIsAmalgable(ItemStack stack){
    	Item item = stack.getItem();
    	if(registry.containsKey(item)){
    		return true;
    	}
    	return false;   	
    }
}
