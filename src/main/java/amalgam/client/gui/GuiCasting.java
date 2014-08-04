package amalgam.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import amalgam.common.container.ContainerCasting;
import amalgam.common.container.SlotCasting;

public class GuiCasting extends GuiContainer {

    private static final ResourceLocation CRAFTING_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");

    public GuiCasting(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float floatParam, int intParam1, int intParam2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CRAFTING_GUI_TEXTURES);
        int xPos = (this.width - this.xSize) / 2;
        int yPos = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);

        ContainerCasting table = (ContainerCasting) this.inventorySlots;
        for (int i = 0; i < 9; i++) {
            SlotCasting slot = (SlotCasting) table.getSlot(i);
            if (slot.getCastState() != 0) {

                int rowNum = i / 3;
                int colNum = i % 3;

                if (slot.hasAmalgam()) {
                    this.drawTexturedModalRect(xPos + 30 + 18 * colNum, yPos + 17 + 18 * rowNum, 10, 10, 16, 16);
                } else {
                    this.drawTexturedModalRect(xPos + 30 + 18 * colNum, yPos + 17 + 18 * rowNum, 20, 20, 16, 16);
                }
            }
        }
    }
}
