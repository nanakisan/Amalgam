package amalgam.common;

import amalgam.common.network.PacketHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = Amalgam.MODID, version = Amalgam.VERSION, guiFactory = "amalgam.client.gui.AmalgamGuiFactory")
public class Amalgam {

    public static final String MODID   = "amalgam";
    public static final String VERSION = "0.4.0";

    @Instance(MODID)
    public static Amalgam      instance;

    @SidedProxy(clientSide = "amalgam.client.ClientProxy", serverSide = "amalgam.common.CommonProxy")
    public static CommonProxy  proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init(event);

        Config.registerItems();
        Config.registerBlocks();
        Config.registerFluids();

        PacketHandler.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        FMLCommonHandler.instance().bus().register(Config.instance);
        Config.registerAmalgamProperties();
        Config.registerRecipes();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.registerRenderers();
    }

}