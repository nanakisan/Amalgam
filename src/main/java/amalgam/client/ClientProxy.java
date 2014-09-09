package amalgam.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import amalgam.client.gui.GuiCasting;
import amalgam.client.renderers.CastingTableRenderer;
import amalgam.client.renderers.CrucibleRenderer;
import amalgam.client.renderers.RenderAmalgamPotato;
import amalgam.common.CommonProxy;
import amalgam.common.Config;
import amalgam.common.container.ContainerCasting;
import amalgam.common.entity.EntityAmalgamPotato;
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
        Config.castingTableRID = RenderingRegistry.getNextAvailableRenderId();
        Config.crucibleRID = RenderingRegistry.getNextAvailableRenderId();

        CrucibleRenderer crucibleRenderer = new CrucibleRenderer();
        CastingTableRenderer castingTableRenderer = new CastingTableRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileStoneCrucible.class, crucibleRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileCastingTable.class, castingTableRenderer);
        RenderingRegistry.registerBlockHandler(crucibleRenderer);
        RenderingRegistry.registerBlockHandler(castingTableRenderer);

        RenderingRegistry.registerEntityRenderingHandler(EntityAmalgamPotato.class, new RenderAmalgamPotato());
    }
}
