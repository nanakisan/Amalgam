package amalgam.common.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import amalgam.common.Config;
import amalgam.common.casting.CastingManager;
import amalgam.common.casting.ICastingRecipe;
import amalgam.common.container.ICastingHandler;
import amalgam.common.container.InventoryCasting;
import amalgam.common.container.InventoryCastingResult;
import amalgam.common.container.SlotCasting;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.AmalgamTank;
import amalgam.common.item.ItemAmalgamBlob;
import amalgam.common.properties.PropertyList;

public class TileCastingTable extends TileAmalgamContainer implements ISidedInventory, ICastingHandler {

    public InventoryCasting       castingInventory;
    public InventoryCastingResult castingResult;

    private static final int      RESULT_SLOT = 9;
    private static final String   ITEMS_KEY   = "ItemStacks";
    private static final String   CAST_KEY    = "CastStates";
    private static final String   STATE_KEY   = "State";
    private static final String   SLOT_KEY    = "Slot";

    public TileCastingTable() {
        super();
        tank = new AmalgamTank(0);
        castingResult = new InventoryCastingResult();
        castingInventory = new InventoryCasting(this, 3, 3);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagList nbttaglist = tag.getTagList(ITEMS_KEY, 10);
        NBTTagList castList = tag.getTagList(CAST_KEY, 10);
        this.castingInventory = new InventoryCasting(this, 3, 3);

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte slot = nbttagcompound1.getByte(SLOT_KEY);
            if (slot >= 0 && slot < this.castingInventory.getSizeInventory()) {
                this.castingInventory.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(nbttagcompound1));
            }
        }

        for (int i = 0; i < castList.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = castList.getCompoundTagAt(i);
            byte slot = nbttagcompound1.getByte(SLOT_KEY);
            byte state = nbttagcompound1.getByte(STATE_KEY);
            if (slot >= 0 && slot < this.castingInventory.getSizeInventory()) {
                this.castingInventory.setCastState(slot, state);
            }
        }

        tank.readFromNBT(tag);

        updateAmalgamDistribution();
        onCastMatrixChanged(castingInventory);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tank.writeToNBT(tag);

        NBTTagList nbttaglist = new NBTTagList();
        NBTTagList castList = new NBTTagList();
        for (int i = 0; i < this.castingInventory.getSizeInventory(); ++i) {
            ItemStack item = this.castingInventory.getStackInSlot(i);
            if (item != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte(SLOT_KEY, (byte) i);
                nbttagcompound1.setByte(CAST_KEY, (byte) this.castingInventory.getCastState(i));
                item.writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
            if (this.castingInventory.getCastState(i) != SlotCasting.EMPTY_STATE) {
                NBTTagCompound castCompound = new NBTTagCompound();
                castCompound.setByte(SLOT_KEY, (byte) i);
                castCompound.setByte(STATE_KEY, (byte) this.castingInventory.getCastState(i));
                castList.appendTag(castCompound);
            }
        }

        tag.setTag(ITEMS_KEY, nbttaglist);
        tag.setTag(CAST_KEY, castList);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        int fillAmount = super.fill(from, resource, doFill);
        // ItemStack s = castingResult.getStackInSlot(0);

        if (fillAmount > 0 && doFill) {
            onCastMatrixChanged(castingInventory);
        }

        this.updateAmalgamDistribution();
        return fillAmount;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        FluidStack returnStack = super.drain(from, resource.amount, doDrain);

        if (returnStack != null && doDrain) {
            onCastMatrixChanged(castingInventory);
        }
        this.updateAmalgamDistribution();
        return returnStack;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        FluidStack returnStack = super.drain(from, maxDrain, doDrain);

        if (returnStack != null && doDrain) {
            onCastMatrixChanged(castingInventory);
        }
        this.updateAmalgamDistribution();
        return returnStack;
    }

    public void updateTankCapacity(InventoryCasting inv) {
        int newCapacity = 0;

        /* Calculate new tank capacity based on the casting states of the slots on the table */
        for (int i = 0; i < 9; i++) {
            switch (inv.getCastState(i)) {
                case SlotCasting.NUGGET_STATE:
                    newCapacity += Config.BASE_AMOUNT;
                    break;
                case SlotCasting.INGOT_STATE:
                    newCapacity += Config.INGOT_AMOUNT;
                    break;
                case SlotCasting.BLOCK_STATE:
                    newCapacity += Config.BLOCK_AMOUNT;
                    break;
                default:
            }
        }

        /* Set the tank capacity to the new capacity. If the capacity decreased we may get the amalgam overflow */
        AmalgamStack extraAmalgam = tank.setCapacity(newCapacity);

        if (extraAmalgam != null) {
            if (extraAmalgam.amount == 0) {
                return;
            }

            /* make sure we are on the server */
            if (!this.worldObj.isRemote) {
                /* If there is amalgam overflow we spawn it in the world as solidified amalgam */
                ItemStack droppedBlob = new ItemStack(Config.amalgamBlob, 1);
                ((ItemAmalgamBlob) Config.amalgamBlob).setProperties(droppedBlob, extraAmalgam.getProperties());
                ((ItemAmalgamBlob) Config.amalgamBlob).setVolume(droppedBlob, extraAmalgam.amount);
                EntityItem amalgEntity = new EntityItem(this.worldObj, xCoord, yCoord, zCoord, droppedBlob);
                this.worldObj.spawnEntityInWorld(amalgEntity);
            }
        }

        this.updateAmalgamDistribution();
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);

        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, this.blockMetadata, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.func_148857_g();
        this.readFromNBT(tag);
    }

    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    public void setTankFluid(AmalgamStack fluid) {
        tank.setFluid(fluid);
        this.updateAmalgamDistribution();
    }

    @Override
    public int getSizeInventory() {
        return castingInventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (slot == RESULT_SLOT) {
            return castingResult.getStackInSlotOnClosing(0);
        }
        return castingInventory.getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot == RESULT_SLOT) {
            castingResult.setInventorySlotContents(0, stack);
        }
        castingInventory.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName() {
        return "container.castingtable";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return castingInventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return castingInventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == RESULT_SLOT) {
            return castingResult.isItemValidForSlot(0, stack);
        }

        return castingInventory.isItemValidForSlot(slot, stack);
    }

    @Override
    public ItemStack decrStackSize(int slot, int decNum) {
        if (slot == RESULT_SLOT) {
            return castingResult.decrStackSize(0, decNum);
        }

        return castingInventory.decrStackSize(slot, decNum);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int meta) {
        if (slot == RESULT_SLOT) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int meta) {
        if (slot == RESULT_SLOT && getEmptySpace() == 0) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == RESULT_SLOT) {
            return castingResult.getStackInSlot(0);
        }

        return castingInventory.getStackInSlot(slot);
    }

    @Override
    public boolean isCastComplete() {
        return tank.getCapacity() == tank.getFluidAmount();
    }

    @Override
    public void onCastMatrixChanged(InventoryCasting inv) {
        ICastingRecipe recipe = CastingManager.findMatchingRecipe(inv, worldObj);

        if (recipe == null) {
            castingResult.setInventorySlotContents(0, null);
            return;
        }

        PropertyList pList = getAmalgamPropertyList();
        if (getFluidAmount() == 0) {
            pList = null;
        }

        castingResult.setInventorySlotContents(0, recipe.getCastingResult(inv, pList));
    }

    @Override
    public void onCastPickup() {
        this.tank.setFluid(null);

        onCastMatrixChanged(this.castingInventory);
        updateAmalgamDistribution();
    }

    public void updateAmalgamDistribution() {
        float fillPercentage = (float) this.getFluidAmount() / (float) this.getTankCapacity();
        // Config.LOG.info("fill percentage: " + fillPercentage);
        for (int i = 0; i < this.castingInventory.getSizeInventory(); i++) {
            if (this.castingInventory.getCastState(i) != SlotCasting.EMPTY_STATE) {
                this.castingInventory.setFillAmount(i, fillPercentage);
            }
        }
    }
}
