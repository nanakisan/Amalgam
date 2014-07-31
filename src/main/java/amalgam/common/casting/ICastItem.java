package amalgam.common.casting;

import net.minecraft.item.ItemStack;
import amalgam.common.properties.PropertyList;

/**
 * This interface should be used for items created through the casting table or other cast methods. It returns the
 * proper ItemSTack for the given properties used to create the item. This allows for different items to be returned
 * based on the properties.
 */
public interface ICastItem {
    ItemStack generateStackWithProperties(PropertyList pList, int stackSize);
}
