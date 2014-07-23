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
import amalgam.common.Amalgam;
import amalgam.common.container.ContainerCastingTable;
import amalgam.common.fluid.AmalgamTank;

public class TileCastingTable extends TileEntity implements IInventory, ISidedInventory, IFluidHandler{

	public ItemStack[] castingMatrix = new ItemStack[9];
	public ItemStack castResult;
	
	// keeps track of which slots are depressed for casting
	private int castState[] = {0,0,0,0,0,0,0,0,0};
	
	// store the amalgam used in casting here
	private AmalgamTank tank;
	
	public ContainerCastingTable container;

	/////////////////////
	// ISidedInventory //
	/////////////////////
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side){
		int slots[] = {0,1,2,3,4,5,6,7,8,9};
		return slots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int meta){
		// return true if the casting state for the slot is 0, can't insert item into the result slot
		if(slot==9) return false;
		return castState[slot]==0;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int meta){
		// return false unless the slot is the crafted item slot and the item is completely crafted
		if(slot == 9) return true;
		
		return false;
	}

	////////////////
	// IInventory //
	////////////////
	
	@Override
	public int getSizeInventory(){
		// the 9 casting slots - the slots used for amalgam 
		int unusableSlots = 0;
		for(int i = 0; i < castState.length; i++ ){
			if(castState[i] != 0) unusableSlots++;
		}
		return 9 - unusableSlots;
	}

	@Override
	public ItemStack getStackInSlot(int slot){
		if(slot == 9) return castResult;
		
		if(castState[slot] == 1) return null;
		
		return castingMatrix[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int decNum){
		if(slot == 9){
			ItemStack stack = castResult;
			return stack.splitStack(decNum);
		}
		
		if(castState[slot] != 0) return null;
		
		ItemStack stack = castingMatrix[slot];
		return stack.splitStack(decNum);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot){
		// Does nothing because we keep the items in the slots on closing
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(slot == 9){
			castResult = stack;
		}
		else{
			if(castState[slot] != 0) return;
			castingMatrix[slot] = stack;
		}
	}

	@Override
	public String getInventoryName(){
		return "Casting Table";
	}

	@Override
	public boolean hasCustomInventoryName(){
		return true;
	}

	@Override
	public int getInventoryStackLimit(){
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_){
		// TODO look at other implementations
		return true;
	}

	@Override
	public void openInventory(){
		// TODO Probably need to do something here to open the gui
		
	}

	@Override
	public void closeInventory(){
		return;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack){
		if(slot == 9) return false;
		if(castState[slot] == 0) return true;
		return false;
	}

	///////////////////
	// IFluidHandler //
	///////////////////
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill){
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain){
		if(resource == null){
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain){
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid){
		if(fluid.getID() == Amalgam.fluidAmalgam.getID()){
			return true;
		}
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid){
		if(fluid.getID() == Amalgam.fluidAmalgam.getID()){
			return true;
		}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from){
		return new FluidTankInfo[]{tank.getInfo()};
	}

	public void updateAmalgamTankCapacity(){
		// loop through the casting slots
		for(int i=0; i<9; i++){
			this.container.getSlot(i);
			// TODO figure out the best way to update the tank size when the castingState changes for slots
		}
	}
}
