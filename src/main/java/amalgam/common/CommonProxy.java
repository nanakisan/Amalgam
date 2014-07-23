package amalgam.common;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import amalgam.common.container.ContainerCastingTable;
import amalgam.common.tile.TileCastingTable;

public class CommonProxy implements IGuiHandler{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Amalgam.log.error("in getting the serverGuiElement");
		
		if(ID == Amalgam.CASTING_GUI_ID){
			Amalgam.log.error("in getting the serverGuiElement: good id");
			TileEntity te = world.getTileEntity(x, y, z);
			return new ContainerCastingTable(player.inventory, (TileCastingTable)te);
		}
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Amalgam.log.error("in getting the clientGuiElement");
		return null;
	}

}
