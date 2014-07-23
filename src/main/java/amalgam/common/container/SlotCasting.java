package amalgam.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCasting extends Slot {

	private static int maxState = 1;
	private int castState;
	
	public SlotCasting(IInventory inv, int slotNum, int xPos, int yPos) {
		super(inv, slotNum, xPos, yPos);
		
		castState = 0;
	}
	
    public boolean isItemValid(ItemStack p_75214_1_){
    	// only return true if the cast state is zero
        return castState == 0;
    }

    public void toggleCastState(){
    	castState = castState + 1;
    	if(castState > maxState) castState = 0;
    	
    	// TODO change background image depending on the state?
    }
    
    public int castState(){
    	return this.castState;
    }
}
