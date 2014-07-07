package amalgam.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import amalgam.common.Amalgam;
import amalgam.common.fluid.ItemAmalgamContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStoneTongsFull extends ItemAmalgamContainer{

	public static int CAPACITY = Amalgam.BASEAMOUNT * 10;
	public ItemStoneTongsFull(){
		super(CAPACITY);
		this.setCreativeTab(CreativeTabs.tabTools);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		this.itemIcon = iconRegister.registerIcon("amalgam:stoneTongsFull");
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int p_77648_7_, float fx, float fy, float fz){
        // Amalgam.log.error("onItemUse");
		
		return false;
    }
	
	@Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
		ItemStack droppedBlob = new ItemStack(Amalgam.amalgamBlob, 1);
		ItemAmalgamContainer blob = (ItemAmalgamContainer)Amalgam.amalgamBlob;
		blob.fill(droppedBlob, this.drain(stack, CAPACITY, true), true);
		
		if (!world.isRemote){
        	player.entityDropItem(droppedBlob, 1);
        }
		
		ItemStack emptyTongs = new ItemStack(Amalgam.stoneTongs);
        return emptyTongs;
    }
}
