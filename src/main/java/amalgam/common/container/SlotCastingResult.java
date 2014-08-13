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

    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, castingMatrix);
        this.onCrafting(stack);

        this.castingMatrix.useAmalgamForCrafting();
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
