package amalgam.common.casting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import amalgam.common.properties.PropertyList;

public class ShapedCastingRecipe implements ICastingRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv, PropertyList p) {
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 0;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

}
