package amalgam.common.casting;

import net.minecraft.item.ItemStack;
import amalgam.common.properties.AmalgamPropertyList;

public interface ICastItem {

    ItemStack generateStackWithProperties(AmalgamPropertyList pList, ItemStack[] items, int stackSize);

}
