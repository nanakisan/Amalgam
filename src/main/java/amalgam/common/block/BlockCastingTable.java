package amalgam.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import amalgam.common.Amalgam;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;

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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float px, float py, float pz){
		Amalgam.log.error("clicking");
		FMLNetworkHandler.openGui(player, Amalgam.instance, Amalgam.CASTING_GUI_ID, world, x, y, z);
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new TileCastingTable();
	}
}
