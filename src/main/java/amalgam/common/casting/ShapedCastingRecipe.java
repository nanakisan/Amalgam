package amalgam.common.casting;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import amalgam.common.container.InventoryCasting;
import amalgam.common.properties.PropertyList;

public class ShapedCastingRecipe implements ICastingRecipe {

    public final int         recipeWidth;
    public final int         recipeHeight;
    public final ItemStack[] recipeItems;
    private final ICastItem  recipeOutput;

    private final int        amount;

    public ShapedCastingRecipe(int cols, int rows, ItemStack[] aitemstack, ICastItem output, int amount) {
        this.recipeWidth = cols;
        this.recipeHeight = rows;
        this.recipeItems = aitemstack.clone();
        this.recipeOutput = output;
        this.amount = amount;
    }

    @Override
    public boolean matches(InventoryCasting inv, World world) {
        for (int i = 0; i <= 3 - this.recipeWidth; ++i) {
            for (int j = 0; j <= 3 - this.recipeHeight; ++j) {
                if (this.checkMatch(inv, i, j, true)) {
                    return true;
                }

                if (this.checkMatch(inv, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(InventoryCasting inv, int row, int col, boolean flag) {
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 3; ++l) {
                int i1 = k - row;
                int j1 = l - col;
                ItemStack itemstack = null;

                if (i1 >= 0 && j1 >= 0 && i1 < this.recipeWidth && j1 < this.recipeHeight) {
                    if (flag) {
                        itemstack = this.recipeItems[this.recipeWidth - i1 - 1 + j1 * this.recipeWidth];
                    } else {
                        itemstack = this.recipeItems[i1 + j1 * this.recipeWidth];
                    }
                }

                ItemStack itemstack1 = inv.getStackInRowAndColumn(k, l);
                if (itemstack1 == null && inv.getCastState(k + l * 3) != 0) {
                    itemstack1 = new ItemStack(Blocks.fire);
                }

                if (itemstack1 != null || itemstack != null) {
                    if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null) {
                        return false;
                    }

                    if (itemstack.getItem() != itemstack1.getItem()) {
                        return false;
                    }

                    if (itemstack.getItemDamage() != 32767 && itemstack.getItemDamage() != itemstack1.getItemDamage()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public int getRecipeSize() {
        return this.recipeWidth * this.recipeHeight;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput.generateStackWithProperties(null, amount);
    }

    @Override
    public ItemStack getCastingResult(InventoryCasting inv, PropertyList list) {
        return this.recipeOutput.generateStackWithProperties(list, this.amount);
    }

}
