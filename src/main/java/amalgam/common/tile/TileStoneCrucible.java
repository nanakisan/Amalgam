package amalgam.common.tile;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import amalgam.common.Config;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.AmalgamTank;
import amalgam.common.item.ItemAmalgamBlob;
import amalgam.common.properties.PropertyList;

import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileStoneCrucible extends TileEntity implements IFluidHandler {

    protected AmalgamTank       tank          = new AmalgamTank(Config.INGOT_AMOUNT * 15);

    private static Set<Block>   heatSources   = Sets.newHashSet(new Block[] { Blocks.fire, Blocks.lava });
    private static final String HEAT_TAG      = "heat";
    private boolean             hasHeat;

    private static final int    UPDATE_PERIOD = 50;
    private int                 ticksSinceLastUpdate;

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
        if (fluid.getID() == Config.fluidAmalgam.getID()) {
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
                int dropAmount = Math.min(amount, Config.INGOT_AMOUNT);
                amount -= dropAmount;
                ItemStack droppedBlob = new ItemStack(Config.amalgamBlob, 1);

                ((ItemAmalgamBlob) Config.amalgamBlob).setProperties(droppedBlob, p);
                ((ItemAmalgamBlob) Config.amalgamBlob).setVolume(droppedBlob, dropAmount);

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

    public float getFluidHeight() {
        return 0.3F + 0.7F * ((float) this.tank.getFluidAmount() / (float) this.tank.getCapacity());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tank.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, this.blockMetadata, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.func_148857_g();
        this.tank.readFromNBT(tag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);
    }

    public PropertyList getAmalgamProperties() {
        return ((AmalgamStack) tank.getFluid()).getProperties();
    }

    public int getFluidVolume() {
        return this.tank.getFluidAmount();
    }

    public int getTankCapacity() {
        return this.tank.getCapacity();
    }

}
