package amalgam.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import amalgam.common.Amalgam;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.IAmalgamContainerItem;
import amalgam.common.tile.TileCastingTable;

public class BlockCastingTable extends BlockContainer implements ITileEntityProvider{

	public BlockCastingTable() {
		super(Material.rock);
		this.setHardness(3.0F);
		this.setResistance(5.0F);
		this.setStepSound(soundTypeStone);
		getCreativeTabToDisplayOn();
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float px, float py, float pz){
		if(world.isRemote){
			return true;
		}

		ItemStack stack = player.inventory.getCurrentItem();
		TileCastingTable te = (TileCastingTable)world.getTileEntity(x, y, z);
		// here we decide what to do based on what item was used to activate the block
		if(stack == null){// if there was no item we print info to the chat
			// player.addChatMessage(new ChatComponentText(te.tank.toString()));
			player.openGui(Amalgam.instance, Amalgam.CASTING_GUI_ID, world, x, y, z);
			return false;
		}
		if(stack.getItem() instanceof IAmalgamContainerItem){
			IAmalgamContainerItem container = (IAmalgamContainerItem) stack.getItem();
			// if we are sneaking or the tongs are empty we attempt to drain the castingTable, otherwise we attempt to fill
			if(player.isSneaking()){
				// if we are sneaking try to drain just a little bit
				int drainAmount = Math.min(container.getEmptySpace(stack), Amalgam.BASEAMOUNT);
				AmalgamStack fluidStack = (AmalgamStack)te.drain(ForgeDirection.UNKNOWN, drainAmount, true); //drain fluid from the crucible
				if(fluidStack != null){ // see if we drained anything
					int result = container.fill(stack, fluidStack, true); // fill the tongs with the drained amalgam
					fluidStack.amount -= result;
					if(fluidStack.amount > 0){
						te.fill(ForgeDirection.UNKNOWN, fluidStack, true);
					}
				}
			}else if(container.getFluid(stack).amount == 0){
				// if we are not sneaking try to drain as much as possible
				AmalgamStack fluidStack = (AmalgamStack)te.drain(ForgeDirection.UNKNOWN, container.getEmptySpace(stack), true); //drain fluid from the crucible
				if(fluidStack != null){ // see if we drained anything
					int result = container.fill(stack, fluidStack, true); // fill the tongs with the drained amalgam
					fluidStack.amount -= result;
					if(fluidStack.amount > 0){
						te.fill(ForgeDirection.UNKNOWN, fluidStack, true);
					}
				}
			}else{
				AmalgamStack newStack = (AmalgamStack)container.drain(stack, container.getCapacity(stack), true);
				int filled = te.fill(ForgeDirection.UNKNOWN, newStack, true);
				newStack.amount -= filled;
				container.fill(stack, newStack, true);
			}
		
	    	return true;
		}
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new TileCastingTable();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_)
    {
        TileCastingTable t = (TileCastingTable) world.getTileEntity(x, y, z);
        if(t!=null) t.emptyTank();
        super.breakBlock(world, x, y, z, block, p_149749_6_);
        world.removeTileEntity(x, y, z);
    }
}
