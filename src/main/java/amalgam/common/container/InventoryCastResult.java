package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryCastResult implements IInventory {

    private ItemStack[] stackResult = new ItemStack[1];

    public int getSizeInventory() {
        return 1;
    }

    public ItemStack getStackInSlot(int slotNum) {
        return this.stackResult[0];
    }

    public String getInventoryName() {
        return "Result";
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public ItemStack decrStackSize(int slotNum, int amount) {
        if (this.stackResult[0] == null) {
            return null;
        } else {

            ItemStack itemstack = this.stackResult[0];
            this.stackResult[0] = null;
            return itemstack;
        }
    }

    public ItemStack getStackInSlotOnClosing(int slotNum) {
        if (this.stackResult[0] == null) {
            return null;
        } else {

            ItemStack itemstack = this.stackResult[0];
            this.stackResult[0] = null;
            return itemstack;
        }
    }

    public void setInventorySlotContents(int slotNum, ItemStack stack) {
        this.stackResult[0] = stack;
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public void markDirty() {
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    public void openInventory() {
    }

    public void closeInventory() {
    }

    public boolean isItemValidForSlot(int slotNum, ItemStack stack) {
        return true;
    }
}
