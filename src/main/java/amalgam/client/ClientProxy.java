package amalgam.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import amalgam.client.gui.GuiCasting;
import amalgam.common.Amalgam;
import amalgam.common.CommonProxy;
import amalgam.common.container.ContainerCastingTable;
import amalgam.common.tile.TileCastingTable;

public class ClientProxy extends CommonProxy{

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Amalgam.log.error("in getting the clientGuiElement");
		
		if(ID == Amalgam.CASTING_GUI_ID){
			Amalgam.log.error("in getting the clientGuiElement: good id");
			TileEntity te = world.getTileEntity(x, y, z);
			return new GuiCasting(new ContainerCastingTable(player.inventory, (TileCastingTable)te));
		}
		return null;
	}
}
