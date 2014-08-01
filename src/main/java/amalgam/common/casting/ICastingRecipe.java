package amalgam.common.casting;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import amalgam.common.container.InventoryCasting;
import amalgam.common.properties.PropertyList;

public interface ICastingRecipe {
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    boolean matches(InventoryCasting inv, World world);

    /**
     * Returns an Item that is the result of this recipe based on an amalgam property list
     */
    ItemStack getCastingResult(InventoryCasting inv, PropertyList list);

    /**
     * Returns the size of the recipe area
     */
    int getRecipeSize();

    ItemStack getRecipeOutput();
}
