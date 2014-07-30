package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import amalgam.common.Amalgam;
import amalgam.common.tile.TileCastingTable;

public class ContainerCastingTable extends Container {

    // this tile entity associated with this container has the crafting
    // inventories (craftMatrix and craftResult in ContianerWorkbench)
    public TileCastingTable castingTable;
    private InventoryPlayer playerInv;

    public ContainerCastingTable(InventoryPlayer inv, TileCastingTable te) {
        this.castingTable = te;
        this.playerInv = inv;

        // we use 9 as the slotIndex below because that is the index in

        int rowNum;
        int colNum;

        for (rowNum = 0; rowNum < 3; ++rowNum) {
            for (colNum = 0; colNum < 3; ++colNum) {
                SlotCasting s = new SlotCasting(this.castingTable, colNum + rowNum * 3, 30 + colNum * 18, 17 + rowNum * 18);
                s.setCastState(te.getCastState(colNum + rowNum * 3));
                this.addSlotToContainer(s);
            }
        }

        this.addSlotToContainer(new SlotCastingResult(this.playerInv.player, this.castingTable, 9, 124, 35));

        for (rowNum = 0; rowNum < 3; ++rowNum) {
            for (colNum = 0; colNum < 9; ++colNum) {
                this.addSlotToContainer(new Slot(this.playerInv, colNum + rowNum * 9 + 9, 8 + colNum * 18, 84 + rowNum * 18));
            }
        }

        for (colNum = 0; colNum < 9; ++colNum) {
            this.addSlotToContainer(new Slot(this.playerInv, colNum, 8 + colNum * 18, 142));
        }

        this.onCraftMatrixChanged(this.castingTable);
        this.updateAmalgamDistribution(); // not sure if this should go here or
                                          // not
    }

    public final void updateAmalgamDistribution() {
        TileCastingTable te = this.castingTable;
        int amount = te.tank.getFluidAmount();
        for (int slotNum = 0; slotNum < 9; slotNum++) {
            SlotCasting s = (SlotCasting) this.getSlot(slotNum);
            if (te.getCastState(slotNum) == 1 && amount > 0) {
                s.setHasAmalgam(true);
                amount -= Amalgam.INGOTAMOUNT;
            } else {
                s.setHasAmalgam(false);
            }
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory castingTable) {
        super.onCraftMatrixChanged(castingTable);
        // this.castingTable.castResult.setInventorySlotContents(0,
        // CraftingManager.getInstance().findMatchingRecipe(this.castingTable,
        // this.castingTable.getWorldObj()));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        // TODO look into this
        return castingTable.getWorldObj().getBlock(castingTable.xCoord, castingTable.yCoord, castingTable.zCoord) != Amalgam.castingTable ? false
                : player.getDistanceSq((double) castingTable.xCoord + 0.5D, (double) castingTable.yCoord + 0.5D, (double) castingTable.zCoord + 0.5D) <= 64.0D;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or
     * you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNum) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotNum);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotNum == 0) {
                if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (slotNum >= 10 && slotNum < 37) {
                if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
                    return null;
                }
            } else if (slotNum >= 37 && slotNum < 46) {
                if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }
}
