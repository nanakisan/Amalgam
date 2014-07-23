package amalgam.common.fluid;

import net.minecraft.item.ItemStack;

public interface IAmalgamContainerItem{
	
	/** returns the AmalgamStack contained in the given ItemStack*/
	public AmalgamStack getFluid(ItemStack container);
	
	/** attempts to fill the itemStack with the given amalgamStack if doFill is true
	 * and returns the amount of fluid put into the item */
	public int fill(ItemStack container, AmalgamStack resource, boolean doFill);
	
	public AmalgamStack drain(ItemStack container, int maxDrain, boolean doDrain);
	
	public int getCapacity(ItemStack container);

	public int getEmptySpace(ItemStack stack);
}
