package amalgam.client.renderers;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import amalgam.common.Config;
import amalgam.common.block.BlockCastingTable;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CastingTableRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        // FIXME render the casting table in the inventory

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);

        IIcon top = ((BlockCastingTable) block).getBlockTextureFromSide(1);
        IIcon base = ((BlockCastingTable) block).getBlockTextureFromSide(0);
        IIcon baseSide = ((BlockCastingTable) block).getBlockTextureFromSide(7);
        IIcon neck = ((BlockCastingTable) block).getBlockTextureFromSide(6);

        renderer.renderFaceYNeg(block, x, y + 1.0F - 0.375F, z, top);
        renderer.renderFaceYPos(block, x, y - 1.0F + 0.25F, z, base);

        renderer.renderFaceXPos(block, x - 1.0F + .875, y, z, baseSide);
        renderer.renderFaceXNeg(block, x + 1.0F - .875, y, z, baseSide);
        renderer.renderFaceZPos(block, x, y, z - 1.0F + .875, baseSide);
        renderer.renderFaceZNeg(block, x, y, z + 1.0F - .875, baseSide);

        renderer.renderFaceXPos(block, x - 1.0F + .6875, y, z, neck);
        renderer.renderFaceXNeg(block, x + 1.0F - .6875, y, z, neck);
        renderer.renderFaceZPos(block, x, y, z - 1.0F + .6875, neck);
        renderer.renderFaceZNeg(block, x, y, z + 1.0F - .6875, neck);

        renderer.clearOverrideBlockTexture();
        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);
        return true;

    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        // TODO change to true after renderInventoryBlock is implemented
        return false;
    }

    @Override
    public int getRenderId() {
        return Config.castingTableRID;
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
        // FIXME render amalgam and items on top of the table

        int amount = ((TileCastingTable) te).getTankAmount();
        for (int i = 0; i < 9; i++) {

            int row = 2 - i % 3;
            int col = 2 - i / 3;
            int castState = ((TileCastingTable) te).getCastState(i);

            if (castState != 0) {
                if (amount > 0) {

                    EntityItem entityitem = new EntityItem(te.getWorldObj(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.lava));

                    entityitem.hoverStart = 0.0F;
                    GL11.glPushMatrix();

                    GL11.glTranslated(x + 0.2 + 0.3 * row, y + 1.01D, z + 0.1 + 0.3 * col);
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 0.0F);

                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

                    GL11.glPopMatrix();
                    amount -= Config.INGOT_AMOUNT;
                } else {
                    // render empty casting slot
                }
            } else {
                ItemStack stack = ((TileCastingTable) te).getStackInSlot(i);

                if (stack != null) {
                    EntityItem entityitem = new EntityItem(te.getWorldObj(), 0.0D, 0.0D, 0.0D, stack);

                    entityitem.hoverStart = 0.0F;
                    GL11.glPushMatrix();

                    GL11.glTranslated(x + 0.2 + 0.3 * row, y + 1.01D, z + 0.1 + 0.3 * col);
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 0.0F);

                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

                    GL11.glPopMatrix();
                }
            }
        }
    }

}
