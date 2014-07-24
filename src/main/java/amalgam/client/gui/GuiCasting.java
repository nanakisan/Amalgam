package amalgam.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import amalgam.common.container.ContainerCastingTable;
import amalgam.common.container.SlotCasting;
import amalgam.common.tile.TileCastingTable;

public class GuiCasting extends GuiContainer{

	private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("textures/gui/container/crafting_table.png");
    
	public GuiCasting(Container container) {
		super(container);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		// TODO this is taken straight from GuiCraftng, needs to be changed
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(craftingTableGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        
        ContainerCastingTable table = (ContainerCastingTable)this.inventorySlots;
        for(int i=0;i<9;i++){
        	SlotCasting s = (SlotCasting) table.getSlot(i);
        	if(s.castState() != 0 ){
            	// TODO add custom image to the corner generic workbench gui that can be used as an overlay to show a slot is casting 

        		int rowNum = i/3;
        		int colNum = i%3;
        		// parameters 1 and 2 are where we draw
        		// parameters 3 and 4 determine where on the image we draw from
        		// parameters 5 and 6 determine how much we are drawing
        		if(s.hasAmalgam()) this.drawTexturedModalRect(k+30+18*colNum, l+17+18*rowNum, 10, 10, 16, 16);
        		else this.drawTexturedModalRect(k+30+18*colNum, l+17+18*rowNum, 20, 20, 16, 16);
        	}
        }
	}

	@Override
	protected void mouseClicked(int x, int y, int keyNum){
        Slot slot = this.getSlotAtPosition(x, y);
        if(slot instanceof SlotCasting){
        	if(!slot.getHasStack() && this.mc.thePlayer.inventory.getItemStack() == null){
        		int newState = ((SlotCasting) slot).toggleCastState();
        		
                ContainerCastingTable table = (ContainerCastingTable)this.inventorySlots;
                TileCastingTable te = table.castingTable;
                te.setCastState(slot.slotNumber, newState);
                table.updateTank();
        	}
        }
        super.mouseClicked(x, y, keyNum);
    }

	private Slot getSlotAtPosition(int p_146975_1_, int p_146975_2_){
		for (int k = 0; k < this.inventorySlots.inventorySlots.size(); ++k){
			Slot slot = (Slot)this.inventorySlots.inventorySlots.get(k);
			if (this.isMouseOverSlot(slot, p_146975_1_, p_146975_2_)){
				return slot;
			}
		}
		return null;
	}
	 /**
	     * Returns if the passed mouse position is over the specified slot.
	     */
	private boolean isMouseOverSlot(Slot p_146981_1_, int p_146981_2_, int p_146981_3_){
		return this.func_146978_c(p_146981_1_.xDisplayPosition, p_146981_1_.yDisplayPosition, 16, 16, p_146981_2_, p_146981_3_);
	}
}
