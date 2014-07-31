package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import amalgam.common.properties.PropertyList;

public class InventoryCasting implements IInventory, ISidedInventory {

    /** properties of the amalgam used to cast */
    private PropertyList propertyList;
    /** List of the stacks in the crafting matrix. */
    private ItemStack[]  stackList;
    /** the width of the crafting inventory */
    private int          inventoryWidth;
    /** Class containing the callbacks for the events on_GUIClosed and on_CraftMaxtrixChanged. */
    private Container    eventHandler;
    /** an array holding the casting state of each slot */
    private int          castState[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    public InventoryCasting(Container container, int rows, int cols) {
        this.stackList = new ItemStack[rows * cols];
        this.eventHandler = container;
        this.inventoryWidth = rows;
    }

    public void setPropertyList(PropertyList pList) {
        propertyList = pList;
    }

    public PropertyList getPropertyList() {
        return propertyList;
    }

    // ///////////////////
    // ISidedInventory //
    // ///////////////////

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        int slots[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        return slots;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int meta) {
        if (slot >= 9) {
            return false;
        }
        return castState[slot] == 0;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int meta) {
        if (slot == 9) {
            return true;
        }

        return false;
    }

    // //////////////
    // IInventory //
    // //////////////

    @Override
    public int getSizeInventory() {
        int unusableSlots = 0;
        for (int i = 0; i < 9; i++) {
            if (castState[i] != 0) {
                unusableSlots++;
            }
        }
        return 9 - unusableSlots;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (castState[slot] == 1) {
            return null;
        }

        return stackList[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int decNum) {
        if (castState[slot] != 0) {
            return null;
        }

        ItemStack stack = stackList[slot];
        return stack.splitStack(decNum);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (castState[slot] != 0) {
            return;
        }
        stackList[slot] = stack;
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
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        // TODO look at other implementations
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

        if (castState[slot] == 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns the itemstack in the slot specified (Top left is 0, 0). Args: row, column
     */
    public ItemStack getStackInRowAndColumn(int row, int col) {
        if (row >= 0 && col < this.inventoryWidth) {
            int slotNum = row + col * this.inventoryWidth;
            return this.getStackInSlot(slotNum);
        } else {
            return null;
        }
    }

    @Override
    public void markDirty() {
    }

}
