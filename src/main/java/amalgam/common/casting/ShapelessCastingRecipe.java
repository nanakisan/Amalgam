package amalgam.common.casting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import amalgam.common.container.InventoryCasting;

public class ShapelessCastingRecipe implements ICastingRecipe {

    /** Is the Item that you get when craft the recipe. */
    private final ICastItem recipeOutput;

    /** The amount of items in the itemStack output */
    private final int       amount;

    /** Is a List of ItemStack that composes the recipe. */
    public final List       recipeItems;

    public ShapelessCastingRecipe(ICastItem item, int amount, List list) {
        this.recipeOutput = item;
        this.amount = amount;
        this.recipeItems = list;
    }

    public ItemStack getRecipeOutput() {
        return this.recipeOutput.generateStackWithProperties(null, amount);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCasting inv, World world) {
        ArrayList arraylist = new ArrayList(this.recipeItems);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (itemstack != null) {
                    boolean flag = false;
                    Iterator iterator = arraylist.iterator();

                    while (iterator.hasNext()) {
                        ItemStack itemstack1 = (ItemStack) iterator.next();

                        if (itemstack.getItem() == itemstack1.getItem()
                                && (itemstack1.getItemDamage() == 32767 || itemstack.getItemDamage() == itemstack1.getItemDamage())) {
                            flag = true;
                            arraylist.remove(itemstack1);
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }
            }
        }
        return arraylist.isEmpty();
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize() {
        return this.recipeItems.size();
    }

    public int getRecipeAmount() {
        return this.amount;
    }

    @Override
    public ItemStack getCastingResult(InventoryCasting inv) {
        return this.recipeOutput.generateStackWithProperties(inv.getPropertyList(), this.amount);
    }

}
