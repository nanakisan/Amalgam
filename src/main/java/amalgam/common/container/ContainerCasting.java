package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import amalgam.common.Config;
import amalgam.common.casting.CastingManager;
import amalgam.common.casting.ICastingRecipe;
import amalgam.common.properties.PropertyList;
import amalgam.common.tile.TileCastingTable;

public class ContainerCasting extends Container {

    public TileCastingTable castingTable;

    /* FIXME add mouseover text to gui saying which state a slot is in and how much amalgam it contains. It should also
     * say how much more amalgam is needed total when mousing over the cast result */

    /* FIXME allow shift clicking a slot to cycle it in reverse */
    public ContainerCasting(InventoryPlayer inv, TileCastingTable te) {
        super();

        this.castingTable = te;

        int rowNum;
        int colNum;
        int slotNum;

        for (rowNum = 0; rowNum < 3; ++rowNum) {
            for (colNum = 0; colNum < 3; ++colNum) {
                slotNum = colNum + rowNum * 3;

                SlotCasting s = new SlotCasting(castingTable.castingInventory, slotNum, 30 + colNum * 18, 17 + rowNum * 18);
                this.addSlotToContainer(s);
            }
        }

        this.addSlotToContainer(new SlotCastingResult(inv.player, castingTable.castingInventory, castingTable.castingResult, 0, 124, 35));

        for (rowNum = 0; rowNum < 3; ++rowNum) {
            for (colNum = 0; colNum < 9; ++colNum) {
                slotNum = colNum + rowNum * 9 + 9;
                this.addSlotToContainer(new Slot(inv, slotNum, 8 + colNum * 18, 84 + rowNum * 18));
            }
        }

        for (colNum = 0; colNum < 9; ++colNum) {
            this.addSlotToContainer(new Slot(inv, colNum, 8 + colNum * 18, 142));
        }

        this.onCraftMatrixChanged(this.castingTable.castingInventory);
    }

    // public final void updateAmalgamDistribution() {
    // TileCastingTable te = this.castingTable;
    // int amount = te.getFluidAmount();
    //
    // for (int slotNum = 0; slotNum < this.inventorySlots.size(); slotNum++) {
    // Slot s = this.getSlot(slotNum);
    //
    // if (s instanceof SlotCasting) {
    // /* Check if we have anymore amalgam */
    // if (amount <= 0) {
    // ((SlotCasting) s).setHasAmalgam(false);
    // ((SlotCasting) s).setIsFull(false);
    // } else {
    // ((SlotCasting) s).setHasAmalgam(true);
    // int state = te.castingInventory.getCastState(slotNum);
    // /* If we have amalgam, decrease the total by the amount needed to fill the slot based on it's
    // * casting state */
    // switch (state) {
    // case SlotCasting.NUGGET_STATE:
    // amount -= Config.BASE_AMOUNT;
    // break;
    // case SlotCasting.INGOT_STATE:
    // amount -= Config.INGOT_AMOUNT;
    // break;
    // case SlotCasting.BLOCK_STATE:
    // amount -= Config.BLOCK_AMOUNT;
    // break;
    // }
    //
    // /* If we didn't have enough amalgam to fill the slot, set isFull to false */
    // if (amount < 0) {
    // ((SlotCasting) s).setIsFull(false);
    // } else {
    // ((SlotCasting) s).setIsFull(true);
    // }
    // }
    // }
    // }
    // }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return castingTable.getWorldObj().getBlock(castingTable.xCoord, castingTable.yCoord, castingTable.zCoord) == Config.castingTable ? player
                .getDistanceSq((double) castingTable.xCoord + 0.5D, (double) castingTable.yCoord + 0.5D, (double) castingTable.zCoord + 0.5D) <= 64.0D
                : false;
    }

    @Override
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

    @Override
    public void onCraftMatrixChanged(IInventory inv) {
        super.onCraftMatrixChanged(inv);
        Config.LOG.info("in here, what is going on?");
        // this.updateAmalgamDistribution();
        ICastingRecipe recipe = CastingManager.findMatchingRecipe(castingTable.castingInventory, castingTable.getWorldObj());

        if (recipe == null) {
            castingTable.castingResult.setInventorySlotContents(0, null);
            return;
        }

        PropertyList pList = castingTable.getAmalgamPropertyList();
        if (castingTable.getFluidAmount() == 0) {
            pList = null;
        }

        castingTable.castingResult.setInventorySlotContents(0, recipe.getCastingResult(castingTable.castingInventory, pList));

    }

    @Override
    public ItemStack slotClick(int slotNum, int ctrNum, int shiftNum, EntityPlayer player) {
        if (slotNum >= 0 && slotNum < this.inventorySlots.size()) {
            Slot slot = this.getSlot(slotNum);

            if (slot instanceof SlotCasting) {
                if (!slot.getHasStack() && player.inventory.getItemStack() == null) {
                    int newState = ((SlotCasting) slot).toggleCastState(true);
                    castingTable.castingInventory.setCastState(slot.slotNumber, newState);
                    this.onCraftMatrixChanged(castingTable.castingInventory);
                }
            }
        }

        return super.slotClick(slotNum, ctrNum, shiftNum, player);
    }

}
