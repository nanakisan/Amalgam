package amalgam.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import amalgam.common.Amalgam;
import amalgam.common.container.ContainerCasting;
import amalgam.common.container.SlotCasting;
import amalgam.common.tile.TileCastingTable;

public class GuiCasting extends GuiContainer {

    private static final ResourceLocation CRAFTING_GUI_TEXTURES = new ResourceLocation(Amalgam.MODID, "textures/gui/CastingGui.png");

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
        SlotCasting testSlot;

        for (int i = 0; i < 9; i++) {
            testSlot = (SlotCasting) table.getSlot(i);

            if (testSlot.getCastState() != 0) {
                int rowNum = i / 3;
                int colNum = i % 3;

                if (testSlot.containsAmalgam()) {
                    this.drawTexturedModalRect(xPos + 30 + 18 * colNum, yPos + 17 + 18 * rowNum, 178, 18, 16, 16);
                } else {
                    this.drawTexturedModalRect(xPos + 30 + 18 * colNum, yPos + 17 + 18 * rowNum, 178, 1, 16, 16);
                }
            }
        }

        TileCastingTable castingTable = table.castingTable;

        if (castingTable.getEmptySpace() != 0 && castingTable.getStackInSlot(9) != null) {
            float red = (float) Math.pow(Math.sin(Minecraft.getMinecraft().theWorld.getWorldTime() * 0.1), 2);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(red, 0.0F, 0.0F, 0.3F);
            this.drawTexturedModalRect(xPos + 120, yPos + 31, 178, 37, 24, 24);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
