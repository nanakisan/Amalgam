package amalgam.common.tile;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
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

    // TODO custom rendering for the crucible. Render similar to the cauldron, but with amalgam instead of water.

    protected AmalgamTank       tank                 = new AmalgamTank(Amalgam.INGOT_AMOUNT * 15);

    private static Set<Block>   heatSources          = Sets.newHashSet(new Block[] { Blocks.fire, Blocks.lava });
    private boolean             hasHeat              = false;
    private static final String HEAT_TAG             = "heat";

    private int                 ticksSinceLastUpdate = 0;
    private static final int    UPDATE_PERIOD        = 100;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        tank.readFromNBT(tag);
        this.hasHeat = tag.getBoolean(HEAT_TAG);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tank.writeToNBT(tag);
        tag.setBoolean(HEAT_TAG, this.hasHeat);
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
        return tank.getCapacity() - tank.getFluidAmount();
    }

    public void emptyTank() {
        if (!this.worldObj.isRemote) {
            int amount = tank.getFluidAmount();
            PropertyList p = ((AmalgamStack) tank.getFluid()).getProperties();
            while (amount > 0) {
                int dropAmount = Math.min(amount, Amalgam.INGOT_AMOUNT);
                amount -= dropAmount;
                ItemStack droppedBlob = new ItemStack(Amalgam.amalgamBlob, 1);

                ((ItemAmalgamBlob) Amalgam.amalgamBlob).setProperties(droppedBlob, p);
                ((ItemAmalgamBlob) Amalgam.amalgamBlob).setVolume(droppedBlob, dropAmount);

                EntityItem amalgEntity = new EntityItem(this.worldObj, xCoord, yCoord, zCoord, droppedBlob);
                this.worldObj.spawnEntityInWorld(amalgEntity);
            }
        }
    }

    @Override
    public void updateEntity() {
        ticksSinceLastUpdate++;

        if (ticksSinceLastUpdate > UPDATE_PERIOD) {
            ticksSinceLastUpdate = 0;

            Block test = this.worldObj.getBlock(this.xCoord, this.yCoord - 1, this.zCoord);
            if (TileStoneCrucible.heatSources.contains(test)) {
                this.hasHeat = true;
            } else {
                this.hasHeat = false;
            }
        }
    }

    public static void addHeatSource(Block source) {
        TileStoneCrucible.heatSources.add(source);
    }

    public boolean isHot() {
        return this.hasHeat;
    }

    public String toString() {
        return tank.toString();
    }
}
