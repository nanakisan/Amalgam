package amalgam.common.container;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import amalgam.common.tile.TileCastingTable;

public class SlotCastingResult extends Slot {

	/** The casting matrix inventory linked to this result slot. */
	private TileCastingTable castingTable;
	/** The player that is using the GUI where this slot resides. */
	private EntityPlayer player;
	/** The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset.*/
    private int amountCrafted;
    
	public SlotCastingResult(EntityPlayer player, TileCastingTable castingTable, int slotID, int xPos, int yPos) {
		super(castingTable, slotID, xPos, yPos);
		this.castingTable = castingTable;
		this.player = player;
	}
	
	public boolean isItemValid(ItemStack stack){
        return false;
    }

	public ItemStack decrStackSize(int amount){
        if (this.getHasStack()){
            this.amountCrafted += Math.min(amount, this.getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }
	
	/**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCasting(item).
     */
    protected void onCrafting(ItemStack output, int amount){
        this.amountCrafted += amount;
        this.onCrafting(output);
    }
    
    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(ItemStack output){
        output.onCrafting(this.player.worldObj, this.player, this.amountCrafted);
        this.amountCrafted = 0;
        // TODO this is probably where I should add the amalgam properties to the itemstack
    }
    
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack){
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, castingTable);
        this.onCrafting(stack);

        // remove amalgam from casting table tank here
        this.castingTable.drain(ForgeDirection.UNKNOWN, -1, true);
        
        for (int i = 0; i < this.castingTable.getSizeInventory(); ++i){
            ItemStack itemstack1 = this.castingTable.getStackInSlot(i);

            if (itemstack1 != null){
                this.castingTable.decrStackSize(i, 1);

                if (itemstack1.getItem().hasContainerItem(itemstack1)){
                    ItemStack itemstack2 = itemstack1.getItem().getContainerItem(itemstack1);

                    if (itemstack2 != null && itemstack2.isItemStackDamageable() && itemstack2.getItemDamage() > itemstack2.getMaxDamage()){
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, itemstack2));
                        continue;
                    }

                    if (!itemstack1.getItem().doesContainerItemLeaveCraftingGrid(itemstack1) || !this.player.inventory.addItemStackToInventory(itemstack2)){
                        if (this.castingTable.getStackInSlot(i) == null){
                            this.castingTable.setInventorySlotContents(i, itemstack2);
                        }else{
                            this.player.dropPlayerItemWithRandomChoice(itemstack2, false);
                        }
                    }
                }
            }
        }
    }
}
