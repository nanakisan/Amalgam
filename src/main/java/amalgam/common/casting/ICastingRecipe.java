package amalgam.common.casting;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import amalgam.common.container.InventoryCasting;
import amalgam.common.properties.AmalgamPropertyList;

public interface ICastingRecipe {

    boolean matches(InventoryCasting inv, World world);

    ItemStack getCastingResult(InventoryCasting inv, AmalgamPropertyList list);

    int getRecipeSize();

    ItemStack getRecipeOutput();

}
