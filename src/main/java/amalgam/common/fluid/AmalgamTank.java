package amalgam.common.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import amalgam.common.Amalgam;

public class AmalgamTank implements IFluidTank {

	protected AmalgamStack fluid;
    protected int capacity;
    protected TileEntity tile;
	
    public AmalgamTank(int cap){
    	super();
    	capacity = cap;
    }
    
    public AmalgamTank readFromNBT(NBTTagCompound nbt){
    	if (!nbt.hasKey("Empty")){
    		this.fluid = AmalgamStack.loadAmalgamStackFromNBT(nbt);
    	}
    	this.capacity = nbt.getInteger("cap");
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        if (fluid != null){
            fluid.writeToNBT(nbt);
        }
        else{
            nbt.setString("Empty", "");
        }
        nbt.setInteger("cap", this.capacity);
        return nbt;
    }
    
	@Override
	public FluidStack getFluid() {
		return fluid;
	}

	@Override
	public int getFluidAmount() {
		if(fluid==null) return 0;
		return fluid.amount;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}
	
	public AmalgamStack setCapacity(int newCapacity) {
		this.capacity = newCapacity;
		// FIXME return the amalgamStack that is the amalgam which no longer fits in the tank because its capacity has decreased!
		return null;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		
		if(resource == null){
			return 0;
		}
		if(resource.fluidID != Amalgam.fluidAmalgam.getID()){
			return 0;
		}
		if(doFill){
			if(fluid == null){
				fluid = new AmalgamStack((AmalgamStack)resource, Math.min(capacity, resource.amount));
				if (tile != null){
	                FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, this, fluid.amount));
	            }
				//Amalgam.log.info(fluid.getProperties().toString());
				return fluid.amount;
			}
			
			int filled = capacity - fluid.amount;
	        if (resource.amount < filled){
	        	fluid = AmalgamStack.combine(fluid, (AmalgamStack)resource);
	            filled = resource.amount;
	        }else{
	        	AmalgamStack temp = new AmalgamStack((AmalgamStack)resource, filled);
	        	fluid = AmalgamStack.combine(fluid, temp);
	        }
	        if (tile != null){
	            FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, this, filled));
	        }
	      //Amalgam.log.info(fluid.getProperties().toString());
	        return filled;
		}else{
			if(fluid == null){
				return Math.min(capacity, resource.amount);
			}
			return Math.min(capacity - fluid.amount, resource.amount);
		}
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (fluid == null){
			return null;
	    }

	    int drained = maxDrain;
	    // we drain all the fluid if we get the amount -1 (this is used in casting table, there is probably a better way to do this)
	    if (fluid.amount < drained || drained == -1){
	    	drained = fluid.amount;
	    }

	    AmalgamStack stack = new AmalgamStack(fluid, drained);
	    if (doDrain){
	    	fluid.amount -= drained;
	        if (fluid.amount <= 0){
	        	fluid = null;
	        }
	        if (tile != null){
	        	FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(fluid, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, this, drained));
	        }
	    }
	    return stack;
	}
	
	public String toString(){
		if(fluid == null){
			return "Empty!";
		}
		return "Capacity: " + this.getCapacity() + " Amount: " + this.getFluidAmount() + " Space Left: " + (this.getCapacity() - this.getFluidAmount()) + " Properties: " + fluid.getProperties().toString();
	}

	public void setFluid(AmalgamStack fluid2) {
		this.fluid = fluid2;
	}

}
