package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import cpw.mods.fml.common.FMLCommonHandler;

public class SlotCastingResult extends Slot {

    private final InventoryCasting castingInventory;
    private final EntityPlayer     player;
    private int                    amountCrafted;

    public SlotCastingResult(EntityPlayer player, InventoryCasting castingInventory, InventoryCastingResult castingResult, int slotID, int xPos,
            int yPos) {
        super(castingResult, slotID, xPos, yPos);
        this.castingInventory = castingInventory;
        this.player = player;
    }

    public boolean canTakeStack(EntityPlayer player) {
        return this.castingInventory.handler.isCastComplete();
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

    protected void onCrafting(ItemStack output, int amount) {
        this.amountCrafted += amount;
        this.onCrafting(output);
    }

    protected void onCrafting(ItemStack output) {
        output.onCrafting(this.player.worldObj, this.player, this.amountCrafted);
        this.amountCrafted = 0;
    }

    private void consumeStackInSlot(ItemStack stack, int slot) {
        this.castingInventory.decrStackSize(slot, 1);

        if (stack.getItem().hasContainerItem(stack)) {
            ItemStack itemstack2 = stack.getItem().getContainerItem(stack);

            if (itemstack2 != null && itemstack2.isItemStackDamageable() && itemstack2.getItemDamage() > itemstack2.getMaxDamage()) {
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, itemstack2));
                return;
            }

            if (!stack.getItem().doesContainerItemLeaveCraftingGrid(stack) || !this.player.inventory.addItemStackToInventory(itemstack2)) {
                if (this.castingInventory.getStackInSlot(slot) == null) {
                    this.castingInventory.setInventorySlotContents(slot, itemstack2);
                } else {
                    this.player.dropPlayerItemWithRandomChoice(itemstack2, false);
                }
            }
        }
    }

    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, castingInventory);
        this.onCrafting(stack);

        for (int i = 0; i < this.castingInventory.getSizeInventory(); ++i) {
            ItemStack itemstack1 = this.castingInventory.getStackInSlot(i);

            if (itemstack1 != null) {
                consumeStackInSlot(itemstack1, i);
            }
        }

        this.castingInventory.handler.onCastPickup();
    }
}
