package amalgam.common.fluid;

import net.minecraft.item.ItemStack;

public interface IAmalgamContainerItem {

    AmalgamStack getFluid(ItemStack container);

    int fill(ItemStack container, AmalgamStack resource, boolean doFill);

    AmalgamStack drain(ItemStack container, int maxDrain, boolean doDrain);

    int getCapacity(ItemStack container);

    int getEmptySpace(ItemStack stack);
}
