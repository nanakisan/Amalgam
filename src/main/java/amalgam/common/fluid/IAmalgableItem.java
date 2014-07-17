package amalgam.common.fluid;

import net.minecraft.item.ItemStack;
import amalgam.common.properties.PropertyList;

public interface IAmalgableItem {
	
	public int getVolume(ItemStack stack);
	public PropertyList getProperties(ItemStack stack);
	
}
