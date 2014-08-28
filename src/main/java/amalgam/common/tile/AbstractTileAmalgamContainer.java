package amalgam.common.tile;

import amalgam.common.Config;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.AmalgamTank;
import amalgam.common.item.ItemAmalgamBlob;
import amalgam.common.properties.PropertyList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public abstract class AbstractTileAmalgamContainer extends TileEntity implements IFluidHandler {

    protected AmalgamTank tank;

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tank.getInfo() };
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
        if (fluid.getID() == Config.fluidAmalgam.getID()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    public void emptyTank() {
        if (!this.worldObj.isRemote) {
            int amount = tank.getFluidAmount();
            PropertyList pList = ((AmalgamStack) tank.getFluid()).getProperties();

            while (amount > 0) {
                int dropAmount = Math.min(amount, Config.INGOT_AMOUNT);
                amount -= dropAmount;
                ItemStack droppedBlob = new ItemStack(Config.amalgamBlob, 1);

                ((ItemAmalgamBlob) Config.amalgamBlob).setProperties(droppedBlob, pList);
                ((ItemAmalgamBlob) Config.amalgamBlob).setVolume(droppedBlob, dropAmount);

                EntityItem amalgEntity = new EntityItem(this.worldObj, xCoord, yCoord, zCoord, droppedBlob);
                this.worldObj.spawnEntityInWorld(amalgEntity);
            }
        }
    }

    public PropertyList getAmalgamPropertyList() {
        return ((AmalgamStack) tank.getFluid()).getProperties();
    }

    public int getEmptySpace() {
        return tank.getCapacity() - tank.getFluidAmount();
    }

    public int getFluidVolume() {
        return tank.getFluidAmount();
    }

    public int getTankCapacity() {
        return tank.getCapacity();
    }

}
