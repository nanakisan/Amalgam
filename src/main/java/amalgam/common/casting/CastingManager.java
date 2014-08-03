package amalgam.common.casting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import amalgam.common.Amalgam;
import amalgam.common.container.InventoryCasting;

public final class CastingManager {

    /** The static instance of this class */
    private static final CastingManager INSTANCE = new CastingManager();

    /** A list of all the recipes added */
    private static List<ICastingRecipe> recipes  = new ArrayList<ICastingRecipe>();

    /**
     * Returns the static instance of this class
     */
    public static CastingManager getInstance() {
        return INSTANCE;
    }

    private CastingManager() {
        // TODO add recipes here?
    }

    /* public ShapedRecipes addRecipe(ItemStack p_92103_1_, Object ... p_92103_2_) { String s = ""; int i = 0; int j =
     * 0; int k = 0; if (p_92103_2_[i] instanceof String[]) { String[] astring = (String[])((String[])p_92103_2_[i++]);
     * for (int l = 0; l < astring.length; ++l) { String s1 = astring[l]; ++k; j = s1.length(); s = s + s1; } } else {
     * while (p_92103_2_[i] instanceof String) { String s2 = (String)p_92103_2_[i++]; ++k; j = s2.length(); s = s + s2;
     * } } HashMap hashmap; for (hashmap = new HashMap(); i < p_92103_2_.length; i += 2) { Character character =
     * (Character)p_92103_2_[i]; ItemStack itemstack1 = null; if (p_92103_2_[i + 1] instanceof Item) { itemstack1 = new
     * ItemStack((Item)p_92103_2_[i + 1]); } else if (p_92103_2_[i + 1] instanceof Block) { itemstack1 = new
     * ItemStack((Block)p_92103_2_[i + 1], 1, 32767); } else if (p_92103_2_[i + 1] instanceof ItemStack) { itemstack1 =
     * (ItemStack)p_92103_2_[i + 1]; } hashmap.put(character, itemstack1); } ItemStack[] aitemstack = new ItemStack[j *
     * k]; for (int i1 = 0; i1 < j * k; ++i1) { char c0 = s.charAt(i1); if (hashmap.containsKey(Character.valueOf(c0)))
     * { aitemstack[i1] = ((ItemStack)hashmap.get(Character.valueOf(c0))).copy(); } else { aitemstack[i1] = null; } }
     * ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, p_92103_1_); this.recipes.add(shapedrecipes);
     * return shapedrecipes; } */

    public static void addShapelessRecipe(ICastItem output, int amount, Object... recipeInput) {
        Amalgam.LOG.info("Registering shapeless recipe");

        ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>();
        Object[] recipeList = recipeInput;
        int numRecipeItems = recipeList.length;

        for (int itemIndex = 0; itemIndex < numRecipeItems; ++itemIndex) {
            Object component = recipeList[itemIndex];

            if (component instanceof ItemStack) {
                arraylist.add(((ItemStack) component).copy());
            } else if (component instanceof Item) {
                arraylist.add(new ItemStack((Item) component));
            } else if (component instanceof String) {
                Amalgam.LOG.info("string component found in shapeless recipe");
                if (((String) component).equalsIgnoreCase("a") || ((String) component).equalsIgnoreCase("amalgam")) {
                    // we use a stack of fire to represent amalgam, might want to change this eventually
                    arraylist.add(new ItemStack(Blocks.fire));
                }
            } else {
                if (!(component instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless recipy!");
                }

                arraylist.add(new ItemStack((Block) component));
            }
        }

        recipes.add(new ShapelessCastingRecipe(output, amount, arraylist));
    }

    public static ICastingRecipe findMatchingRecipe(InventoryCasting inv, World world) {
        Amalgam.LOG.info("looking for a matching recipe");
        for (int recipeIndex = 0; recipeIndex < recipes.size(); ++recipeIndex) {
            ICastingRecipe irecipe = (ICastingRecipe) recipes.get(recipeIndex);

            if (irecipe.matches(inv, world)) {
                Amalgam.LOG.info("found match!");
                return irecipe;
            }
        }

        return null;
    }

    /**
     * returns the List<> of all recipes
     */
    public static List<ICastingRecipe> getRecipeList() {
        return recipes;
    }
}
