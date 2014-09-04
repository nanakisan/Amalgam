package amalgam.client.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import amalgam.common.Amalgam;
import amalgam.common.container.ContainerCasting;
import amalgam.common.container.SlotCasting;
import amalgam.common.properties.PropertyManager;
import amalgam.common.tile.TileCastingTable;

public class GuiCasting extends GuiContainer {

    private static final ResourceLocation CRAFTING_GUI_TEXTURES = new ResourceLocation(Amalgam.MODID, "textures/gui/CastingGui.png");

    public GuiCasting(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float floatParam, int intParam1, int intParam2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        /* Load the gui texture. This is essential before we call any draw commands */
        this.mc.getTextureManager().bindTexture(CRAFTING_GUI_TEXTURES);
        int xPos = (this.width - this.xSize) / 2;
        int yPos = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);

        ContainerCasting table = (ContainerCasting) this.inventorySlots;
        int color = (int) table.castingTable.getAmalgamPropertyList().getValue(PropertyManager.COLOR);
        
        Color c = new Color(color);
        SlotCasting testSlot;

        /* Loop through all the casting slots, get their state and the amount of amalgam in them, and overlay them on
         * the gui. */
        for (int i = 0; i < 9; i++) {
            testSlot = (SlotCasting) table.getSlot(i);
            int state = testSlot.getCastState();

            if (state != SlotCasting.EMPTY_STATE) {
                int rowNum = i / 3;
                int colNum = i % 3;

                /* Render the casting slot differently depending on the casting state and the amalgam in the slot */
                this.drawTexturedModalRect(xPos + 30 + 18 * colNum, yPos + 17 + 18 * rowNum, 160 + 17 * state, 1, 16, 16);
                
                /* change to amalgam color and render fill overlay */
                GL11.glColor3f(c.getRed()/255.0F, c.getGreen()/255.0F, c.getBlue()/255.0F);
                this.drawTexturedModalRect(xPos + 30 + 18 * colNum, yPos + 17 + 16 - (int)(16.0 * testSlot.getFillLevel()) + 18 * rowNum, 160 + 17 * state , 35- (int)(16.0 * testSlot.getFillLevel()) + 16, 16, (int)(16.0 * testSlot.getFillLevel()));
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }
        }

        TileCastingTable castingTable = table.castingTable;

        /* Add the red overlay if the cast result is not complete. */
        if (castingTable.getEmptySpace() != 0 && castingTable.getStackInSlot(9) != null) {
            float red = (float) Math.pow(Math.sin(Minecraft.getMinecraft().theWorld.getWorldTime() * 0.1), 2);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(red, 0.0F, 0.0F, 0.3F);
            this.drawTexturedModalRect(xPos + 120, yPos + 31, 178, 52, 24, 24);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
