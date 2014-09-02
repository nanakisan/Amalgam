package amalgam.common.network;

import amalgam.common.Amalgam;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Amalgam.MODID);

    private PacketHandler() {
    }

    public static void init() {
        INSTANCE.registerMessage(PacketSyncAmalgamTank.class, PacketSyncAmalgamTank.class, 0, Side.CLIENT);
    }

}
