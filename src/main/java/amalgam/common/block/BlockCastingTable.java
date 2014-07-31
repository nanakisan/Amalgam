package amalgam.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import amalgam.common.Amalgam;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.IAmalgamContainerItem;
import amalgam.common.tile.TileCastingTable;

public class BlockCastingTable extends BlockContainer implements ITileEntityProvider {

    public BlockCastingTable() {
        super(Material.rock);
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setStepSound(soundTypeStone);
        getCreativeTabToDisplayOn();
        this.setCreativeTab(Amalgam.tab);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float px, float py, float pz) {
        if (world.isRemote) {
            return true;
        }

        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            player.openGui(Amalgam.instance, Amalgam.CASTING_GUI_ID, world, x, y, z);
            return false;
        }

        TileCastingTable te = (TileCastingTable) world.getTileEntity(x, y, z);
        if (stack.getItem() instanceof IAmalgamContainerItem) {
            IAmalgamContainerItem container = (IAmalgamContainerItem) stack.getItem();
            if (player.isSneaking()) {
                int drainAmount = Math.min(container.getEmptySpace(stack), Amalgam.BASEAMOUNT);
                AmalgamStack fluidStack = (AmalgamStack) te.drain(ForgeDirection.UNKNOWN, drainAmount, true);
                if (fluidStack != null) {
                    int result = container.fill(stack, fluidStack, true);
                    fluidStack.amount -= result;
                    if (fluidStack.amount > 0) {
                        te.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                    }
                }
            } else if (container.getFluid(stack).amount == 0) {
                AmalgamStack fluidStack = (AmalgamStack) te.drain(ForgeDirection.UNKNOWN, container.getEmptySpace(stack), true);
                if (fluidStack != null) {
                    int result = container.fill(stack, fluidStack, true);
                    fluidStack.amount -= result;
                    if (fluidStack.amount > 0) {
                        te.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                    }
                }
            } else {
                AmalgamStack newStack = (AmalgamStack) container.drain(stack, container.getCapacity(stack), true);
                int filled = te.fill(ForgeDirection.UNKNOWN, newStack, true);
                newStack.amount -= filled;
                container.fill(stack, newStack, true);
            }
            return true;
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metaData) {
        return new TileCastingTable();
    }

    @Override
    // the metadata variable might actually be the block side, not sure
    public void breakBlock(World world, int x, int y, int z, Block block, int metaData) {
        TileCastingTable t = (TileCastingTable) world.getTileEntity(x, y, z);
        if (t != null) {
            t.emptyTank();
        }
        super.breakBlock(world, x, y, z, block, metaData);
        world.removeTileEntity(x, y, z);
    }
}
