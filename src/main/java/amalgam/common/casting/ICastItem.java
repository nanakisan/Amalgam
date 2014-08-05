package amalgam.common.casting;

import net.minecraft.item.ItemStack;
import amalgam.common.properties.PropertyList;

public interface ICastItem {
    ItemStack generateStackWithProperties(PropertyList pList, int stackSize);
}
