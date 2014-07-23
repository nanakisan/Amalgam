package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import amalgam.common.tile.TileCastingTable;

public class ContainerCastingTable extends Container{

	// TODO look at craftingTable or furnace implementations to figure out how to add slots and a gui
	
	// this tile entity associated with this container has the crafting inventories (craftMatrix and craftResult in ContianerWorkbench)
	private TileCastingTable castingTable;
	private InventoryPlayer playerInv;
	
	public ContainerCastingTable(InventoryPlayer inv, TileCastingTable te){
		this.castingTable = te;
		this.playerInv = inv;
		
		// we use 9 as the slotIndex below because that is the index in 
		this.addSlotToContainer(new SlotCastingResult(this.playerInv.player, this.castingTable, 9, 124, 35));
		int l;
		int i1;

		for (l = 0; l < 3; ++l){
			for (i1 = 0; i1 < 3; ++i1){
				this.addSlotToContainer(new SlotCasting(this.castingTable, i1 + l * 3, 30 + i1 * 18, 17 + l * 18));
	        }
	    }

		for (l = 0; l < 3; ++l){
			for (i1 = 0; i1 < 9; ++i1){
				this.addSlotToContainer(new Slot(this.playerInv, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
			}
		}

		for (l = 0; l < 9; ++l){
			this.addSlotToContainer(new Slot(this.playerInv, l, 8 + l * 18, 142));
		}

		this.onCraftMatrixChanged(this.castingTable);
	}
	
    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory castingTable){
        //this.castingTable.castResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.castingTable, this.castingTable.getWorldObj()));
    }
    
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		//return castingTable.getWorldObj().getBlock(castingTable.xCoord, castingTable.yCoord, castingTable.zCoord) != Amalgam.castingTable ? false : player.getDistanceSq((double)castingTable.xCoord + 0.5D, (double)castingTable.yCoord + 0.5D, (double)castingTable.zCoord + 0.5D) <= 64.0D;
		return true;
	}

}
