package amalgam.common.container;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCasting extends Slot {

    private static final int MAX_STATE    = 3;

    public static final int  EMPTY_STATE  = 0;
    public static final int  NUGGET_STATE = 1;
    public static final int  INGOT_STATE  = 2;
    public static final int  BLOCK_STATE  = 3;

    private boolean          hasAmalgam;
    private boolean          isFull;

    public SlotCasting(InventoryCasting inv, int slotNum, int xPos, int yPos) {
        super(inv, slotNum, xPos, yPos);
        hasAmalgam = false;
        isFull = false;
    }

    public boolean isItemValid(ItemStack stack) {
        return getCastState() == 0;
    }

    public int toggleCastState(boolean up) {
        int castState = getCastState();
        if (up) {
            castState = castState + 1;
        } else {
            castState = castState - 1;
        }

        if (castState > MAX_STATE) {
            castState = 0;
            hasAmalgam = false;
            isFull = false;
        } else if (castState < 0) {
            castState = MAX_STATE;
        }

        setCastState(castState);
        return castState;
    }

    public void setCastState(int castState) {
        ((InventoryCasting) this.inventory).setCastState(this.slotNumber, castState);
    }

    public int getCastState() {
        return ((InventoryCasting) this.inventory).getCastState(this.slotNumber);
    }

    public void setHasAmalgam(boolean hasAmalgam) {
        this.hasAmalgam = hasAmalgam;
    }

    public boolean getHasAmalgam() {
        return this.hasAmalgam && getCastState() != 0;
    }

    public void setIsFull(boolean isFull) {
        this.isFull = isFull;
    }

    public boolean getIsFull() {
        return this.isFull && getCastState() != 0;
    }
}
