package amalgam.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import amalgam.common.Config;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.IAmalgamContainerItem;
import amalgam.common.properties.PropertyManager;
import amalgam.common.tile.AbstractTileAmalgamContainer;

public abstract class AbstractBlockAmalgamContainer extends Block implements ITileEntityProvider {

    protected AbstractBlockAmalgamContainer(Material mat) {
        super(mat);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metaData) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof AbstractTileAmalgamContainer) {
            ((AbstractTileAmalgamContainer) te).emptyTank();
        }

        super.breakBlock(world, x, y, z, block, metaData);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.inventory.getCurrentItem();

        if (stack == null) {
            return true;
        }

        TileEntity te = world.getTileEntity(x, y, z);

        if (PropertyManager.itemIsAmalgable(stack)) {
            interactWithAmalgableItem(te, stack);
            return true;
        }

        Item stackItem = stack.getItem();

        if (stackItem instanceof IAmalgamContainerItem) {
            interactWithAmalgamContainerItem(te, (IAmalgamContainerItem) stackItem, stack, player);
            return true;
        }

        return false;
    }

    protected abstract void interactWithAmalgableItem(TileEntity te, ItemStack stack);

    protected void interactWithAmalgamContainerItem(TileEntity te, IAmalgamContainerItem container, ItemStack stack, EntityPlayer player) {
        if (!(te instanceof AbstractTileAmalgamContainer)) {
            return;
        }

        AbstractTileAmalgamContainer handler = (AbstractTileAmalgamContainer) te;
        if (player.isSneaking()) {
            int drainAmount = Math.min(container.getEmptySpace(stack), Config.BASE_AMOUNT);
            AmalgamStack fluidStack = (AmalgamStack) handler.drain(ForgeDirection.UNKNOWN, drainAmount, true);

            if (fluidStack != null) {
                int result = container.fill(stack, fluidStack, true);
                fluidStack.amount -= result;

                if (fluidStack.amount > 0) {
                    handler.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                }
            }
        } else if (container.getFluid(stack).amount == 0) {
            AmalgamStack fluidStack = (AmalgamStack) handler.drain(ForgeDirection.UNKNOWN, container.getEmptySpace(stack), true);

            if (fluidStack != null) {
                int result = container.fill(stack, fluidStack, true);
                fluidStack.amount -= result;

                if (fluidStack.amount > 0) {
                    handler.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                }
            }
        } else {
            AmalgamStack newStack = (AmalgamStack) container.drain(stack, container.getCapacity(stack), true);
            int filled = handler.fill(ForgeDirection.UNKNOWN, newStack, true);
            newStack.amount -= filled;
            container.fill(stack, newStack, true);
        }
    }
}