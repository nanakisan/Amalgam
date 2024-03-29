package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryCasting implements IInventory {

    private ItemStack[]    stackList;
    private int[]          castState;
    private float[]        amalgamFillAmount;
    private final int      inventoryWidth;
    public ICastingHandler handler;

    public InventoryCasting(ICastingHandler handler, int rows, int cols) {
        this.inventoryWidth = rows;
        this.stackList = new ItemStack[rows * cols];
        this.castState = new int[rows * cols];
        this.amalgamFillAmount = new float[rows * cols];
        this.handler = handler;
    }

    @Override
    public int getSizeInventory() {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (castState[slot] == SlotCasting.EMPTY_STATE) {
            return stackList[slot];
        }

        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int decNum) {
        if (castState[slot] != SlotCasting.EMPTY_STATE) {
            return null;
        }

        if (stackList[slot] == null) {
            return null;
        } else {
            ItemStack itemstack;

            if (stackList[slot].stackSize <= decNum) {
                itemstack = stackList[slot];
                this.setInventorySlotContents(slot, null);
                handler.onCastMatrixChanged(this);

                return itemstack;
            } else {
                itemstack = stackList[slot].splitStack(decNum);

                if (stackList[slot].stackSize == 0) {
                    this.setInventorySlotContents(slot, itemstack);

                }

                handler.onCastMatrixChanged(this);

                return itemstack;
            }
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (castState[slot] != SlotCasting.EMPTY_STATE) {
            return;
        }

        stackList[slot] = stack;
        handler.onCastMatrixChanged(this);
    }

    @Override
    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (castState[slot] == SlotCasting.EMPTY_STATE) {
            return true;
        }

        return false;
    }

    public ItemStack getStackInRowAndColumn(int row, int col) {
        if (row >= 0 && col < this.inventoryWidth) {
            int slotNum = row + col * this.inventoryWidth;

            return stackList[slotNum];
        } else {
            return null;
        }
    }

    @Override
    public void markDirty() {
    }

    public int getCastState(int slot) {
        return castState[slot];
    }

    public void setCastState(int slot, int state) {
        castState[slot] = state;
        handler.updateTankCapacity(this);
        handler.onCastMatrixChanged(this);
    }

    public float getFillAmount(int slot) {
        return amalgamFillAmount[slot];
    }

    public void setFillAmount(int slot, float amount) {
        amalgamFillAmount[slot] = amount;
    }
}
