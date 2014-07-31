package amalgam.common.casting;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import amalgam.common.container.InventoryCasting;

public interface ICastingRecipe {
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    boolean matches(InventoryCasting inv, World world);

    /**
     * Returns an Item that is the result of this recipe based on an amalgam property list
     */
    ItemStack getCastingResult(InventoryCasting inv);

    /**
     * Returns the size of the recipe area
     */
    int getRecipeSize();

    ItemStack getRecipeOutput();
}
