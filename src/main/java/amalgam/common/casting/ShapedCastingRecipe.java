package amalgam.common.casting;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import amalgam.common.container.InventoryCasting;

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
    public ItemStack getCastingResult(InventoryCasting inv) {
        return null;
    }

}
