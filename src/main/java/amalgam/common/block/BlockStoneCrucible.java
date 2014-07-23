package amalgam.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
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

public class BlockStoneCrucible extends Block implements ITileEntityProvider{

	public BlockStoneCrucible() {
		super(Material.rock);
		this.setHardness(3.0F);
		this.setResistance(5.0F);
		this.setStepSound(soundTypeStone);
		getCreativeTabToDisplayOn();
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon("amalgam:stoneCrucible");
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float px, float py, float pz){
		if(world.isRemote){
			return true;
		}

		ItemStack stack = player.inventory.getCurrentItem();
		TileStoneCrucible te = (TileStoneCrucible)world.getTileEntity(x, y, z);
		// here we decide what to do based on what item was used to activate the block
		if(stack == null){// if there was no item we print info to the chat
			player.addChatMessage(new ChatComponentText(te.toString()));
			return true;
		}
		if(stack.getItem() instanceof IAmalgamContainerItem){
			IAmalgamContainerItem container = (IAmalgamContainerItem) stack.getItem();
			// if we are sneaking or the tongs are empty we attempt to drain the crucible, otherwise we attempt to fill
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
		if(PropertyManager.itemIsAmalgable(stack)){ // next we see if the item is amalgable, if it is we make it into a fluid and add it to the crucible if we have space
			player.addChatMessage(new ChatComponentText("Item is amalgable"));
			int amount = PropertyManager.getVolume(stack);
			
			if(amount > 0 && amount < te.getEmptySpace()){ // make sure we have space before we add it to the crucible
				PropertyList amalgProperties = PropertyManager.getProperties(stack);
				AmalgamStack amalg = new AmalgamStack(amount, amalgProperties); 
				
				if(amalgProperties == null){
					Amalgam.log.error("No properties!!!!!");
				}
				
				// fill the crucible
				te.fill(ForgeDirection.UNKNOWN, amalg, true);
				
				// decrement stack size
				if(stack.stackSize == 1){
					// set stack to null
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				}else{
					stack.stackSize = stack.stackSize - 1;
				}
				return true;
			}else{
				player.addChatMessage(new ChatComponentText("The crucible is about to overflow, you shouldn't add more."));
				return false;
			}
			
		}else{
			player.addChatMessage(new ChatComponentText("Item is not amalgable"));
			return false;
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_)
    {
        super.breakBlock(world, x, y, z, block, p_149749_6_);
        world.removeTileEntity(x, y, z);
    }

	@Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int p_149749_5_, int p_149749_6_){
        super.onBlockEventReceived(world, x, y, z, p_149749_5_, p_149749_6_);
        // TODO drop all amalgam inside the container
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null ? tileentity.receiveClientEvent(p_149749_5_, p_149749_6_) : false;
    }

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new TileStoneCrucible();
	}
}
