package amalgam.common.casting;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import amalgam.common.container.InventoryCasting;
import amalgam.common.properties.PropertyList;

public class ShapedCastingRecipe implements ICastingRecipe {

    @Override
    public boolean matches(InventoryCasting inv, World world) {
        return false;
    }

    @Override
    public int getRecipeSize() {
        return 0;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ItemStack getCastingResult(InventoryCasting inv, PropertyList list) {
        return null;
    }

}
