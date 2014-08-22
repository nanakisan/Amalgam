package amalgam.client.renderers;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import amalgam.common.Config;
import amalgam.common.block.BlockStoneCrucible;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import amalgam.common.tile.TileStoneCrucible;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CrucibleRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
        GL11.glPushMatrix();
        float height = ((TileStoneCrucible) te).getFluidHeight();
        GL11.glTranslated(x, y + height, z + 1.0D);
        GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
        PropertyList p = ((TileStoneCrucible) te).getAmalgamProperties();

        Color color = new Color((int) p.getValue(PropertyManager.COLOR));

        GL11.glColor3f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);
        // Config.LOG.info("color: " + color.getRed());

        if (height > .301) {

            IIcon iicon = ((BlockStoneCrucible) Config.stoneCrucible).liquidAmalgam;
            // IIcon iicon = ((BlockStoneCrucible) Config.stoneCrucible).solidAmalgam;

            if (!((TileStoneCrucible) te).isHot()) {
                iicon = ((BlockStoneCrucible) Config.stoneCrucible).solidAmalgam;
            }

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

            Tessellator tessellator = Tessellator.instance;

            float f1 = iicon.getMaxU();
            float f2 = iicon.getMinV();
            float f3 = iicon.getMinU();
            float f4 = iicon.getMaxV();

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, f1, f4);
            tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, f3, f4);
            tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, f3, f2);
            tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, f1, f2);
            tessellator.draw();
        }

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);

        IIcon side = ((BlockStoneCrucible) block).getBlockTextureFromSide(2);
        IIcon bottom = ((BlockStoneCrucible) block).getBlockTextureFromSide(6);
        Tessellator t = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        t.startDrawingQuads();
        t.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 1.0F - 0.75F, 0.0D, bottom);

        t.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, -1.0F + 0.25F, 0.0D, bottom);

        t.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, side);
        renderer.renderFaceXNeg(block, 1.0D, 0.0D, 0.0D, side);

        t.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, side);
        renderer.renderFaceXPos(block, -1.0D, 0.0D, 0.0D, side);

        t.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, side);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 1.0D, side);

        t.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, side);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, -1.0D, side);

        t.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);

        IIcon innerSide = ((BlockStoneCrucible) block).getBlockTextureFromSide(2);
        IIcon bottom = ((BlockStoneCrucible) block).getBlockTextureFromSide(6);
        float f5 = 0.123F;

        renderer.renderFaceXPos(block, x - 1.0F + f5, y, z, innerSide);
        renderer.renderFaceXNeg(block, x + 1.0F - f5, y, z, innerSide);
        renderer.renderFaceZPos(block, x, y, z - 1.0F + f5, innerSide);
        renderer.renderFaceZNeg(block, x, y, z + 1.0F - f5, innerSide);
        renderer.renderFaceYPos(block, x, y - 1.0F + 0.25F, z, bottom);
        renderer.renderFaceYNeg(block, x, y + 1.0F - 0.75F, z, bottom);

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
        return Config.crucibleRID;
    }

}
