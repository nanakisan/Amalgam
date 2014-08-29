package amalgam.common.tile;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import amalgam.common.Config;
import amalgam.common.casting.ICastItem;
import amalgam.common.container.SlotCasting;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.AmalgamTank;
import amalgam.common.item.ItemAmalgamBlob;

public class TileCastingTable extends AbstractTileAmalgamContainer {

    private ItemStack[]         castingItems = new ItemStack[10];
    private int[]               castStates   = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private static final String ITEMS_KEY    = "ItemStacks";
    private static final String CAST_KEY     = "CastStates";
    private static final String SLOT_KEY     = "Slot";

    public TileCastingTable() {
        super();
        tank = new AmalgamTank(0);
    }

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
        int fillAmount = super.fill(from, resource, doFill);
        ItemStack s = castingItems[9];

        if (fillAmount > 0 && doFill && s != null && s.hasTagCompound()) {
            updateCastResult(s);
        }

        return fillAmount;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        FluidStack returnStack = super.drain(from, resource.amount, doDrain);
        ItemStack s = castingItems[9];

        if (returnStack != null && s != null && s.hasTagCompound()) {
            updateCastResult(s);
        }

        return returnStack;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        FluidStack returnStack = super.drain(from, maxDrain, doDrain);
        ItemStack s = castingItems[9];

        if (returnStack != null && s != null && s.hasTagCompound()) {
            updateCastResult(s);
        }

        return returnStack;
    }

    public void updateCastResult(ItemStack result) {
        ItemStack temp;
        Set<ItemStack> items = new HashSet<ItemStack>();

        for (int i = 0; i < 9; i++) {
            temp = castingItems[i];
            if (temp != null) {
                items.add(temp);
            }
        }

        ItemStack[] materials = items.toArray(new ItemStack[items.size()]);
        castingItems[9] = ((ICastItem) result.getItem()).generateStackWithProperties(getAmalgamPropertyList(), materials, result.stackSize);
    }

    public void updateAmalgamTankCapacity() {
        int newCapacity = 0;

        /* Calculate new tank capacity based on the casting states of the slots on the table */
        for (int i = 0; i < 9; i++) {
            switch(castStates[i]){
                case SlotCasting.NUGGET_STATE:
                    newCapacity += Config.BASE_AMOUNT;
                    break;
                case SlotCasting.INGOT_STATE:
                    newCapacity += Config.INGOT_AMOUNT;
                    break;
                case SlotCasting.BLOCK_STATE:
                    newCapacity += Config.BLOCK_AMOUNT;
                    break;
            }
        }

        /* Set the tank capacity to the new capacity. If the capacity decreased we may get the amalgam overflow */
        AmalgamStack extraAmalgam = tank.setCapacity(newCapacity);

        if (extraAmalgam != null) {
            if (extraAmalgam.amount == 0) {
                return;
            }

            if (!this.worldObj.isRemote) {
                /* If there is amalgam overflow we spawn it in the world as solidified amalgam */
                ItemStack droppedBlob = new ItemStack(Config.amalgamBlob, 1);
                ((ItemAmalgamBlob) Config.amalgamBlob).setProperties(droppedBlob, extraAmalgam.getProperties());
                ((ItemAmalgamBlob) Config.amalgamBlob).setVolume(droppedBlob, extraAmalgam.amount);
                EntityItem amalgEntity = new EntityItem(this.worldObj, xCoord, yCoord, zCoord, droppedBlob);
                this.worldObj.spawnEntityInWorld(amalgEntity);
            }
        }
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
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, this.blockMetadata, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.func_148857_g();
        this.castStates = tag.getIntArray(CAST_KEY);
        this.tank.readFromNBT(tag);
        NBTTagList nbttaglist = tag.getTagList(ITEMS_KEY, 10);
        this.castingItems = new ItemStack[10];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte(SLOT_KEY);

            if (b0 >= 0 && b0 < this.castingItems.length) {
                this.castingItems[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    public ItemStack getStackInSlot(int slotNum) {
        return castingItems[slotNum];
    }

    public void setStackInSlot(int slotNum, ItemStack stack) {
        castingItems[slotNum] = stack;
    }

    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    public void setTankFluid(AmalgamStack fluid) {
        tank.setFluid(fluid);
    }

    public boolean decrStackSize(int slot, int decNum) {
        if (castStates[slot] != 0) {
            return true;
        }

        if (castingItems[slot] == null) {
            return true;
        } else {
            ItemStack itemstack;

            if (castingItems[slot].stackSize <= decNum) {
                itemstack = castingItems[slot];
                this.setStackInSlot(slot, null);
                return false;
            } else {
                itemstack = castingItems[slot].splitStack(decNum);

                if (castingItems[slot].stackSize == 0) {
                    this.setStackInSlot(slot, itemstack);
                }

                return true;
            }
        }
    }

}
