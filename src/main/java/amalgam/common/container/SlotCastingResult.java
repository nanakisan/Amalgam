package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import cpw.mods.fml.common.FMLCommonHandler;

public class SlotCastingResult extends Slot {

    private final InventoryCasting castingMatrix;
    private final EntityPlayer     player;
    private int                    amountCrafted;

    public SlotCastingResult(EntityPlayer player, InventoryCasting castMatrix, InventoryCastResult castResult, int slotID, int xPos, int yPos) {
        super(castResult, slotID, xPos, yPos);
        this.castingMatrix = castMatrix;
        this.player = player;
    }

    public boolean canTakeStack(EntityPlayer player) {
        return this.castingMatrix.castComplete();
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

    private void consumeStackInSlot(ItemStack stack, int slot){
        this.castingMatrix.decrStackSize(slot, 1);

        if (stack.getItem().hasContainerItem(stack)) {
            ItemStack itemstack2 = stack.getItem().getContainerItem(stack);

            if (itemstack2 != null && itemstack2.isItemStackDamageable() && itemstack2.getItemDamage() > itemstack2.getMaxDamage()) {
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, itemstack2));
                return;
            }

            if (!stack.getItem().doesContainerItemLeaveCraftingGrid(stack)
                    || !this.player.inventory.addItemStackToInventory(itemstack2)) {
                if (this.castingMatrix.getStackInSlot(slot) == null) {
                    this.castingMatrix.setInventorySlotContents(slot, itemstack2);
                } else {
                    this.player.dropPlayerItemWithRandomChoice(itemstack2, false);
                }
            }
        }
    }
    
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, castingMatrix);
        this.onCrafting(stack);

        this.castingMatrix.useAmalgamForCrafting();

        for (int i = 0; i < this.castingMatrix.getSizeInventory(); ++i) {
            ItemStack itemstack1 = this.castingMatrix.getStackInSlot(i);

            if (itemstack1 != null) {
                consumeStackInSlot(itemstack1, i);
            }
        }
        
    }
}
