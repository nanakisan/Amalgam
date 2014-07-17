package amalgam.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import amalgam.common.tile.TileCastingTable;

public class ContainerCastingTable extends Container{

	// this tile entity associated with this container has the crafting inventories (craftMatrix and craftResult in ContianerWorkbench)
	private TileCastingTable castingTable;
	private InventoryPlayer playerInv;
	
	public ContainerCastingTable(InventoryPlayer inv, TileCastingTable te){
		this.castingTable = te;
		this.playerInv = inv;
		
		// create casting slots here
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

}
