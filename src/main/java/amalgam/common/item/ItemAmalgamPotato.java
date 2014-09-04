package amalgam.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;

public class ItemAmalgamPotato extends Item implements ICastItem {

    // TODO:A thowable grenade type item. crafted with amalgam BLOCKS! look at snowball implementation
    
    @Override
    public ItemStack generateStackWithProperties(PropertyList pList, ItemStack[] items, int stackSize) {
        return null;
    }

}
