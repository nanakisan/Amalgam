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

    public TileCastingTable    castingTable;
    public InventoryCasting    castingMatrix;
    public InventoryCastResult castResult;

    public ContainerCasting(InventoryPlayer inv, TileCastingTable te) {
        super();

        this.castingTable = te;

        castingMatrix = new InventoryCasting(this, 3, 3);
        castResult = new InventoryCastResult(this);

        int rowNum;
        int colNum;
        int slotNum;

        for (rowNum = 0; rowNum < 3; ++rowNum) {
            for (colNum = 0; colNum < 3; ++colNum) {
                slotNum = colNum + rowNum * 3;

                SlotCasting s = new SlotCasting(castingMatrix, slotNum, 30 + colNum * 18, 17 + rowNum * 18);
                s.setCastState(te.getCastState(slotNum));
                this.addSlotToContainer(s);
            }
        }

        for (rowNum = 0; rowNum < 3; ++rowNum) {
            for (colNum = 0; colNum < 3; ++colNum) {
                slotNum = colNum + rowNum * 3;
                castingMatrix.setInventorySlotContents(slotNum, castingTable.getStackInSlot(slotNum));
            }
        }
        this.addSlotToContainer(new SlotCastingResult(inv.player, castingMatrix, castResult, 0, 124, 35));

        for (rowNum = 0; rowNum < 3; ++rowNum) {
            for (colNum = 0; colNum < 9; ++colNum) {
                slotNum = colNum + rowNum * 9 + 9;
                this.addSlotToContainer(new Slot(inv, slotNum, 8 + colNum * 18, 84 + rowNum * 18));
            }
        }

        for (colNum = 0; colNum < 9; ++colNum) {
            this.addSlotToContainer(new Slot(inv, colNum, 8 + colNum * 18, 142));
        }

        this.onCraftMatrixChanged(this.castingMatrix);
    }

    public final void updateAmalgamDistribution() {
        TileCastingTable te = this.castingTable;
        int amount = te.getTankAmount();
        for (int slotNum = 0; slotNum < this.inventorySlots.size(); slotNum++) {
            Slot s = this.getSlot(slotNum);
            if (s instanceof SlotCasting) {
                if (te.getCastState(slotNum) == 1 && amount > 0) {
                    ((SlotCasting) s).doesHaveAmalgam(true);
                    amount -= Config.INGOT_AMOUNT;
                } else {
                    ((SlotCasting) s).doesHaveAmalgam(false);
                }
            }
        }
    }

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
        this.updateAmalgamDistribution();
        ICastingRecipe recipe = CastingManager.findMatchingRecipe(castingMatrix, castingTable.getWorldObj());
        if (recipe == null) {
            castResult.setInventorySlotContents(0, null);
            return;
        }
        PropertyList pList = castingTable.getAmalgamPropertyList();
        if(castingTable.getTankAmount() == 0){
            pList = null;
        }

        castResult.setInventorySlotContents(0, recipe.getCastingResult(castingMatrix, pList));

    }

    @Override
    public ItemStack slotClick(int slotNum, int ctrNum, int shiftNum, EntityPlayer player) {
        if (slotNum >= 0 && slotNum < this.inventorySlots.size()) {
            Slot slot = this.getSlot(slotNum);
            if (slot instanceof SlotCasting) {
                if (!slot.getHasStack() && player.inventory.getItemStack() == null) {
                    int newState = ((SlotCasting) slot).toggleCastState();
                    castingTable.setCastState(slot.slotNumber, newState);
                    this.onCraftMatrixChanged(castingMatrix);
                }
            }
        }

        return super.slotClick(slotNum, ctrNum, shiftNum, player);
    }

}
