package amalgam.client.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import amalgam.common.Config;
import amalgam.common.block.BlockCastingTable;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CastingTableRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);

        IIcon top = ((BlockCastingTable) block).getBlockTextureFromSide(1);
        IIcon side = ((BlockCastingTable) block).getBlockTextureFromSide(2);
        IIcon base = ((BlockCastingTable) block).getBlockTextureFromSide(0);
        IIcon baseSide = ((BlockCastingTable) block).getBlockTextureFromSide(7);
        IIcon neck = ((BlockCastingTable) block).getBlockTextureFromSide(6);

        Tessellator t = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        t.startDrawingQuads();
        t.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, base);
        renderer.renderFaceYNeg(block, 0.0D, +1.0F - 0.375F, 0.0D, top);

        t.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, top);
        renderer.renderFaceYPos(block, 0.0D, -1.0F + 0.25F, 0.0D, base);

        t.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, side);
        renderer.renderFaceXNeg(block, 1.0F - .875, 0.0D, 0.0D, baseSide);
        renderer.renderFaceXNeg(block, 0 + 1.0F - .6875, 0, 0, neck);

        t.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, side);
        renderer.renderFaceXPos(block, -1.0F + .875, 0.0, 0.0, baseSide);
        renderer.renderFaceXPos(block, -1.0F + .6875, 0, 0, neck);

        t.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, side);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 1.0F - .875, baseSide);
        renderer.renderFaceZNeg(block, 0, 0, +1.0F - .6875, neck);

        t.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, side);
        renderer.renderFaceZPos(block, 0, 0, -1.0F + .875, baseSide);
        renderer.renderFaceZPos(block, 0, 0, -1.0F + .6875, neck);

        t.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
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
        return true;
    }

    @Override
    public int getRenderId() {
        return Config.castingTableRID;
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {

        if (!Config.advancedRendering) {
            return;
        }

        TileCastingTable table = (TileCastingTable) te;

        ItemStack stack = table.getStackInSlot(9);
        if (stack == null) {
            return;
        }

        GL11.glPushMatrix();

        if (Config.floatingCastResult) {
            long time = te.getWorldObj().getWorldTime();
            GL11.glTranslated(x + 0.5, y + 1.1D, z + 0.5);
            GL11.glRotatef(time * 1.5F, 0.0F, 90.0F, 0.0F);
        } else {
            GL11.glTranslated(x + 0.5F, y + 1.05D, z + 0.25);
            GL11.glRotatef(90.0F, 90.0F, 0.0F, 0.0F);
        }

        GL11.glScalef(1.25F, 1.25F, 1.25F);

        if (!table.tankIsFull()) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_DST_ALPHA);
        }

        if (Block.getBlockFromItem(stack.getItem()) == Blocks.air) {

            EntityItem itemEntity = new EntityItem(te.getWorldObj(), te.xCoord, te.yCoord + 1.2, te.zCoord, stack);
            itemEntity.hoverStart = 0.0F;
            RenderManager.instance.renderEntityWithPosYaw(itemEntity, 0.0, 0.0, 0.0, 0.0F, 0.0F);

        } else {
            GL11.glTranslated(0.0F, 0.5F, 0.0F);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            RenderBlocks.getInstance().renderBlockAsItem(Block.getBlockFromItem(stack.getItem()), 0, 1.0F);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

    }

}
