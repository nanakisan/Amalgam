package amalgam.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
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
import amalgam.common.item.ItemAmalgamArmor;
import amalgam.common.item.ItemAmalgamAxe;
import amalgam.common.item.ItemAmalgamBlob;
import amalgam.common.item.ItemAmalgamHoe;
import amalgam.common.item.ItemAmalgamPick;
import amalgam.common.item.ItemAmalgamShovel;
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

    public static Fluid        fluidAmalgam;
    public static Block        stoneCrucible;
    public static Block        castingTable;
    public static Item         stoneTongs;
    public static Item         amalgamBlob;
    public static Item         amalgamSword;
    public static Item         amalgamPick;
    public static Item         amalgamAxe;
    public static Item         amalgamShovel;
    public static Item         amalgamHoe;

    public static Item         amalgamHelmet;
    public static Item         amalgamChest;
    public static Item         amalgamLegs;
    public static Item         amalgamBoots;

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
        amalgamPick = new ItemAmalgamPick().setUnlocalizedName("amalgamPick");
        amalgamAxe = new ItemAmalgamAxe().setUnlocalizedName("amalgamAxe");
        amalgamShovel = new ItemAmalgamShovel().setUnlocalizedName("amalgamShovel");
        amalgamHoe = new ItemAmalgamHoe().setUnlocalizedName("amalgamHoe");

        amalgamHelmet = new ItemAmalgamArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 0).setUnlocalizedName("amalgamHelmet");
        amalgamChest = new ItemAmalgamArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 1).setUnlocalizedName("amalgamChest");
        amalgamLegs = new ItemAmalgamArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 2).setUnlocalizedName("amalgamLegs");
        amalgamBoots = new ItemAmalgamArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 3).setUnlocalizedName("amalgamBoots");

        GameRegistry.registerItem(stoneTongs, "stoneTongs");
        GameRegistry.registerItem(amalgamBlob, "amalgamBlob");
        GameRegistry.registerItem(amalgamSword, "amalgamSword");
        GameRegistry.registerItem(amalgamPick, "amalgamPick");
        GameRegistry.registerItem(amalgamAxe, "amalgamAxe");
        GameRegistry.registerItem(amalgamShovel, "amalgamShovel");
        GameRegistry.registerItem(amalgamHoe, "amalgamHoe");
        GameRegistry.registerItem(amalgamHelmet, "amalgamHelmet");
        GameRegistry.registerItem(amalgamChest, "amalgamChest");
        GameRegistry.registerItem(amalgamLegs, "amalgamLegs");
        GameRegistry.registerItem(amalgamBoots, "amalgamBoots");

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
        CastingManager.addRecipe((ICastItem) amalgamSword, 1, "a", "a", "s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamPick, 1, "aaa", " s ", " s ", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamAxe, 1, "aa", "as", " s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamAxe, 1, "aa", "sa", "s ", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamShovel, 1, "a", "s", "s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamHoe, 1, "aa", " s", " s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamHoe, 1, "aa", "s ", "s ", 's', Items.stick);

        // TODO recipes with just amalgam are not working
        CastingManager.addRecipe((ICastItem) amalgamHelmet, 1, "aaa", "a a", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamChest, 1, "a a", "aaa", "aaa", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamLegs, 1, "aaa", "a a", "a a", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamBoots, 1, "a a", "a a", 's', Items.stick);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

}