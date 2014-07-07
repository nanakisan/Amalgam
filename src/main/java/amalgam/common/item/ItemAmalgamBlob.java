package amalgam.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import amalgam.common.Amalgam;
import amalgam.common.fluid.ItemAmalgamContainer;

public class ItemAmalgamBlob extends ItemAmalgamContainer{
	public static int CAPACITY = Amalgam.BASEAMOUNT * 10;
	public ItemAmalgamBlob(){
		super(CAPACITY);
		this.setCreativeTab(CreativeTabs.tabTools);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		this.itemIcon = iconRegister.registerIcon("amalgam:amalgamBlob");
	}
}

