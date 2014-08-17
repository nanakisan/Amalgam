package amalgam.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import amalgam.common.Amalgam;
import amalgam.common.Config;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.IAmalgamContainerItem;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCastingTable extends BlockContainer implements ITileEntityProvider {

    private IIcon iconBottomSide;
    private IIcon iconBottom;
    private IIcon iconTop;
    private IIcon iconNeck;

    public BlockCastingTable() {
        super(Material.rock);
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypeStone);
        this.setCreativeTab(Config.tab);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.iconNeck = iconRegister.registerIcon("amalgam:castingTableNeck");
        this.iconTop = iconRegister.registerIcon("amalgam:castingTableTop");
        this.iconBottom = iconRegister.registerIcon("amalgam:castingTableBase");
        this.iconBottomSide = iconRegister.registerIcon("amalgam:castingTableBaseSide");
        this.blockIcon = iconRegister.registerIcon("amalgam:castingTableSide");
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iconTop : side == 0 ? this.iconBottom : side == 6 ? this.iconNeck : side == 7 ? this.iconBottomSide : this.blockIcon;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {

        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            player.openGui(Amalgam.instance, Config.CASTING_GUI_ID, world, x, y, z);
            return true;
        }

        TileCastingTable table = (TileCastingTable) world.getTileEntity(x, y, z);
        if (stack.getItem() instanceof IAmalgamContainerItem) {
            IAmalgamContainerItem container = (IAmalgamContainerItem) stack.getItem();
            if (player.isSneaking()) {
                int drainAmount = Math.min(container.getEmptySpace(stack), Config.BASE_AMOUNT);
                AmalgamStack fluidStack = (AmalgamStack) table.drain(ForgeDirection.UNKNOWN, drainAmount, true);
                if (fluidStack != null) {
                    int result = container.fill(stack, fluidStack, true);
                    fluidStack.amount -= result;
                    if (fluidStack.amount > 0) {
                        table.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                    }
                }
            } else if (container.getFluid(stack).amount == 0) {
                AmalgamStack fluidStack = (AmalgamStack) table.drain(ForgeDirection.UNKNOWN, container.getEmptySpace(stack), true);
                if (fluidStack != null) {
                    int result = container.fill(stack, fluidStack, true);
                    fluidStack.amount -= result;
                    if (fluidStack.amount > 0) {
                        table.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                    }
                }
            } else {
                AmalgamStack newStack = (AmalgamStack) container.drain(stack, container.getCapacity(stack), true);
                int filled = table.fill(ForgeDirection.UNKNOWN, newStack, true);
                newStack.amount -= filled;
                container.fill(stack, newStack, true);
            }
            return true;
        }

        player.openGui(Amalgam.instance, Config.CASTING_GUI_ID, world, x, y, z);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metaData) {
        return new TileCastingTable();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metaData) {
        TileCastingTable table = (TileCastingTable) world.getTileEntity(x, y, z);
        if (table != null) {
            table.emptyTank();
        }
        super.breakBlock(world, x, y, z, block, metaData);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean isToolEffective(String type, int metadata) {
        if ("pickaxe".equals(type)) {
            return true;
        }

        return false;
    }

    @Override
    public int getRenderType() {
        return Config.castingTableRID;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

}
