package amalgam.common;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import amalgam.common.block.BlockCastingTable;
import amalgam.common.block.BlockStoneCrucible;
import amalgam.common.casting.CastingManager;
import amalgam.common.casting.ICastItem;
import amalgam.common.entity.EntityAmalgamPotato;
import amalgam.common.item.ItemAmalgamArmor;
import amalgam.common.item.ItemAmalgamAxe;
import amalgam.common.item.ItemAmalgamBlob;
import amalgam.common.item.ItemAmalgamHoe;
import amalgam.common.item.ItemAmalgamPick;
import amalgam.common.item.ItemAmalgamPotato;
import amalgam.common.item.ItemAmalgamShovel;
import amalgam.common.item.ItemAmalgamShroom;
import amalgam.common.item.ItemAmalgamSword;
import amalgam.common.item.ItemStoneTongs;
import amalgam.common.properties.AmalgamPropertyManager;
import amalgam.common.tile.TileCastingTable;
import amalgam.common.tile.TileStoneCrucible;

import com.google.common.collect.Sets;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Config {

    // TODO figure out a way for people to see properties without WAILA installed (show them on tongs and amalgam blobs)
    
    // TODO allow casting table and crucible to be crafted with smoothstone (add config options)

    // TODO consider moving all the item definitions and registering to a library class and move all config stuff into
    // the config class
    // We will have 4 config files:
    
    // amalgam.cfg - the config file for general settings
    // the next three configs should probably be in a subfolder, maybe all configs should be?
    
    // amalgamProperties.cfg - this will hold all defined property lists, one for each type of material used in the mod.
    // Gold, Diamond, Iron etc properties are defined here, and we can dynamically add more ores
    
    // amalgamMaterials.cfg - this is where you define the materials which can be used. each material has a name,
    // property list (taken from the other config file), volume, and is defined as being an ore dictionary entry or a
    // normal item entry, and we can dynamically add more items. gold nuggets, blocks, ingots all share the same
    // properties but have different volumes and ore dictionary entries which is why the items and property list configs
    // are separate (they would all use the gold property list defined in the properties config)
    
    // amalgamItems.cfg -this is where we define how each material property determines each tool property. Each tool
    // property is a linear combination of the properties of the amalgam used to make the tool, here we just define the
    // weights each amalgam property has on the tool property. we can't dynamicaly add more items unfortunately

    public static Config        instance              = new Config();

    public static Configuration configFile;

    public static final Logger  LOG                   = LogManager.getLogger(Amalgam.MODID);
    public static final int     CASTING_GUI_ID        = 1;

    public static boolean       floatingCastResult    = true;
    public static boolean       coloredAmalgam        = true;
    public static boolean       allowFunItems         = true;
    public static boolean       disableVanillaRecipes = true;        

    public static final int     BASE_AMOUNT           = 1;
    public static final int     INGOT_AMOUNT          = BASE_AMOUNT * 9;
    public static final int     BLOCK_AMOUNT          = INGOT_AMOUNT * 9;

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

    public static Item          amalgamPotato;
    public static Item          amalgamShroom;

    public static Item          amalgamHelmet;
    public static Item          amalgamChest;
    public static Item          amalgamLegs;
    public static Item          amalgamBoots;

    public static CreativeTabs  tab;

    public static int           castingTableRID       = -1;
    public static int           crucibleRID           = -1;

    public static boolean       moreVanillaMaterials  = true;
    public static boolean       commonModMaterials    = true;

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

        floatingCastResult = configFile.getBoolean("Floating Cast Result", Configuration.CATEGORY_GENERAL, floatingCastResult,
                "Render the casting table output above the casting table.");

        coloredAmalgam = configFile.getBoolean("Allow colored amalgam", Configuration.CATEGORY_GENERAL, coloredAmalgam,
                "Allow amalgam color to be determined by material properties");

        allowFunItems = configFile.getBoolean("Allow fun items", Configuration.CATEGORY_GENERAL, allowFunItems,
                "Allow fun items like the AmalgaShroom and Unstable Amalgamized Potato");

        disableVanillaRecipes = configFile.getBoolean("Disable vanilla recipes", Configuration.CATEGORY_GENERAL, disableVanillaRecipes,
                "Removes vanilla recipes for swords, tools and armor");

        if (configFile.hasChanged()) {
            configFile.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {

        if (eventArgs.modID.equals(Amalgam.MODID)) {

            if (eventArgs.configID != null && eventArgs.configID.equals("ores")) {
                LOG.info("config id: " + eventArgs.configID);
                AmalgamPropertyManager.syncConfigDefaults();
                // AmalgamPropertyManager.loadConfiguration();
            } else if (eventArgs.configID != null && eventArgs.configID.equals(Configuration.CATEGORY_GENERAL)) {
                LOG.info("config id: " + eventArgs.configID);
                syncConfig();
            }
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

        amalgamShroom = new ItemAmalgamShroom().setUnlocalizedName("amalgamShroom");
        amalgamPotato = new ItemAmalgamPotato().setUnlocalizedName("amalgamPotato");

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
        GameRegistry.registerItem(amalgamShroom, "amalgamShroom");
        GameRegistry.registerItem(amalgamPotato, "amalgamPotato");
    }

    public static void registerBlocks() {

        stoneCrucible = new BlockStoneCrucible().setBlockName("stoneCrucible");
        castingTable = new BlockCastingTable().setBlockName("castingTable");

        GameRegistry.registerBlock(stoneCrucible, "stoneCrucible");
        GameRegistry.registerBlock(castingTable, "castingTable");

        GameRegistry.registerTileEntity(TileStoneCrucible.class, "stoneCrucible");
        GameRegistry.registerTileEntity(TileCastingTable.class, "castingTable");
    }

    public static void registerAmalgamProperties() {

        AmalgamPropertyManager.registerItemProperties(new ItemStack(amalgamBlob), null, 0);

        // if (Config.moreMaterials) {
        // AmalgamPropertyList emeraldProp = new AmalgamPropertyList().add(AmalgamPropertyManager.DENSITY, 3.1F)
        // .add(AmalgamPropertyManager.HARDNESS, 3.7F).add(AmalgamPropertyManager.LUSTER, 3.4F)
        // .add(AmalgamPropertyManager.MALLEABILITY, 3.2F).add(AmalgamPropertyManager.COLOR, 0x41F384);
        // AmalgamPropertyList obsidianProp = new AmalgamPropertyList().add(AmalgamPropertyManager.DENSITY, 6.5F)
        // .add(AmalgamPropertyManager.HARDNESS, 9.5F).add(AmalgamPropertyManager.LUSTER, 1.1F)
        // .add(AmalgamPropertyManager.MALLEABILITY, 0.2F).add(AmalgamPropertyManager.COLOR, 0x15091B);
        // AmalgamPropertyList blazeRodProp = new AmalgamPropertyList().add(AmalgamPropertyManager.DENSITY, 0.8F)
        // .add(AmalgamPropertyManager.HARDNESS, 0.5F).add(AmalgamPropertyManager.LUSTER, 2.0F)
        // .add(AmalgamPropertyManager.MALLEABILITY, 8.5F).add(AmalgamPropertyManager.COLOR, 0xFFCB00);
        //
        // AmalgamPropertyManager.registerOreDictProperties("gemEmerald", emeraldProp, Config.INGOT_AMOUNT);
        // AmalgamPropertyManager.registerOreDictProperties("blockEmerald", emeraldProp, Config.BLOCK_AMOUNT);
        //
        // AmalgamPropertyManager.registerItemProperties(new ItemStack(Items.blaze_powder), blazeRodProp, BASE_AMOUNT);
        // AmalgamPropertyManager.registerItemProperties(new ItemStack(Items.blaze_rod), blazeRodProp, BASE_AMOUNT * 2);
        // AmalgamPropertyManager.registerItemProperties(new ItemStack(Blocks.obsidian), obsidianProp, BASE_AMOUNT * 3);
        // }

        // if (Config.modMaterials) {
        // AmalgamPropertyList copperProp = new AmalgamPropertyList().add(AmalgamPropertyManager.DENSITY, 2.1F)
        // .add(AmalgamPropertyManager.HARDNESS, 1.7F).add(AmalgamPropertyManager.LUSTER, 4.4F)
        // .add(AmalgamPropertyManager.MALLEABILITY, 4.2F).add(AmalgamPropertyManager.COLOR, 0xB87333);
        // AmalgamPropertyList tinProp = new AmalgamPropertyList().add(AmalgamPropertyManager.DENSITY, 3.2F)
        // .add(AmalgamPropertyManager.HARDNESS, 2.9F).add(AmalgamPropertyManager.LUSTER, 1.1F)
        // .add(AmalgamPropertyManager.MALLEABILITY, 2.3F).add(AmalgamPropertyManager.COLOR, 0xBBCCBB);
        // AmalgamPropertyList silverProp = new AmalgamPropertyList().add(AmalgamPropertyManager.DENSITY, 1.5F)
        // .add(AmalgamPropertyManager.HARDNESS, 3.5F).add(AmalgamPropertyManager.LUSTER, 6.5F)
        // .add(AmalgamPropertyManager.MALLEABILITY, 4.3F).add(AmalgamPropertyManager.COLOR, 0xCCCCCC);
        // AmalgamPropertyList leadProp = new AmalgamPropertyList().add(AmalgamPropertyManager.DENSITY, 7.8F)
        // .add(AmalgamPropertyManager.HARDNESS, 6.5F).add(AmalgamPropertyManager.LUSTER, 1.2F)
        // .add(AmalgamPropertyManager.MALLEABILITY, 2.2F).add(AmalgamPropertyManager.COLOR, 0x778899);
        //
        // AmalgamPropertyManager.registerOreDictProperties("nuggetCopper", copperProp, Config.BASE_AMOUNT);
        // AmalgamPropertyManager.registerOreDictProperties("nuggetTin", tinProp, Config.BASE_AMOUNT);
        // AmalgamPropertyManager.registerOreDictProperties("nuggetSilver", silverProp, Config.BASE_AMOUNT);
        // AmalgamPropertyManager.registerOreDictProperties("nuggetLead", leadProp, Config.BASE_AMOUNT);
        //
        // AmalgamPropertyManager.registerOreDictProperties("ingotCopper", copperProp, Config.INGOT_AMOUNT);
        // AmalgamPropertyManager.registerOreDictProperties("ingotTin", tinProp, Config.INGOT_AMOUNT);
        // AmalgamPropertyManager.registerOreDictProperties("ingotSilver", silverProp, Config.INGOT_AMOUNT);
        // AmalgamPropertyManager.registerOreDictProperties("ingotLead", leadProp, Config.INGOT_AMOUNT);
        //
        // AmalgamPropertyManager.registerOreDictProperties("blockCopper", copperProp, Config.BLOCK_AMOUNT);
        // AmalgamPropertyManager.registerOreDictProperties("blockTin", tinProp, Config.BLOCK_AMOUNT);
        // AmalgamPropertyManager.registerOreDictProperties("blockSilver", silverProp, Config.BLOCK_AMOUNT);
        // AmalgamPropertyManager.registerOreDictProperties("iblockLead", leadProp, Config.BLOCK_AMOUNT);
        // }
    }

    public static void registerRecipes() {
        String n = String.valueOf(CastingManager.NUGGET_CHAR);
        String i = String.valueOf(CastingManager.INGOT_CHAR);
        String b = String.valueOf(CastingManager.BLOCK_CHAR);

        CastingManager.addShapelessRecipe((ICastItem) amalgamSword, 1, "Amalgam", Blocks.stone);
        CastingManager.addRecipe((ICastItem) amalgamSword, 1, i, i, "s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamPick, 1, i + i + i, " s ", " s ", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamAxe, 1, i + i, i + "s", " s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamAxe, 1, i + i, "s" + i, "s ", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamShovel, 1, i, "s", "s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamHoe, 1, i + i, " s", " s", 's', Items.stick);
        CastingManager.addRecipe((ICastItem) amalgamHoe, 1, i + i, "s ", "s ", 's', Items.stick);

        CastingManager.addRecipe((ICastItem) amalgamHelmet, 1, i + i + i, i + " " + i);
        CastingManager.addRecipe((ICastItem) amalgamChest, 1, i + " " + i, i + i + i, i + i + i);
        CastingManager.addRecipe((ICastItem) amalgamLegs, 1, i + i + i, i + " " + i, i + " " + i);
        CastingManager.addRecipe((ICastItem) amalgamBoots, 1, i + " " + i, i + " " + i);

        CastingManager.addRecipe((ICastItem) amalgamShroom, 1, n + n + n, n + "a" + n, n + n + n, 'a', Blocks.red_mushroom);
        CastingManager.addRecipe((ICastItem) amalgamPotato, 4, "gpg", "p" + b + "p", "gpg", 'g', Items.gunpowder, 'p', Items.potato);

        GameRegistry.addRecipe(new ItemStack(stoneTongs), "s s", " s ", " s ", 's', Blocks.cobblestone);
        GameRegistry.addRecipe(new ItemStack(stoneCrucible), "s s", "s s", "sss", 's', Blocks.cobblestone);
        GameRegistry.addRecipe(new ItemStack(castingTable), "sss", "sws", "sss", 's', Blocks.cobblestone, 'w', Blocks.crafting_table);
    }

    public static void removeVanillaRecipes() {
        if (disableVanillaRecipes) {
            Iterator<IRecipe> recipes = CraftingManager.getInstance().getRecipeList().iterator();

            Set<Item> REMOVE_ITEMS = Sets.newHashSet(new Item[] { Items.diamond_axe, Items.diamond_boots, Items.diamond_chestplate,
                    Items.diamond_helmet, Items.diamond_hoe, Items.diamond_leggings, Items.diamond_pickaxe, Items.diamond_shovel,
                    Items.diamond_sword, Items.iron_axe, Items.iron_boots, Items.iron_chestplate, Items.iron_helmet, Items.iron_hoe,
                    Items.iron_leggings, Items.iron_pickaxe, Items.iron_shovel, Items.iron_sword, Items.golden_axe, Items.golden_boots,
                    Items.golden_chestplate, Items.golden_helmet, Items.golden_hoe, Items.golden_leggings, Items.golden_pickaxe, Items.golden_shovel,
                    Items.golden_sword });

            while (recipes.hasNext()) {
                IRecipe testRecipe = recipes.next();
                if (testRecipe.getRecipeOutput() != null && REMOVE_ITEMS.contains(testRecipe.getRecipeOutput().getItem())) {
                    recipes.remove();
                }
            }
        }
    }

    public static void registerEntities() {
        int entityID = 0;
        EntityRegistry.registerModEntity(EntityAmalgamPotato.class, "Unstable Amalgamized Potato", entityID++, Amalgam.instance, 64, 10, true);
    }
}
