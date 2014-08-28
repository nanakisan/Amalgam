package amalgam.common.block;

import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import amalgam.common.Config;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.IAmalgamContainerItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import amalgam.common.tile.TileStoneCrucible;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStoneCrucible extends AbstractBlockAmalgamContainer implements ITileEntityProvider {

    @SideOnly(Side.CLIENT)
    private IIcon iconInner;
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    public IIcon  liquidAmalgam;
    @SideOnly(Side.CLIENT)
    public IIcon  solidAmalgam;

    public BlockStoneCrucible() {
        super(Material.iron);
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypeStone);
        this.setCreativeTab(Config.tab);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iconTop : side == 0 ? this.iconBottom : side == 6 ? this.iconInner : this.blockIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.iconInner = iconRegister.registerIcon("amalgam:stoneCrucibleInner");
        this.iconTop = iconRegister.registerIcon("amalgam:stoneCrucibleTop");
        this.iconBottom = iconRegister.registerIcon("amalgam:stoneCrucibleBottom");
        this.blockIcon = iconRegister.registerIcon("amalgam:stoneCrucibleSide");
        this.liquidAmalgam = iconRegister.registerIcon("amalgam:amalgamStill");
        this.solidAmalgam = iconRegister.registerIcon("amalgam:amalgamSolid");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return Config.crucibleRID;
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getAmalgamIcon(boolean isSolid) {
        if (isSolid) {
            return ((BlockStoneCrucible) Config.stoneCrucible).solidAmalgam;
        }

        return ((BlockStoneCrucible) Config.stoneCrucible).liquidAmalgam;
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);

        this.setBlockBoundsForItemRender();
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof TileStoneCrucible) {
            TileStoneCrucible cruc = (TileStoneCrucible) te;

            if (cruc.isHot()) {

                if (entity instanceof EntityItem) {
                    ItemStack stack = ((EntityItem) entity).getEntityItem();
                    if (PropertyManager.itemIsAmalgable(stack)) {
                        interactWithAmalgableItem(cruc, stack);
                        return;
                    }
                }

                if (cruc.getRenderLiquidLevel() > .301) {
                    entity.setFire(3);
                }
            }
        }
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int side, int metaData) {
        super.onBlockEventReceived(world, x, y, z, side, metaData);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity == null ? false : tileentity.receiveClientEvent(side, metaData);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metaData) {
        return new TileStoneCrucible();
    }

    @Override
    public boolean isToolEffective(String type, int metadata) {
        if ("pickaxe".equals(type)) {
            return true;
        }

        return false;
    }

    @Override
    protected void interactWithAmalgableItem(TileEntity te, ItemStack stack) {
        if (!(te instanceof TileStoneCrucible)) {
            return;
        }

        TileStoneCrucible crucible = (TileStoneCrucible) te;

        if (!crucible.isHot()) {
            return;
        }

        int amount = PropertyManager.getVolume(stack);

        if (amount > 0 && amount <= crucible.getEmptySpace()) {
            PropertyList amalgProperties = PropertyManager.getProperties(stack);
            AmalgamStack amalg = new AmalgamStack(amount, amalgProperties);

            if (amalgProperties == null) {
                Config.LOG.error("No properties!!!!!");
            }

            crucible.fill(ForgeDirection.UNKNOWN, amalg, true);
            stack.stackSize = stack.stackSize - 1;

            te.getWorldObj().notifyBlocksOfNeighborChange(te.xCoord, te.yCoord, te.zCoord, this);
            return;
        }
    }

    @Override
    protected void interactWithAmalgamContainerItem(TileEntity te, IAmalgamContainerItem container, ItemStack stack, EntityPlayer player) {
        if (te instanceof TileStoneCrucible && ((TileStoneCrucible) te).isHot()) {
            super.interactWithAmalgamContainerItem(te, container, stack, player);
            te.getWorldObj().notifyBlocksOfNeighborChange(te.xCoord, te.yCoord, te.zCoord, this);
        }
    }

    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int meta) {
        TileStoneCrucible crucible = (TileStoneCrucible) world.getTileEntity(x, y, z);
        return (int) (15.0F * crucible.getFluidVolume() / crucible.getTankCapacity());
    }
}
