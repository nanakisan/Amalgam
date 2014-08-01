package amalgam.common.network;

import java.util.Locale;

import amalgam.common.Amalgam;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Amalgam.MODID.toLowerCase(Locale.US));

    private PacketHandler() {
    }

    public static void init() {
        INSTANCE.registerMessage(PacketSyncCastingTank.class, PacketSyncCastingTank.class, 0, Side.CLIENT);
    }

}
