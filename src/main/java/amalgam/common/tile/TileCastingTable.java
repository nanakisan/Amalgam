package amalgam.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import amalgam.common.container.ContainerCastingTable;
import amalgam.common.fluid.AmalgamTank;

public class TileCastingTable extends TileEntity implements IInventory, ISidedInventory, IFluidHandler{

	private ItemStack[] castingMatrix;
	private ItemStack castResult;
	
	// keeps track of which slots are depressed for casting
	private int castState[] = {0,0,0,0,0,0,0,0,0};
	
	// store the amalgam used in casting here
	private AmalgamTank tank;
	
	public ContainerCastingTable container;
	
	/////////////////////
	// ISidedInventory //
	/////////////////////
	
	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int meta){
		// return true if the casting state for the slot is 0
		return castState[slot]==0;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int meta){
		// TODO Auto-generated method stub
		return false;
	}

	////////////////
	// IInventory //
	////////////////
	
	@Override
	public int getSizeInventory(){
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot){
		if(castState[slot] == 1) return null;
		
		return castingMatrix[slot];
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(castState[slot]==1) return;
		
		castingMatrix[slot] = stack;
	}

	@Override
	public String getInventoryName(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomInventoryName(){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit(){
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openInventory(){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory(){
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_){
		// TODO Auto-generated method stub
		return false;
	}

	///////////////////
	// IFluidHandler //
	///////////////////
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill){
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from){
		// TODO Auto-generated method stub
		return null;
	}

}
