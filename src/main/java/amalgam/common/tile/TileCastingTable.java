package amalgam.common.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import amalgam.common.Config;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.AmalgamTank;
import amalgam.common.item.ItemAmalgamBlob;
import amalgam.common.network.PacketHandler;
import amalgam.common.properties.PropertyList;

public class TileCastingTable extends TileEntity implements IFluidHandler {

    // FIXME custom rendering based on the casting state and amalgam on the table. Show empty and filled casting slots.

    private ItemStack[]         castingItems = new ItemStack[10];
    private int[]               castStates   = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private final AmalgamTank   tank         = new AmalgamTank(0);

    private static final String ITEMS_KEY    = "ItemStacks";
    private static final String CAST_KEY     = "CastStates";
    private static final String SLOT_KEY     = "Slot";

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagList nbttaglist = tag.getTagList(ITEMS_KEY, 10);
        this.castingItems = new ItemStack[10];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte(SLOT_KEY);

            if (b0 >= 0 && b0 < this.castingItems.length) {
                this.castingItems[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        this.castStates = tag.getIntArray(CAST_KEY);
        tank.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tank.writeToNBT(tag);
        tag.setIntArray(CAST_KEY, this.castStates);

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.castingItems.length; ++i) {
            if (this.castingItems[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte(SLOT_KEY, (byte) i);
                this.castingItems[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        tag.setTag(ITEMS_KEY, nbttaglist);
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
        boolean canDrain = false;

        if (fluid.getID() == Config.fluidAmalgam.getID()) {
            canDrain = true;
        }
        return canDrain;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tank.getInfo() };
    }

    public void updateAmalgamTankCapacity() {
        int newCapacity = 0;

        for (int i = 0; i < 9; i++) {
            newCapacity += castStates[i] * Config.INGOT_AMOUNT;
        }

        AmalgamStack extraAmalgam = tank.setCapacity(newCapacity);

        if (extraAmalgam != null) {
            if (extraAmalgam.amount == 0) {
                return;
            }

            if (!this.worldObj.isRemote) {

                ItemStack droppedBlob = new ItemStack(Config.amalgamBlob, 1);

                ((ItemAmalgamBlob) Config.amalgamBlob).setProperties(droppedBlob, extraAmalgam.getProperties());
                ((ItemAmalgamBlob) Config.amalgamBlob).setVolume(droppedBlob, extraAmalgam.amount);

                EntityItem amalgEntity = new EntityItem(this.worldObj, xCoord, yCoord, zCoord, droppedBlob);
                this.worldObj.spawnEntityInWorld(amalgEntity);
            }
        }
    }

    public boolean tankIsFull() {
        return tank.getFluidAmount() == tank.getCapacity();
    }

    public int getCastState(int i) {
        return castStates[i];
    }

    public void setCastState(int index, int value) {
        this.castStates[index] = value;
        this.updateAmalgamTankCapacity();
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setIntArray(CAST_KEY, this.castStates);
        tank.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, this.blockMetadata, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.func_148857_g();
        this.castStates = tag.getIntArray(CAST_KEY);
        this.tank.readFromNBT(tag);
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

    public ItemStack getStackInSlot(int slotNum) {
        return castingItems[slotNum];
    }

    public void setStackInSlot(int slotNum, ItemStack stack) {
        castingItems[slotNum] = stack;
    }

    public int getTankAmount() {
        return tank.getFluidAmount();
    }

    public void setTankFluid(AmalgamStack fluid) {
        tank.setFluid(fluid);
    }

    public PropertyList getAmalgamPropertyList() {
        return ((AmalgamStack) tank.getFluid()).getProperties();
    }
}
