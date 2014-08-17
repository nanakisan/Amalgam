package amalgam.client.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import amalgam.common.Config;
import amalgam.common.block.BlockCastingTable;
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
    }

}
