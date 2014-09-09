package amalgam.common;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
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
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
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

    public static Config        instance              = new Config();

    public static Configuration configFile;

    public static final Logger  LOG                   = LogManager.getLogger(Amalgam.MODID);
    public static final int     CASTING_GUI_ID        = 1;

    public static boolean       materialRebalancing   = true;
    public static boolean       moreMaterials         = true;
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

        materialRebalancing = configFile.getBoolean("Allow vanilla material rebalancing", Configuration.CATEGORY_GENERAL, materialRebalancing,
                "Use new balanced properties for iron, gold and diamond.");

        moreMaterials = configFile.getBoolean("Allow extra vanilla materials to be used", Configuration.CATEGORY_GENERAL, moreMaterials,
                "Allow obsidian, blaze rods, emeralds and nether quartz to be used in amalgam in addition to iron, gold and diamond");

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

        GameRegistry.registerBlock(stoneCrucible, "StoneCrucible");
        GameRegistry.registerBlock(castingTable, "CastingTable");

        GameRegistry.registerTileEntity(TileStoneCrucible.class, "stoneCrucible");
        GameRegistry.registerTileEntity(TileCastingTable.class, "castingTable");
    }

    public static void registerAmalgamProperties() {
        PropertyList ironProp;
        PropertyList goldProp;
        PropertyList diamondProp;

        if (Config.materialRebalancing) {
            ironProp = new PropertyList().add(PropertyManager.DENSITY, 2.5F).add(PropertyManager.HARDNESS, 2).add(PropertyManager.LUSTER, 2.0F)
                    .add(PropertyManager.MALIABILITY, 3.5F);

            goldProp = new PropertyList().add(PropertyManager.DENSITY, 1.0F).add(PropertyManager.HARDNESS, 0.75F).add(PropertyManager.LUSTER, 5.0F)
                    .add(PropertyManager.MALIABILITY, 1.5F);

            diamondProp = new PropertyList().add(PropertyManager.DENSITY, 5.0F).add(PropertyManager.HARDNESS, 3.25F)
                    .add(PropertyManager.LUSTER, 1.0F).add(PropertyManager.MALIABILITY, 0.5F);
        } else {
            ironProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.IRON);
            goldProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.GOLD);
            diamondProp = PropertyManager.generatePropertiesFromToolMaterial(ToolMaterial.EMERALD);
        }

        ironProp.add(PropertyManager.COLOR, 0xBBBBBB);
        goldProp.add(PropertyManager.COLOR, 0xEAEE57);
        diamondProp.add(PropertyManager.COLOR, 0x33EBCB);

        PropertyManager.registerOreDictProperties("nuggetIron", ironProp, Config.BASE_AMOUNT);
        PropertyManager.registerOreDictProperties("nuggetGold", goldProp, Config.BASE_AMOUNT);
        PropertyManager.registerOreDictProperties("nuggetDiamond", diamondProp, Config.BASE_AMOUNT);
        PropertyManager.registerOreDictProperties("ingotIron", ironProp, Config.INGOT_AMOUNT);
        PropertyManager.registerOreDictProperties("ingotGold", goldProp, Config.INGOT_AMOUNT);
        PropertyManager.registerOreDictProperties("gemDiamond", diamondProp, Config.INGOT_AMOUNT);
        PropertyManager.registerOreDictProperties("blockIron", ironProp, Config.BLOCK_AMOUNT);
        PropertyManager.registerOreDictProperties("blockGold", goldProp, Config.BLOCK_AMOUNT);
        PropertyManager.registerOreDictProperties("blockDiamond", diamondProp, Config.BLOCK_AMOUNT);

        PropertyManager.registerItemProperties(new ItemStack(amalgamBlob), null, 0);

        if (Config.moreMaterials) {
            PropertyList lapisProp = new PropertyList().add(PropertyManager.DENSITY, 0.5F).add(PropertyManager.HARDNESS, 0.5F)
                    .add(PropertyManager.LUSTER, 6.0F).add(PropertyManager.MALIABILITY, 0.5F).add(PropertyManager.COLOR, 0x0F298C);
            PropertyList redstoneProp = new PropertyList().add(PropertyManager.DENSITY, 1.0F).add(PropertyManager.HARDNESS, 1.0F)
                    .add(PropertyManager.LUSTER, 1.0F).add(PropertyManager.MALIABILITY, 4.0F).add(PropertyManager.COLOR, 0xD12B13);
            PropertyList emeraldProp = new PropertyList().add(PropertyManager.DENSITY, 1.0F).add(PropertyManager.HARDNESS, 1.0F)
                    .add(PropertyManager.LUSTER, 4.0F).add(PropertyManager.MALIABILITY, 1.0F).add(PropertyManager.COLOR, 0x41F384);
            PropertyList quartzProp = new PropertyList().add(PropertyManager.DENSITY, 2.0F).add(PropertyManager.HARDNESS, 2.0F)
                    .add(PropertyManager.LUSTER, 3.0F).add(PropertyManager.MALIABILITY, 3.0F).add(PropertyManager.COLOR, 0xFFFFFF);
            PropertyList obsidianProp = new PropertyList().add(PropertyManager.DENSITY, 6.0F).add(PropertyManager.HARDNESS, 3.9F)
                    .add(PropertyManager.LUSTER, 0.0F).add(PropertyManager.MALIABILITY, 0.5F).add(PropertyManager.COLOR, 0x09090B);
            PropertyList blazeRodProp = new PropertyList().add(PropertyManager.DENSITY, 1.0F).add(PropertyManager.HARDNESS, 0.5F)
                    .add(PropertyManager.LUSTER, 2.0F).add(PropertyManager.MALIABILITY, 5.0F).add(PropertyManager.COLOR, 0xFFCB00);

            // PropertyManager.registerOreDictProperties("gemLapis", lapisProp, Config.BASE_AMOUNT * 2);
            PropertyManager.registerOreDictProperties("gemQuartz", quartzProp, Config.BASE_AMOUNT * 2);
            // PropertyManager.registerOreDictProperties("dustRedstone", redstoneProp, Config.BASE_AMOUNT * 2);
            PropertyManager.registerOreDictProperties("gemEmerald", emeraldProp, Config.INGOT_AMOUNT);

            PropertyManager.registerOreDictProperties("blockQuartz", quartzProp, Config.INGOT_AMOUNT * 2);
            // PropertyManager.registerOreDictProperties("blockLapis", lapisProp, Config.INGOT_AMOUNT * 2);
            // PropertyManager.registerOreDictProperties("blockRedstone", redstoneProp, Config.INGOT_AMOUNT * 2);
            PropertyManager.registerOreDictProperties("blockEmerald", emeraldProp, Config.BLOCK_AMOUNT);

            PropertyManager.registerItemProperties(new ItemStack(Items.blaze_powder), blazeRodProp, BASE_AMOUNT);
            PropertyManager.registerItemProperties(new ItemStack(Items.blaze_rod), blazeRodProp, BASE_AMOUNT * 2);
            PropertyManager.registerItemProperties(new ItemStack(Blocks.obsidian), obsidianProp, BASE_AMOUNT * 3);
        }

        // TODO: more ore dictionary properties for common mod metals
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
