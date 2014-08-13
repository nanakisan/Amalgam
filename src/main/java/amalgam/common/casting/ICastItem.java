package amalgam.common.casting;

import net.minecraft.item.ItemStack;
import amalgam.common.properties.PropertyList;

public interface ICastItem {

    // TODO consider making this depend on the non-amalgam materials used to create the item. Will allow NBT data from
    // other components to affect the outcome
    ItemStack generateStackWithProperties(PropertyList pList, int stackSize);

}
