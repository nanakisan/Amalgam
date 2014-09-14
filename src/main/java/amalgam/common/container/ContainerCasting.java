package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import amalgam.common.Config;
import amalgam.common.casting.CastingManager;
import amalgam.common.casting.ICastingRecipe;
import amalgam.common.network.PacketHandler;
import amalgam.common.network.PacketSyncCastingItem;
import amalgam.common.network.PacketSyncCastingState;
import amalgam.common.properties.PropertyList;
import amalgam.common.tile.TileCastingTable;

public class ContainerCasting extends Container {

    public TileCastingTable castingTable;

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
        if (!this.castingTable.getWorldObj().isRemote) {
            if (slotNum >= 0 && slotNum < this.inventorySlots.size() && shiftNum != 6) {
                Slot slot = this.getSlot(slotNum);

                if (slot instanceof SlotCasting) {
                    if (!slot.getHasStack() && player.inventory.getItemStack() == null) {

                        int castState = castingTable.castingInventory.getCastState(slotNum);
                        if (shiftNum == 0) {
                            castState = castState + 1;
                        } else {
                            castState = castState - 1;
                        }

                        if (castState > 3) {
                            castState = 0;
                        } else if (castState < 0) {
                            castState = 3;
                        }

                        // set the new state on the server
                        castingTable.castingInventory.setCastState(slot.slotNumber, castState);
                        // send the new state to the clients
                        PacketHandler.INSTANCE.sendToAll(new PacketSyncCastingState(slot.slotNumber, castState, this.castingTable.xCoord,
                                this.castingTable.yCoord, this.castingTable.zCoord));
                    }
                }
            }
        }

        return super.slotClick(slotNum, ctrNum, shiftNum, player);
    }

    @Override
    public void detectAndSendChanges() {
        for (int i = 0; i < this.inventorySlots.size(); ++i) {
            ItemStack itemstack = ((Slot) this.inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack) this.inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack) && this.inventorySlots.get(i) instanceof SlotCasting) {
                Config.LOG.info("Change detected");
                itemstack1 = itemstack == null ? null : itemstack.copy();
                this.inventoryItemStacks.set(i, itemstack1);

                PacketHandler.INSTANCE.sendToAll(new PacketSyncCastingItem(itemstack1, i, this.castingTable.xCoord, this.castingTable.yCoord,
                        this.castingTable.zCoord));
            }
        }
    }
}
