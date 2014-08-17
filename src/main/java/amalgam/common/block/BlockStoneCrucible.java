package amalgam.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
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

public class BlockStoneCrucible extends Block implements ITileEntityProvider {

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

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iconTop : side == 0 ? this.iconBottom : side == 6 ? this.iconInner : this.blockIcon;
    }

    @Override
    public int getRenderType() {
        return Config.crucibleRID;
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

    public static IIcon getAmalgamIcon(boolean isSolid) {
        if (isSolid) {
            return ((BlockStoneCrucible) Config.stoneCrucible).solidAmalgam;
        }
        return ((BlockStoneCrucible) Config.stoneCrucible).liquidAmalgam;
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
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

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        // TODO if there is heat set entity on fire if it is a mob, add entity to amalgam if it can be added, burn up
        // otherwise
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

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {

        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            return true;
        }

        TileStoneCrucible crucible = (TileStoneCrucible) world.getTileEntity(x, y, z);
        if (!crucible.isHot()) {
            // player.addChatMessage(new ChatComponentText("The crucible is cold, put a heat source under it."));
            return true;
        }

        if (stack.getItem() instanceof IAmalgamContainerItem) {
            IAmalgamContainerItem container = (IAmalgamContainerItem) stack.getItem();
            if (player.isSneaking()) {
                int drainAmount = Math.min(container.getEmptySpace(stack), Config.BASE_AMOUNT);
                AmalgamStack fluidStack = (AmalgamStack) crucible.drain(ForgeDirection.UNKNOWN, drainAmount, true);
                if (fluidStack != null) { // see if we drained anything
                    int result = container.fill(stack, fluidStack, true);
                    fluidStack.amount -= result;
                    if (fluidStack.amount > 0) {
                        crucible.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                    }
                }
            } else if (container.getFluid(stack).amount == 0) {
                AmalgamStack fluidStack = (AmalgamStack) crucible.drain(ForgeDirection.UNKNOWN, container.getEmptySpace(stack), true);
                if (fluidStack != null) {
                    int result = container.fill(stack, fluidStack, true);
                    fluidStack.amount -= result;
                    if (fluidStack.amount > 0) {
                        crucible.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                    }
                }
            } else {
                AmalgamStack newStack = (AmalgamStack) container.drain(stack, container.getCapacity(stack), true);
                int filled = crucible.fill(ForgeDirection.UNKNOWN, newStack, true);
                newStack.amount -= filled;
                container.fill(stack, newStack, true);
            }
            return true;
        }

        if (PropertyManager.itemIsAmalgable(stack)) {

            int amount = PropertyManager.getVolume(stack);
            if (amount > 0 && amount < crucible.getEmptySpace()) {

                PropertyList amalgProperties = PropertyManager.getProperties(stack);
                AmalgamStack amalg = new AmalgamStack(amount, amalgProperties);

                if (amalgProperties == null) {
                    Config.LOG.error("No properties!!!!!");
                }

                crucible.fill(ForgeDirection.UNKNOWN, amalg, true);

                stack.stackSize = stack.stackSize - 1;
                // player.addChatMessage(new ChatComponentText("The item melts into amalgam"));

                return true;
            } else {
                // player.addChatMessage(new
                // ChatComponentText("The crucible is about to overflow, you shouldn't add more."));
                return false;
            }

        } else {
            // player.addChatMessage(new
            // ChatComponentText("Adding this item will ruin your amalgam, try using a different material"));
            return false;
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metaData) {// or
                                                                                         // side?
        TileStoneCrucible crucible = (TileStoneCrucible) world.getTileEntity(x, y, z);
        if (crucible != null) {
            crucible.emptyTank();
        }
        super.breakBlock(world, x, y, z, block, metaData);
        world.removeTileEntity(x, y, z);
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
}
