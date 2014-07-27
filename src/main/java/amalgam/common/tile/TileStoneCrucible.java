package amalgam.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import amalgam.common.Amalgam;
import amalgam.common.fluid.AmalgamTank;

public class TileStoneCrucible extends TileEntity implements IFluidHandler{

	protected AmalgamTank tank = new AmalgamTank(Amalgam.BASEAMOUNT * 100);
	
	@Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        tank.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tank.writeToNBT(tag);
    }
    
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill){
		int r = tank.fill(resource, doFill);
		return r;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain){
		if(resource == null){
			return null;
		}
		FluidStack r = tank.drain(resource.amount, doDrain);
		return r;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain){
		FluidStack r = tank.drain(maxDrain, doDrain);
		return r;
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
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from){
		return new FluidTankInfo[]{tank.getInfo()};
	}

	public int getEmptySpace(){
		//Amalgam.log.info("Calculating empty space... capacity: " + tank.getCapacity() + " fluid amount: " + tank.getFluidAmount());
		return tank.getCapacity() - tank.getFluidAmount();
	}
	
	public String toString(){
		return tank.toString();
	}
}
