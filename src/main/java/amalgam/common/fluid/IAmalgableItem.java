package amalgam.common.fluid;

import net.minecraft.item.ItemStack;
import amalgam.common.properties.AmalgamPropertyList;

public interface IAmalgableItem {

    int getVolume(ItemStack stack);

    AmalgamPropertyList getProperties(ItemStack stack);
}
