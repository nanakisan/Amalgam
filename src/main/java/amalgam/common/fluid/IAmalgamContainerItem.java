package amalgam.common.fluid;

import net.minecraft.item.ItemStack;

public interface IAmalgamContainerItem {

    /** Returns the AmalgamStack contained in the given ItemStack */
    AmalgamStack getFluid(ItemStack container);

    /**
     * Attempts to fill the itemStack with the given amalgamStack if doFill is true and returns the amount of fluid put
     * into the item
     */
    int fill(ItemStack container, AmalgamStack resource, boolean doFill);

    AmalgamStack drain(ItemStack container, int maxDrain, boolean doDrain);

    int getCapacity(ItemStack container);

    /** Returns the space left in the itemStack for additional fluid */
    int getEmptySpace(ItemStack stack);
}
