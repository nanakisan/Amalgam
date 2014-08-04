package amalgam.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import amalgam.common.Amalgam;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.IAmalgamContainerItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import amalgam.common.tile.TileStoneCrucible;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStoneCrucible extends Block implements ITileEntityProvider {

    public BlockStoneCrucible() {
        super(Material.rock);
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypeStone);
        getCreativeTabToDisplayOn();
        this.setCreativeTab(Amalgam.tab);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon("amalgam:stoneCrucible");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            return true;
        }

        TileStoneCrucible crucible = (TileStoneCrucible) world.getTileEntity(x, y, z);
        if (stack.getItem() instanceof IAmalgamContainerItem) {
            IAmalgamContainerItem container = (IAmalgamContainerItem) stack.getItem();
            if (player.isSneaking()) {
                int drainAmount = Math.min(container.getEmptySpace(stack), Amalgam.BASE_AMOUNT);
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
            player.addChatMessage(new ChatComponentText("Item is amalgable"));
            int amount = PropertyManager.getVolume(stack);
            if (amount > 0 && amount < crucible.getEmptySpace()) {
                PropertyList amalgProperties = PropertyManager.getProperties(stack);
                AmalgamStack amalg = new AmalgamStack(amount, amalgProperties);

                if (amalgProperties == null) {
                    Amalgam.LOG.error("No properties!!!!!");
                }

                crucible.fill(ForgeDirection.UNKNOWN, amalg, true);

                if (stack.stackSize == 1) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                } else {
                    stack.stackSize = stack.stackSize - 1;
                }
                return true;
            } else {
                player.addChatMessage(new ChatComponentText("The crucible is about to overflow, you shouldn't add more."));
                return false;
            }

        } else {
            player.addChatMessage(new ChatComponentText("Item is not amalgable"));
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
}
