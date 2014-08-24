package amalgam.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
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
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import amalgam.common.tile.TileCastingTable;
import amalgam.common.tile.TileStoneCrucible;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Config {

    public static Config        instance                   = new Config();

    public static Configuration configFile;

    public static final Logger  LOG                        = LogManager.getLogger(Amalgam.MODID);
    public static final int     CASTING_GUI_ID             = 1;

    public static boolean       replicateVanillaProperties = true;
    public static boolean       advancedRendering          = true;
    public static boolean       floatingCastResult         = true;
    public static boolean       coloredAmalgam             = true;
    public static int           baseVolume                 = 10;

    public static final int     BASE_AMOUNT                = 1;
    public static final int     INGOT_AMOUNT               = BASE_AMOUNT * 9;
    public static final int     BLOCK_AMOUNT               = INGOT_AMOUNT * 9;

    public static Fluid         fluidAmalgam;

    public static Block         stoneCrucible;
    public static Block         castingTable;

    public static Item          stoneTongs;
    public static Item          amalgamBlob;

    public static Item          amalgamSword;
    public static Item          amalgamPick;
    public static Item          amalgamAxe;
    public static Item          amalgamShovel;
    public static Item          amalgamHoe;

    public static Item          amalgamHelmet;
    public static Item          amalgamChest;
    public static Item          amalgamLegs;
    public static Item          amalgamBoots;

    public static CreativeTabs  tab;

    public static int           castingTableRID            = -1;
    public static int           crucibleRID                = -1;

    public static void init(FMLPreInitializationEvent event) {
        tab = new CreativeTabs("Amalgam") {
            @SideOnly(Side.CLIENT)
            public Item getTabIconItem() {
                return amalgamBlob;
            }
        };

        configFile = new Configuration(event.getSuggestedConfigurationFile());

        syncConfig();
    }

    public static void syncConfig() {

        replicateVanillaProperties = configFile.getBoolean("Replicate Vanilla Properties", Configuration.CATEGORY_GENERAL,
                replicateVanillaProperties, "True: Use vanilla properties for vanilla ores. Fasle: Use new properties for vanilla ores");

        advancedRendering = configFile.getBoolean("Advanced rendering", Configuration.CATEGORY_GENERAL, advancedRendering,
                "Allows for rendering of items on casting tables");

        floatingCastResult = configFile.getBoolean("Floating Cast Result", Configuration.CATEGORY_GENERAL, floatingCastResult,
                "Does the cast result float, or is it rendered flat");

        coloredAmalgam = configFile.getBoolean("Allow colored amalgam", Configuration.CATEGORY_GENERAL, coloredAmalgam,
                "Allow amalgam color to be determined by material properties");

        baseVolume = configFile.getInt("Base amalgam volume (mB)", Configuration.CATEGORY_GENERAL, baseVolume, 1, Integer.MAX_VALUE,
                "The volume of the smallest bit of amalgam (mB)");

        if (configFile.hasChanged()) {
            configFile.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.modID.equals(Amalgam.MODID)) {
            syncConfig();
        }
    }

    public static void registerFluids() {

        fluidAmalgam = new Fluid("Amalgam");
        FluidRegistry.registerFluid(fluidAmalgam);
    }

    public static void registerItems() {

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
    }

    public static void registerBlocks() {

        stoneCrucible = new BlockStoneCrucible().setBlockName("stoneCrucible");
        castingTable = new BlockCastingTable().setBlockName("castingTable");

        GameRegistry.registerBlock(stoneCrucible, "StoneCrucible");
        GameRegistry.registerBlock(castingTable, "CastingTable");

        GameRegistry.registerTileEntity(TileStoneCrucible.class, "stoneCrucible");
        GameRegistry.registerTileEntity(TileCastingTable.class, "castingTable");
    }

    public static void registerAmalgamProperties() {
        PropertyList ironProp;
        PropertyList goldProp;
        PropertyList diamondProp;

        if (Config.replicateVanillaProperties) {
            ironProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.IRON);
            goldProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.GOLD);
            diamondProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.EMERALD);
        } else {
            ironProp = new PropertyList().add(PropertyManager.DENSITY, 2.5F).add(PropertyManager.HARDNESS, 2).add(PropertyManager.LUSTER, 2.0F)
                    .add(PropertyManager.MALIABILITY, 3.5F);

            goldProp = new PropertyList().add(PropertyManager.DENSITY, 1.0F).add(PropertyManager.HARDNESS, 0.75F).add(PropertyManager.LUSTER, 5.0F)
                    .add(PropertyManager.MALIABILITY, 1.5F);

            diamondProp = new PropertyList().add(PropertyManager.DENSITY, 5.0F).add(PropertyManager.HARDNESS, 3.25F)
                    .add(PropertyManager.LUSTER, 1.0F).add(PropertyManager.MALIABILITY, 0.5F);
        }

        ironProp.add(PropertyManager.COLOR, 0xFFFFFF);
        goldProp.add(PropertyManager.COLOR, 0xEAEE57);
        diamondProp.add(PropertyManager.COLOR, 0x33EBCB);

        PropertyManager.registerOreDictProperties("ingotIron", ironProp, Config.INGOT_AMOUNT);
        PropertyManager.registerOreDictProperties("ingotGold", goldProp, Config.INGOT_AMOUNT);
        PropertyManager.registerOreDictProperties("gemDiamond", diamondProp, Config.INGOT_AMOUNT);
        PropertyManager.registerOreDictProperties("blockIron", ironProp, Config.BLOCK_AMOUNT);
        PropertyManager.registerOreDictProperties("blockGold", goldProp, Config.BLOCK_AMOUNT);
        PropertyManager.registerOreDictProperties("blockDiamond", diamondProp, Config.BLOCK_AMOUNT);
        PropertyManager.registerOreDictProperties("nuggetGold", goldProp, Config.BASE_AMOUNT);

        PropertyManager.registerItemProperties(new ItemStack(amalgamBlob), null, 0);
    }

    public static void registerRecipes() {
        CastingManager.addShapelessRecipe((ICastItem) amalgamSword, 1, "Amalgam", Blocks.stone);
        CastingManager.addRecipe((ICastItem) amalgamSword, 1, "a", "a", "s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamPick, 1, "aaa", " s ", " s ", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamAxe, 1, "aa", "as", " s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamAxe, 1, "aa", "sa", "s ", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamShovel, 1, "a", "s", "s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamHoe, 1, "aa", " s", " s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamHoe, 1, "aa", "s ", "s ", 's', Items.stick);

        CastingManager.addRecipe((ICastItem) amalgamHelmet, 1, "aaa", "a a");
        CastingManager.addRecipe((ICastItem) amalgamChest, 1, "a a", "aaa", "aaa");
        CastingManager.addRecipe((ICastItem) amalgamLegs, 1, "aaa", "a a", "a a");
        CastingManager.addRecipe((ICastItem) amalgamBoots, 1, "a a", "a a");

        GameRegistry.addRecipe(new ItemStack(stoneTongs), "s s", " s ", " s ", 's', Blocks.cobblestone);
        GameRegistry.addRecipe(new ItemStack(stoneCrucible), "s s", "s s", "sss", 's', Blocks.cobblestone);
        GameRegistry.addRecipe(new ItemStack(castingTable), "sss", "sws", "sss", 's', Blocks.cobblestone, 'w', Blocks.crafting_table);
    }
}
