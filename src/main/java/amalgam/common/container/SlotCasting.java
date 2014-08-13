package amalgam.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCasting extends Slot {

    private static final int MAX_STATE = 1;
    private int              castState;
    private boolean          hasAmalgam;

    public SlotCasting(IInventory inv, int slotNum, int xPos, int yPos) {
        super(inv, slotNum, xPos, yPos);
        castState = 0;
        hasAmalgam = false;
    }

    public boolean isItemValid(ItemStack stack) {
        return castState == 0;
    }

    public int toggleCastState() {
        castState = castState + 1;
        if (castState > MAX_STATE) {
            castState = 0;
            hasAmalgam = false;
        }
        return castState;
    }

    public int getCastState() {
        return this.castState;
    }

    public boolean containsAmalgam() {
        return this.hasAmalgam && this.castState != 0;
    }

    public void doesHaveAmalgam(boolean hasAmalgam) {
        this.hasAmalgam = hasAmalgam;
    }

    public void setCastState(int castState) {
        this.castState = castState;
    }
}
