package amalgam.common;

import net.minecraftforge.common.ForgeChunkManager;
import amalgam.common.network.PacketHandler;
import amalgam.common.properties.AmalgamPropertyManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = Amalgam.MODID, version = Amalgam.VERSION, guiFactory = "amalgam.client.gui.AmalgamGuiFactory")
public class Amalgam {

    public static final String MODID   = "amalgam";
    public static final String VERSION = "0.6.0";

    @Instance(MODID)
    public static Amalgam      instance;

    @SidedProxy(clientSide = "amalgam.client.ClientProxy", serverSide = "amalgam.common.CommonProxy")
    public static CommonProxy  proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init(event);

        // TODO create classes AmalgamItems, AmalgamBlocks, etc to handle registering items and blocks as well as
        // providing static references to items and blocks. Basically do some of the stuff that is done in the coConfig
        // class right now

        // TODO do per-world config files look at advanced health config by the betterstorage person to get ideas
        Config.registerItems();
        Config.registerBlocks();
        Config.registerFluids();

        AmalgamPropertyManager.captureConfig(event.getModConfigurationDirectory());

        Config.registerEntities();
        PacketHandler.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        FMLCommonHandler.instance().bus().register(Config.instance);
        Config.registerAmalgamProperties();
        Config.registerRecipes();

        FMLInterModComms.sendMessage("Waila", "register", "amalgam.common.WailaProvider.callbackRegister");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.registerRenderers();

        Config.removeVanillaRecipes();
        AmalgamPropertyManager.loadConfiguration();
    }

}