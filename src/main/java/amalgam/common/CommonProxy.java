package amalgam.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import amalgam.common.container.ContainerCasting;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == Config.CASTING_GUI_ID) {
            TileEntity te = world.getTileEntity(x, y, z);
            return new ContainerCasting(player.inventory, (TileCastingTable) te);
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

    public void registerRenderers() {

    }

}
