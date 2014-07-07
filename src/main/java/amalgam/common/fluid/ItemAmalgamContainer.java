package amalgam.common.fluid;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import amalgam.common.Amalgam;
import amalgam.common.properties.PropertyList;

public class ItemAmalgamContainer extends Item implements IFluidContainerItem{

	protected int capacity;
	
	public ItemAmalgamContainer(){
        super();
    }

    public ItemAmalgamContainer(int capacity)
    {
        super();
        this.capacity = capacity;
    }
	
	@Override
	public AmalgamStack getFluid(ItemStack container){
		if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Amalgam")){
            return null;
        }
		// read the Amalgam tag compound
		NBTTagCompound containerNBT = container.stackTagCompound.getCompoundTag("Amalgam");
		// get amount and PropertyList
		int amount = containerNBT.getInteger("Amount");
		PropertyList pList = new PropertyList();
		pList.readFromNBT(containerNBT.getCompoundTag("Tag"));
        return new AmalgamStack(amount, pList);
	}

	@Override
	public int getCapacity(ItemStack container){
		return capacity;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (resource == null){
            return 0;
        }
        
        if(resource.fluidID !=Amalgam.fluidAmalgam.getID()){
        	return 0;
        }

        if (!doFill){
            if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Amalgam")){
                return Math.min(capacity, resource.amount);
            }

    		NBTTagCompound containerNBT = container.stackTagCompound.getCompoundTag("Amalgam");
    		int amount = containerNBT.getInteger("Amount");
            return Math.min(capacity - amount, resource.amount);
        }

        if (container.stackTagCompound == null){
            container.stackTagCompound = new NBTTagCompound();
        }

        // if container is empty
        if (!container.stackTagCompound.hasKey("Amalgam"))
        {
            NBTTagCompound amalgamTag = resource.writeToNBT(new NBTTagCompound());

            if (capacity < resource.amount)
            {
                amalgamTag.setInteger("Amount", capacity);
                container.stackTagCompound.setTag("Amalgam", amalgamTag);
                return capacity;
            }

            container.stackTagCompound.setTag("Amalgam", amalgamTag);
            return resource.amount;
        }

        NBTTagCompound amalgamTag = container.stackTagCompound.getCompoundTag("Amalgam");
        AmalgamStack stack = AmalgamStack.loadAmalgamStackFromNBT(amalgamTag);
        
        if (!stack.isFluidEqual(resource)){
            return 0;
        }

        // if container isn't empty
        int filled = capacity - stack.amount; // this is how much space is left in the container
        if (resource.amount < filled){ // we have enough space to take all the resource
        	stack = AmalgamStack.combine(stack, (AmalgamStack)resource);
            filled = resource.amount;
        }
        else{ // we don't have enough space to take all the resource, so we create a temporary stack of the resource of the amount we can fill.
        	AmalgamStack temp = new AmalgamStack((AmalgamStack)resource, filled);
        	stack = AmalgamStack.combine(stack, temp);
        }

        // write our new amalgam to the NBT tag for the container
        container.stackTagCompound.setTag("Amalgam", stack.writeToNBT(amalgamTag));
        return filled;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Amalgam")){
            return null;
        }

        AmalgamStack stack = AmalgamStack.loadAmalgamStackFromNBT(container.stackTagCompound.getCompoundTag("Amalgam"));
        stack.amount = Math.min(stack.amount, maxDrain);
        
        if (doDrain){
            if (maxDrain >= capacity){
                container.stackTagCompound.removeTag("Amalgam");
                if (container.stackTagCompound.hasNoTags()){
                    container.stackTagCompound = null;
                }
                return stack;
            }

            NBTTagCompound amalgamTag = container.stackTagCompound.getCompoundTag("Amalgam");
            amalgamTag.setInteger("Amount", amalgamTag.getInteger("Amount") - maxDrain);
            container.stackTagCompound.setTag("Amalgam", amalgamTag);
        }
        return stack;
    }
}

