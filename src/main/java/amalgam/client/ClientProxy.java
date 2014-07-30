package amalgam.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import amalgam.client.gui.GuiCasting;
import amalgam.common.Amalgam;
import amalgam.common.CommonProxy;
import amalgam.common.container.ContainerCastingTable;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.client.FMLClientHandler;

public class ClientProxy extends CommonProxy {

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

        if (id == Amalgam.CASTING_GUI_ID) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            return new GuiCasting(new ContainerCastingTable(player.inventory, (TileCastingTable) tileEntity));
        }
        return null;
    }

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
