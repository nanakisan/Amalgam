package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class InventoryCasting implements IInventory, ISidedInventory {

    private ItemStack[]      stackList = new ItemStack[9];
    private int              inventoryWidth;
    private ContainerCasting table;

    public InventoryCasting(ContainerCasting container, int rows, int cols) {
        this.table = container;
        this.inventoryWidth = rows;
        this.stackList = new ItemStack[rows * cols];
    }

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

    @Override
    public int getSizeInventory() {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        Slot s = table.getSlot(slot);
        if (s instanceof SlotCasting && ((SlotCasting) s).getCastState() == 1) {
            return null;
        }

        return stackList[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int decNum) {
        Slot s = table.getSlot(slot);
        if (s instanceof SlotCasting && ((SlotCasting) s).getCastState() != 0) {
            return null;
        }

        if (stackList[slot] != null) {
            ItemStack itemstack;

            if (stackList[slot].stackSize <= decNum) {
                itemstack = stackList[slot];
                this.setInventorySlotContents(slot, null);
                this.table.onCraftMatrixChanged(this);
                return itemstack;
            } else {
                itemstack = stackList[slot].splitStack(decNum);

                if (stackList[slot].stackSize == 0) {
                    this.setInventorySlotContents(slot, itemstack);

                }
                this.table.onCraftMatrixChanged(this);
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
        Slot s = table.getSlot(slot);
        if (s instanceof SlotCasting && ((SlotCasting) s).getCastState() != 0) {
            return;
        }

        stackList[slot] = stack;
        table.castingTable.setStackInSlot(slot, stack);
        this.table.onCraftMatrixChanged(this);
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

        Slot s = table.getSlot(slot);
        if (s instanceof SlotCasting && ((SlotCasting) s).getCastState() != 0) {
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
        Slot s = table.getSlot(slot);
        if (s instanceof SlotCasting) {
            return ((SlotCasting) this.table.getSlot(slot)).getCastState();
        }
        return 0;
    }

    public void useAmalgamForCrafting() {
        this.table.castingTable.setTankFluid(null);
    }
}
