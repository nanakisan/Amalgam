package amalgam.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import amalgam.common.container.ContainerCastingTable;
import amalgam.common.container.SlotCasting;
import amalgam.common.network.PacketHandler;
import amalgam.common.network.PacketSyncCastingSlot;
import amalgam.common.tile.TileCastingTable;

public class GuiCasting extends GuiContainer {

    private static final ResourceLocation CRAFTING_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");

    public GuiCasting(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f1, int i1, int i2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CRAFTING_GUI_TEXTURES);
        int xPos = (this.width - this.xSize) / 2;
        int yPos = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);

        ContainerCastingTable table = (ContainerCastingTable) this.inventorySlots;
        for (int i = 0; i < 9; i++) {
            SlotCasting slot = (SlotCasting) table.getSlot(i);
            if (slot.getCastState() != 0) {
                // TODO add custom image to the corner generic workbench gui
                // that can be used as an overlay to show a slot is casting

                int rowNum = i / 3;
                int colNum = i % 3;
                // parameters 1 and 2 are where we draw
                // parameters 3 and 4 determine where on the image we draw from
                // parameters 5 and 6 determine how much we are drawing
                if (slot.hasAmalgam()) {
                    this.drawTexturedModalRect(xPos + 30 + 18 * colNum, yPos + 17 + 18 * rowNum, 10, 10, 16, 16);
                } else {
                    this.drawTexturedModalRect(xPos + 30 + 18 * colNum, yPos + 17 + 18 * rowNum, 20, 20, 16, 16);
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int keyNum) {
        Slot slot = this.getSlotAtPosition(x, y);
        if (slot instanceof SlotCasting) {
            if (!slot.getHasStack() && this.mc.thePlayer.inventory.getItemStack() == null) {
                int newState = ((SlotCasting) slot).toggleCastState();
                ContainerCastingTable table = (ContainerCastingTable) this.inventorySlots;
                TileCastingTable te = table.castingTable;
                te.setCastState(slot.slotNumber, newState);
                PacketHandler.INSTANCE.sendToServer(new PacketSyncCastingSlot(te.xCoord, te.yCoord, te.zCoord, slot.slotNumber, newState));
                table.updateAmalgamDistribution();
            }
        }
        super.mouseClicked(x, y, keyNum);
    }

    private Slot getSlotAtPosition(int xPos, int yPos) {
        for (int k = 0; k < this.inventorySlots.inventorySlots.size(); ++k) {
            Slot slot = (Slot) this.inventorySlots.inventorySlots.get(k);
            if (this.isMouseOverSlot(slot, xPos, yPos)) {
                return slot;
            }
        }
        return null;
    }

    /** Returns if the passed mouse position is over the specified slot. */
    private boolean isMouseOverSlot(Slot slot, int xPos, int yPos) {
        return this.func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, xPos, yPos);
    }
}
