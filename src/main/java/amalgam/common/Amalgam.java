package amalgam.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import amalgam.common.block.BlockCastingTable;
import amalgam.common.block.BlockStoneCrucible;
import amalgam.common.casting.CastingManager;
import amalgam.common.casting.ICastItem;
import amalgam.common.item.ItemAmalgamBlob;
import amalgam.common.item.ItemAmalgamSword;
import amalgam.common.item.ItemStoneTongs;
import amalgam.common.network.PacketHandler;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import amalgam.common.tile.TileCastingTable;
import amalgam.common.tile.TileStoneCrucible;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = Amalgam.MODID, version = Amalgam.VERSION)
public class Amalgam {

    public static final String MODID          = "amalgam";
    public static final String VERSION        = "0.0.1";
    public static final int    BASE_AMOUNT    = 10;
    public static final int    INGOT_AMOUNT   = BASE_AMOUNT * 9;
    public static final Logger LOG            = LogManager.getLogger(MODID);
    public static final int    CASTING_GUI_ID = 1;

    @Instance(MODID)
    public static Amalgam      instance;

    @SidedProxy(clientSide = "amalgam.client.ClientProxy", serverSide = "amalgam.common.CommonProxy")
    public static CommonProxy  proxy;

    // public static PropertyManager propertyManager = PropertyManager.getInstance();
    // public static CastingManager castingManager = CastingManager.getInstance();
    public static Fluid        fluidAmalgam;
    public static Block        stoneCrucible;
    public static Block        castingTable;
    public static Item         stoneTongs;
    public static Item         amalgamBlob;
    public static Item         amalgamSword;
    public static CreativeTabs tab;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        tab = new CreativeTabs("Amalgam") {
            @SideOnly(Side.CLIENT)
            public Item getTabIconItem() {
                return Amalgam.amalgamBlob;
            }
        };

        PacketHandler.init();

        fluidAmalgam = new Fluid("Amalgam");
        FluidRegistry.registerFluid(fluidAmalgam);

        stoneCrucible = new BlockStoneCrucible().setBlockName("stoneCrucible");
        castingTable = new BlockCastingTable().setBlockName("castingTable");
        GameRegistry.registerBlock(stoneCrucible, "StoneCrucible");
        GameRegistry.registerBlock(castingTable, "CastingTable");

        stoneTongs = new ItemStoneTongs().setUnlocalizedName("stoneTongs");
        amalgamBlob = new ItemAmalgamBlob().setUnlocalizedName("amalgamBlob");
        amalgamSword = new ItemAmalgamSword().setUnlocalizedName("amalgamSword");
        GameRegistry.registerItem(stoneTongs, "stoneTongs");
        GameRegistry.registerItem(amalgamBlob, "amalgamBlob");
        GameRegistry.registerItem(amalgamSword, "amalgamSword");

        GameRegistry.registerTileEntity(TileStoneCrucible.class, "stoneCrucible");
        GameRegistry.registerTileEntity(TileCastingTable.class, "castingTable");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        PropertyList ironProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.IRON);
        PropertyList goldProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.GOLD);
        PropertyList diamondProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.EMERALD);

        PropertyManager.registerItemProperties(new ItemStack(Items.iron_ingot), ironProp, INGOT_AMOUNT);
        PropertyManager.registerItemProperties(new ItemStack(Items.gold_ingot), goldProp, INGOT_AMOUNT);
        PropertyManager.registerItemProperties(new ItemStack(Items.diamond), diamondProp, INGOT_AMOUNT);
        PropertyManager.registerItemProperties(new ItemStack(Blocks.iron_block), ironProp, INGOT_AMOUNT * 9);
        PropertyManager.registerItemProperties(new ItemStack(Blocks.gold_block), goldProp, INGOT_AMOUNT * 9);
        PropertyManager.registerItemProperties(new ItemStack(Blocks.diamond_block), diamondProp, INGOT_AMOUNT * 9);
        PropertyManager.registerItemProperties(new ItemStack(Items.gold_nugget), goldProp, BASE_AMOUNT);

        PropertyManager.registerItemProperties(new ItemStack(amalgamBlob), null, 0);

        CastingManager.addShapelessRecipe((ICastItem) amalgamSword, 1, "Amalgam", Blocks.stone);
        CastingManager.addRecipe((ICastItem) amalgamSword, 1, "a","a","s",'s', Items.stick);

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // might need to put something here eventually?
    }

}