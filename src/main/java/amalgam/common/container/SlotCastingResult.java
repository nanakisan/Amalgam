package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import amalgam.common.fluid.AmalgamTank;
import cpw.mods.fml.common.FMLCommonHandler;

public class SlotCastingResult extends Slot {

    /** The casting matrix inventory linked to this result slot. */
    private InventoryCasting castingMatrix;
    /** The player that is using the GUI where this slot resides. */
    private EntityPlayer     player;
    /**
     * The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset.
     */
    private int              amountCrafted;

    private AmalgamTank      tank;

    public SlotCastingResult(EntityPlayer player, AmalgamTank tank, InventoryCasting castMatrix, InventoryCastResult castResult, int slotID,
            int xPos, int yPos) {
        super(castResult, slotID, xPos, yPos);
        this.castingMatrix = castMatrix;
        this.player = player;
        this.tank = tank;
    }

    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    public ItemStack decrStackSize(int amount) {
        if (this.getHasStack()) {
            this.amountCrafted += Math.min(amount, this.getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }

    /**
     * The itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCasting(item).
     */
    protected void onCrafting(ItemStack output, int amount) {
        this.amountCrafted += amount;
        this.onCrafting(output);
    }

    /**
     * The itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(ItemStack output) {
        output.onCrafting(this.player.worldObj, this.player, this.amountCrafted);
        this.amountCrafted = 0;
    }

    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, castingMatrix);
        this.onCrafting(stack);

        // TODO need to drain amalgam at some point after cast item is picked up.

        this.tank.drain(-1, true);

        for (int i = 0; i < this.castingMatrix.getSizeInventory(); ++i) {
            ItemStack itemstack1 = this.castingMatrix.getStackInSlot(i);

            if (itemstack1 != null) {
                this.castingMatrix.decrStackSize(i, 1);

                if (itemstack1.getItem().hasContainerItem(itemstack1)) {
                    ItemStack itemstack2 = itemstack1.getItem().getContainerItem(itemstack1);

                    if (itemstack2 != null && itemstack2.isItemStackDamageable() && itemstack2.getItemDamage() > itemstack2.getMaxDamage()) {
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, itemstack2));
                        continue;
                    }

                    if (!itemstack1.getItem().doesContainerItemLeaveCraftingGrid(itemstack1)
                            || !this.player.inventory.addItemStackToInventory(itemstack2)) {
                        if (this.castingMatrix.getStackInSlot(i) == null) {
                            this.castingMatrix.setInventorySlotContents(i, itemstack2);
                        } else {
                            this.player.dropPlayerItemWithRandomChoice(itemstack2, false);
                        }
                    }
                }
            }
        }
    }
}
