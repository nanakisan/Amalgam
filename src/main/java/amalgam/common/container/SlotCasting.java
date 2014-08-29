package amalgam.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCasting extends Slot {

    private static final int MAX_STATE    = 3;

    public static final int  EMPTY_STATE  = 0;
    public static final int  NUGGET_STATE = 1;
    public static final int  INGOT_STATE  = 2;
    public static final int  BLOCK_STATE  = 3;

    private int              castState;
    private boolean          hasAmalgam;
    private boolean          isFull;

    public SlotCasting(IInventory inv, int slotNum, int xPos, int yPos) {
        super(inv, slotNum, xPos, yPos);
        castState = 0;
        hasAmalgam = false;
        isFull = false;
    }

    public boolean isItemValid(ItemStack stack) {
        return castState == 0;
    }

    public int toggleCastState() {
        castState = castState + 1;

        if (castState > MAX_STATE) {
            castState = 0;
            hasAmalgam = false;
            isFull = false;
        }

        return castState;
    }

    public void setCastState(int castState) {
        this.castState = castState;
    }
    
    public int getCastState() {
        return this.castState;
    }

    public void setHasAmalgam(boolean hasAmalgam) {
        this.hasAmalgam = hasAmalgam;
    }
    
    public boolean getHasAmalgam() {
        return this.hasAmalgam && this.castState != 0;
    }
    
    public void setIsFull(boolean isFull) {
        this.isFull = isFull;
    }
    
    public boolean getIsFull() {
        return this.isFull && this.castState != 0;
    }
}
