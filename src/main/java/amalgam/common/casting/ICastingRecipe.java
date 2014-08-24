package amalgam.common.casting;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import amalgam.common.container.InventoryCasting;
import amalgam.common.properties.PropertyList;

public interface ICastingRecipe {

    boolean matches(InventoryCasting inv, World world);

    ItemStack getCastingResult(InventoryCasting inv, PropertyList list);

    int getRecipeSize();

    ItemStack getRecipeOutput();

}
