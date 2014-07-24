package amalgam.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import amalgam.common.Amalgam;

public class SlotCasting extends Slot{

	private static final int maxState = 1;
	private int castState;
	private boolean hasAmalgam;
	
	public SlotCasting(IInventory inv, int slotNum, int xPos, int yPos) {
		super(inv, slotNum, xPos, yPos);
		
		castState = 0;
		hasAmalgam = false;
	}
	
    public boolean isItemValid(ItemStack p_75214_1_){
    	// only return true if the cast state is zero
        return castState == 0;
    }

    public int toggleCastState(){
    	castState = castState + 1;
    	if(castState > maxState){
    		castState = 0;
    		hasAmalgam = false;
    	}

    	Amalgam.log.info("new cast state! " + castState);
    	
    	return castState;
    }
    
    public int castState(){
    	return this.castState;
    }
    
    public boolean hasAmalgam(){
    	return this.hasAmalgam && (this.castState != 0);
    }
    
    public void setHasAmalgam(boolean a){
    	this.hasAmalgam = a;
    }

	public void setCastState(int castState) {
		this.castState = castState;
	}
}
