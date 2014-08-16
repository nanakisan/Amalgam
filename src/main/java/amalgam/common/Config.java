package amalgam.common;

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
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Config {

    public static Configuration configFile;

    public static final Logger  LOG             = LogManager.getLogger(Amalgam.MODID);
    public static final int     CASTING_GUI_ID  = 1;

    public static boolean       VANILLA_RECIPES = true;
    public static int           BASE_VOLUME     = 10;
    public static final int     BASE_AMOUNT     = 1;
    public static final int     INGOT_AMOUNT    = BASE_AMOUNT * 9;
    public static final int     BLOCK_AMOUNT    = INGOT_AMOUNT * 9;

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
        BASE_VOLUME = configFile.getInt("Base amalgam volume (mB)", Configuration.CATEGORY_GENERAL, BASE_VOLUME, 1, Integer.MAX_VALUE,
                "The volume of the smallest bit of amalgam (mB)");

        // TODO turn vanilla recipes off
        VANILLA_RECIPES = configFile.getBoolean("Disable vanilla recipes", Configuration.CATEGORY_GENERAL, VANILLA_RECIPES,
                "Disable vanilla recipes for swords, tools and armor");

        // TODO allow override of material properties,look at mod override code for the forge config gui

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
        PropertyList ironProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.IRON);
        PropertyList goldProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.GOLD);
        PropertyList diamondProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.EMERALD);

        PropertyManager.registerItemProperties(new ItemStack(Items.iron_ingot), ironProp, Config.INGOT_AMOUNT);
        PropertyManager.registerItemProperties(new ItemStack(Items.gold_ingot), goldProp, Config.INGOT_AMOUNT);
        PropertyManager.registerItemProperties(new ItemStack(Items.diamond), diamondProp, Config.INGOT_AMOUNT);
        PropertyManager.registerItemProperties(new ItemStack(Blocks.iron_block), ironProp, Config.BLOCK_AMOUNT);
        PropertyManager.registerItemProperties(new ItemStack(Blocks.gold_block), goldProp, Config.BLOCK_AMOUNT);
        PropertyManager.registerItemProperties(new ItemStack(Blocks.diamond_block), diamondProp, Config.BLOCK_AMOUNT);
        PropertyManager.registerItemProperties(new ItemStack(Items.gold_nugget), goldProp, Config.BASE_AMOUNT);

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

        GameRegistry.addRecipe(new ItemStack(stoneTongs), " ss", " s ", "ss ", 's', Blocks.cobblestone);
        GameRegistry.addRecipe(new ItemStack(stoneCrucible), "s s", "s s", "sss", 's', Blocks.cobblestone);
        GameRegistry.addRecipe(new ItemStack(castingTable), "sss", "sws", "www", 's', Blocks.cobblestone, 'w', Blocks.planks);
    }
}
