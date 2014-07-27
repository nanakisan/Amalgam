package amalgam.common.network;

import amalgam.common.Amalgam;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Amalgam.MODID.toLowerCase());

	public static void init(){
		INSTANCE.registerMessage(PacketSyncCastingSlot.class, PacketSyncCastingSlot.class, 0, Side.SERVER);
		INSTANCE.registerMessage(PacketSyncCastingTank.class, PacketSyncCastingTank.class, 1, Side.CLIENT);
	}
}
