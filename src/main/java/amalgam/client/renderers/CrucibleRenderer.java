package amalgam.client.renderers;

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
import amalgam.common.tile.TileStoneCrucible;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CrucibleRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
        GL11.glPushMatrix();
        float height = ((TileStoneCrucible) te).getFluidHeight();
        GL11.glTranslated(x, y + height, z + 1.0D);
        GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
        if (height - .001 > .3) {

            IIcon iicon = ((BlockStoneCrucible) Config.stoneCrucible).liquidAmalgam;

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

        GL11.glPopMatrix();
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        // FIXME render the crucible in the inventory

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
        // TODO change this to true once we get the inventory rendering code working
        return false;
    }

    @Override
    public int getRenderId() {
        return Config.crucibleRID;
    }

}
