package amalgam.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import amalgam.client.gui.GuiCasting;
import amalgam.common.CommonProxy;
import amalgam.common.Config;
import amalgam.common.CrucibleSpecialRenderer;
import amalgam.common.container.ContainerCasting;
import amalgam.common.tile.TileCastingTable;
import amalgam.common.tile.TileStoneCrucible;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z) {

        if (guiID == Config.CASTING_GUI_ID) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            return new GuiCasting(new ContainerCasting(player.inventory, (TileCastingTable) tileEntity));
        }
        return null;
    }

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }

    @Override
    public void registerRenderers() {
        CrucibleSpecialRenderer r = new CrucibleSpecialRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileStoneCrucible.class, r);
        RenderingRegistry.registerBlockHandler(100, r);
    }
}
