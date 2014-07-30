package amalgam.common.fluid;

import net.minecraft.item.ItemStack;
import amalgam.common.properties.PropertyList;

public interface IAmalgableItem {
    int getVolume(ItemStack stack);

    PropertyList getProperties(ItemStack stack);
}
