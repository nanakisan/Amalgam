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
import amalgam.common.fluid.ItemAmalgamContainer;
import amalgam.common.item.ItemStoneTongsFull;
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
		
		ItemAmalgamContainer stf = (ItemAmalgamContainer)Amalgam.stoneTongsFull;
		
		ItemStack stack = player.inventory.getCurrentItem();
		// here we decide what to do based on what item was used to activate the block
		if(stack == null){// if there was no item we print info to the chat
			TileStoneCrucible te = (TileStoneCrucible)world.getTileEntity(x, y, z);
			player.addChatMessage(new ChatComponentText(te.toString()));
			return true;
		}
		if(stack.getItem() == Amalgam.stoneTongs){ // if we used stone tongs we try to drain some amalgam from the crucible into the tongs
			// get the tile entity and attempt to drain 
			TileStoneCrucible te = (TileStoneCrucible)world.getTileEntity(x, y, z);
			AmalgamStack fluidStack = (AmalgamStack)te.drain(ForgeDirection.UNKNOWN, ItemStoneTongsFull.CAPACITY, true); //drain fluid from the crucible
			if(fluidStack != null){ // see if we drained anything
				ItemStack newStack = new ItemStack(Amalgam.stoneTongsFull); // create a new full stone tongs item if we drained something
				stf.fill(newStack, fluidStack, true); // fill the tongs with the drained amalgam
				player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);// set the player's held item to the new tongs
			}
			
		}else if(stack.getItem() == Amalgam.stoneTongsFull){
			// only fill the tank if there is enough room
			
			AmalgamStack newStack = (AmalgamStack)stf.drain(stack, stf.getCapacity(stack), true);
			TileStoneCrucible te = (TileStoneCrucible)world.getTileEntity(x, y, z);
			int filled = te.fill(ForgeDirection.UNKNOWN, newStack, true);
			if(filled < newStack.amount){
				// if we don't completely empty the tongs reduce the amalgam stored in the tongs
				newStack.amount-= filled;
				stf.fill(stack, newStack, true);
			}else{
				// if we completely empty the tongs switch to the empty tongs
				player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Amalgam.stoneTongs));
			}
		}else if(PropertyManager.itemIsAmalgable(stack)){ // next we see if the item is amalgable, if it is we make it into a fluid and add it to the crucible if we have space
			player.addChatMessage(new ChatComponentText("Item is amalgable"));
			TileStoneCrucible te = (TileStoneCrucible)world.getTileEntity(x, y, z);
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
				player.addChatMessage(new ChatComponentText("There is no space"));
				return false;
			}
			
		}else{
			player.addChatMessage(new ChatComponentText("Item is not amalgable"));
			return false;
		}
		return false;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_)
    {
        super.breakBlock(world, x, y, z, block, p_149749_6_);
        world.removeTileEntity(x, y, z);
    }

    public boolean onBlockEventReceived(World world, int x, int y, int z, int p_149749_5_, int p_149749_6_){
        super.onBlockEventReceived(world, x, y, z, p_149749_5_, p_149749_6_);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null ? tileentity.receiveClientEvent(p_149749_5_, p_149749_6_) : false;
    }

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new TileStoneCrucible();
	}
}
