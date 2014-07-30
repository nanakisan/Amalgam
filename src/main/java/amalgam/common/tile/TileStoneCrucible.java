package amalgam.common.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import amalgam.common.Amalgam;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.AmalgamTank;
import amalgam.common.item.ItemAmalgamBlob;
import amalgam.common.properties.PropertyList;

public class TileStoneCrucible extends TileEntity implements IFluidHandler {

    protected AmalgamTank tank = new AmalgamTank(Amalgam.BASEAMOUNT * 100);

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        tank.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tank.writeToNBT(tag);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null) {
            return null;
        }
        return tank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (fluid.getID() == Amalgam.fluidAmalgam.getID()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tank.getInfo() };
    }

    public int getEmptySpace() {
        // Amalgam.log.info("Calculating empty space... capacity: " +
        // tank.getCapacity() + " fluid amount: " + tank.getFluidAmount());
        return tank.getCapacity() - tank.getFluidAmount();
    }

    public void emptyTank() {
        if (!this.worldObj.isRemote) {
            int amount = tank.getFluidAmount();
            PropertyList p = ((AmalgamStack) tank.getFluid()).getProperties();
            while (amount > 0) {
                int dropAmount = Math.min(amount, Amalgam.INGOTAMOUNT);
                amount -= dropAmount;
                ItemStack droppedBlob = new ItemStack(Amalgam.amalgamBlob, 1);

                ((ItemAmalgamBlob) Amalgam.amalgamBlob).setProperties(droppedBlob, p);
                ((ItemAmalgamBlob) Amalgam.amalgamBlob).setVolume(droppedBlob, dropAmount);

                Amalgam.LOG.info("attempting to spawn an amalgam blob entity");
                EntityItem amalgEntity = new EntityItem(this.worldObj, xCoord, yCoord, zCoord, droppedBlob);
                this.worldObj.spawnEntityInWorld(amalgEntity);
            }
        }
    }

    public String toString() {
        return tank.toString();
    }
}
