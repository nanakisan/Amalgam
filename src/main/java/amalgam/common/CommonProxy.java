package amalgam.common;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import amalgam.common.container.ContainerCastingTable;
import amalgam.common.tile.TileCastingTable;

public class CommonProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == Amalgam.CASTING_GUI_ID) {
            TileEntity te = world.getTileEntity(x, y, z);
            return new ContainerCastingTable(player.inventory, (TileCastingTable) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public World getClientWorld() {
        return null;
    }
}
