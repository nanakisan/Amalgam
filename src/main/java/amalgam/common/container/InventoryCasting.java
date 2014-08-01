package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class InventoryCasting implements IInventory, ISidedInventory {

    private ItemStack[]      stackList = new ItemStack[9];
    private int              inventoryWidth;
    private ContainerCasting table;

    public InventoryCasting(ContainerCasting container, int rows, int cols) {
        this.table = container;
        this.inventoryWidth = rows;
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

        return ((SlotCasting) table.getSlot(slot)).getCastState() == 0;
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
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (((SlotCasting) table.getSlot(slot)).getCastState() == 1) {
            // Amalgam.LOG.info("found some amalgam");
            return null;
        }

        return stackList[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int decNum) {
        if (((SlotCasting) table.getSlot(slot)).getCastState() != 0) {
            return null;
        }

        if (stackList[slot] != null) {
            ItemStack itemstack;

            if (stackList[slot].stackSize <= decNum) {
                itemstack = stackList[slot];
                this.setInventorySlotContents(slot, null);
                return itemstack;
            } else {
                itemstack = stackList[slot].splitStack(decNum);

                if (stackList[slot].stackSize == 0) {
                    this.setInventorySlotContents(slot, itemstack);
                }

                return itemstack;
            }
        } else {
            return null;
        }

    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (((SlotCasting) table.getSlot(slot)).getCastState() != 0) {
            return;
        }

        stackList[slot] = stack;
        table.castingTable.setStackInSlot(slot, stack);
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

        if (((SlotCasting) table.getSlot(slot)).getCastState() == 0) {
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
            return stackList[slotNum];
        } else {
            return null;
        }
    }

    @Override
    public void markDirty() {
    }

}
