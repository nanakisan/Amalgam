package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryCastResult implements IInventory {

    private ItemStack[] stackResult = new ItemStack[1];

    public int getSizeInventory() {
        return 1;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int slotNum) {
        return this.stackResult[0];
    }

    /**
     * Returns the name of the inventory
     */
    public String getInventoryName() {
        return "Result";
    }

    /**
     * Returns if the inventory is named
     */
    public boolean hasCustomInventoryName() {
        return false;
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int slotNum, int amount) {
        if (this.stackResult[0] == null) {
            return null;
        } else {

            ItemStack itemstack = this.stackResult[0];
            this.stackResult[0] = null;
            return itemstack;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int slotNum) {
        if (this.stackResult[0] == null) {
            return null;
        } else {

            ItemStack itemstack = this.stackResult[0];
            this.stackResult[0] = null;
            return itemstack;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int slotNum, ItemStack stack) {
        this.stackResult[0] = stack;
    }

    /**
     * Returns the maximum stack size for a inventory slot.
     */
    public int getInventoryStackLimit() {
        return 64;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty() {
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    public void openInventory() {
    }

    public void closeInventory() {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int slotNum, ItemStack stack) {
        return true;
    }
}
