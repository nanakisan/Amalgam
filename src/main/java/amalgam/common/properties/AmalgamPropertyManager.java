package amalgam.common.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Level;

import amalgam.common.Config;
import amalgam.common.fluid.IAmalgableItem;
import amalgam.common.properties.AmalgamProperty.ComboType;
import cpw.mods.fml.common.FMLLog;

public final class AmalgamPropertyManager {

    private static final AmalgamPropertyManager     INSTANCE          = new AmalgamPropertyManager();
    private static final Map<String, List<Object>>  ITEM_REGISTRY     = new HashMap<String, List<Object>>();
    private static final Map<Integer, List<Object>> ORE_DICT_REGISTRY = new HashMap<Integer, List<Object>>();

    public static final AmalgamProperty             MALLEABILITY      = new AmalgamProperty("Malleability", 1, ComboType.QUADAVERAGE);
    public static final AmalgamProperty             DENSITY           = new AmalgamProperty("Density", 6, ComboType.QUADAVERAGE);
    public static final AmalgamProperty             LUSTER            = new AmalgamProperty("Luster", 2, ComboType.QUADAVERAGE);
    public static final AmalgamProperty             HARDNESS          = new AmalgamProperty("Hardness", 1, ComboType.QUADAVERAGE);

    public static final AmalgamProperty             COLOR             = new AmalgamProperty("Color", 0x999999, ComboType.COLOR);

    // these are used for the separate ore config file (look at ForgeChunkManager for where this idea comes from)
    private static File                             propertyCfgFile;
    private static File                             materialCfgFile;
    private static Configuration                    propertyConfig;
    private static Configuration                    materialConfig;

    private AmalgamPropertyManager() {
    }

    public static AmalgamPropertyManager getInstance() {
        return INSTANCE;
    }

    public static void registerItemProperties(ItemStack stack, AmalgamPropertyList list, int volume) {
        registerItemProperties(getFullItemName(stack.getItem()), list, volume);
    }

    private static String getFullItemName(Item item) {
        return Item.itemRegistry.getNameForObject(item);
    }

    public static void registerItemProperties(String name, AmalgamPropertyList list, int volume) {
        List<Object> blob = Arrays.asList(new Object[] { list, Integer.valueOf(volume) });
        ITEM_REGISTRY.put(name, blob);
    }

    public static void registerOreDictProperties(String dictName, AmalgamPropertyList list, int volume) {
        if ("Unkown".equals(dictName) || "name".equals(dictName)) {
            Config.LOG.error("can't register 'Unkown' or 'name'");
            return;
        }

        int id = OreDictionary.getOreID(dictName);

        registerOreDictProperties(id, list, volume);
    }

    public static void registerOreDictProperties(int oreDictID, AmalgamPropertyList list, int volume) {
        if (oreDictID == -1) {
            Config.LOG.error("can't register ore id: -1");
            return;
        }

        if (list != null && volume > 0) {
            List<Object> blob = Arrays.asList(new Object[] { list, Integer.valueOf(volume) });
            ORE_DICT_REGISTRY.put(oreDictID, blob);
        } else {
            Config.LOG.error("Trying to register an ore dictionary entry with improper values");
        }
    }

    public static AmalgamPropertyList getProperties(ItemStack stack) {
        String name = getFullItemName(stack.getItem());

        // First we check the item registry, then we fall back on the ore dict registry if we find no entry.
        if (ITEM_REGISTRY.containsKey(name)) {
            List<Object> blob = (List<Object>) ITEM_REGISTRY.get(name);

            if (blob == null) {
                return ((IAmalgableItem) stack.getItem()).getProperties(stack);
            }

            return (AmalgamPropertyList) blob.get(0);
        }

        int ids[] = OreDictionary.getOreIDs(stack);

        for (int i = 0; i < ids.length; i++) {
            if (ORE_DICT_REGISTRY.containsKey(ids[i])) {
                List<Object> blob = ORE_DICT_REGISTRY.get(ids[i]);
                return (AmalgamPropertyList) blob.get(0);
            }
        }

        return null;
    }

    public static int getVolume(ItemStack stack) {
        String name = getFullItemName(stack.getItem());

        // First we check the item registry, then we fall back on the ore dict registry if we find no entry.
        if (ITEM_REGISTRY.containsKey(name)) {
            List<Object> blob = (List<Object>) ITEM_REGISTRY.get(name);

            if (blob == null) {
                return ((IAmalgableItem) stack.getItem()).getVolume(stack);
            }

            return (Integer) blob.get(1);
        }

        int ids[] = OreDictionary.getOreIDs(stack);

        for (int i = 0; i < ids.length; i++) {
            if (ORE_DICT_REGISTRY.containsKey(ids[i])) {
                List<Object> blob = ORE_DICT_REGISTRY.get(ids[i]);
                return (Integer) blob.get(1);
            }
        }

        return 0;
    }

    public static boolean itemIsAmalgable(ItemStack stack) {
        String name = getFullItemName(stack.getItem());

        if (ITEM_REGISTRY.containsKey(name)) {
            return true;
        }

        int ids[] = OreDictionary.getOreIDs(stack);

        for (int i = 0; i < ids.length; i++) {
            if (ORE_DICT_REGISTRY.containsKey(ids[i])) {
                return true;
            }
        }

        return false;
    }

    // ForgeChunkManager inspired methods

    // TODO restructure the way adding materials and properties work.
    // One screen for creating properties (like the one we have currently)
    // One screen for adding materials, you select the property of the material from a list of properties that are
    // defined in the config, input the volume, select if it an ore dictionary or item entry and enter the name
    // This separates out the current config screen into two config screens

    public static void loadConfigurations() {
        ORE_DICT_REGISTRY.clear();
        ITEM_REGISTRY.clear();

        Set<String> propertyLists = propertyConfig.getCategoryNames();
        Set<String> materials = propertyConfig.getCategoryNames();
        
        for (String mat : materials) {  
            Property name = materialConfig.get(mat, "Name", "Unkown");
            Property volume = materialConfig.get(mat, "Volume" , 9);
            Property type = materialConfig.get(mat, "Type", "oreDict");
            Property pList = materialConfig.get(mat, "Property List", "-");
            
            String pListName = pList.getString();
            if(!propertyLists.contains(pListName)){
                Config.LOG.info("Property list for material doesn't exist");
                continue;
            }
            
            Property density = propertyConfig.get(pListName, "density", DENSITY.getDefaultValue());
            Property malleability = propertyConfig.get(pListName, "malleability", MALLEABILITY.getDefaultValue());
            Property hardness = propertyConfig.get(pListName, "hardness", HARDNESS.getDefaultValue());
            Property luster = propertyConfig.get(pListName, "luster", LUSTER.getDefaultValue());
            Property color = propertyConfig.get(pListName, "color", "0x999999");

            AmalgamPropertyList properties = new AmalgamPropertyList().add(HARDNESS, (float) hardness.getDouble())
                    .add(MALLEABILITY, (float) malleability.getDouble()).add(DENSITY, (float) density.getDouble())
                    .add(LUSTER, (float) luster.getDouble()).add(COLOR, Integer.decode(color.getString()));

            if("oreDict".equals(type.getString())){
                registerOreDictProperties(name.getString(), properties, volume.getInt());
            }else if("itemName".equals(type.getString())){
                registerItemProperties(name.getString(), properties, volume.getInt());
            }else{
                Config.LOG.error("Material type is not oreDict or itemName");
            }
        }
    }

    public static void capturePropertyListConfig(File configDir) {
        propertyCfgFile = new File(configDir, "amalgamOreProperties.cfg");
        propertyConfig = new Configuration(propertyCfgFile, true);
        try {
            propertyConfig.load();
        } catch (Exception e) {
            File dest = new File(propertyCfgFile.getParentFile(), "amalgamOreProperties.cfg.bak");
            if (dest.exists()) {
                dest.delete();
            }
            propertyCfgFile.renameTo(dest);
            FMLLog.log(
                    Level.ERROR,
                    e,
                    "A critical error occured reading the amalgamOreProperties.cfg file, defaults will be used - the invalid file is backed up at amalgamOreProperties.cfg.bak");
        }

        syncPropertyListConfigDefaults();
    }
    
    public static void captureMaterialConfig(File configDir) {
        materialCfgFile = new File(configDir, "amalgamMaterials.cfg");
        materialConfig = new Configuration(materialCfgFile, true);
        try {
            materialConfig.load();
        } catch (Exception e) {
            File dest = new File(materialCfgFile.getParentFile(), "amalgamMaterials.cfg.bak");
            if (dest.exists()) {
                dest.delete();
            }
            materialCfgFile.renameTo(dest);
            FMLLog.log(
                    Level.ERROR,
                    e,
                    "A critical error occured reading the amalgamMaterials.cfg file, defaults will be used - the invalid file is backed up at amalgamMaterials.cfg.bak");
        }

        syncMaterialConfigDefaults();
    }

    /**
     * Synchronizes the local fields with the values in the Configuration object.
     */
    public static void syncMaterialConfigDefaults() {
        Set<String> propertyLists = propertyConfig.getCategoryNames();

        for (String material : materialConfig.getCategoryNames()) {

            Property pList = propertyConfig.get(material, "Property List", "-");

            if (!propertyLists.contains(pList.getString())) {
                addToMaterial(material, "Property List", "-", Type.STRING);
            }
            propertyConfig.get(material, "Volume", 9);
            propertyConfig.get(material, "Type", "oreDict");
        }

        if (materialConfig.hasChanged()) {
            materialConfig.save();
        }
    }

    /**
     * Synchronizes the local fields with the values in the Configuration object.
     */
    public static void syncPropertyListConfigDefaults() {
        Set<String> propertyLists = propertyConfig.getCategoryNames();

        for (String propertyList : propertyLists) {
            propertyConfig.get(propertyList, "malleability", MALLEABILITY.getDefaultValue());
            propertyConfig.get(propertyList, "density", DENSITY.getDefaultValue());
            propertyConfig.get(propertyList, "luster", LUSTER.getDefaultValue());
            propertyConfig.get(propertyList, "hardness", HARDNESS.getDefaultValue());
        }

        if (propertyConfig.hasChanged()) {
            propertyConfig.save();
        }
    }

    public static Configuration getPropertyListConfig() {
        return propertyConfig;
    }

    public static Configuration getMaterialConfig() {
        return materialConfig;
    }

    public static List<ConfigCategory> getAllPropertyLists() {
        List<ConfigCategory> list = new ArrayList<ConfigCategory>();
        for (String pList : propertyConfig.getCategoryNames()) {
            list.add(propertyConfig.getCategory(pList));
        }
        return list;
    }

    public static List<ConfigCategory> getAllMaterials() {
        List<ConfigCategory> list = new ArrayList<ConfigCategory>();
        for (String mat : materialConfig.getCategoryNames()) {
            list.add(materialConfig.getCategory(mat));
        }
        return list;
    }

    public static ConfigCategory getPropertyList(String pList) {
        if (pList != null) {
            return propertyConfig.getCategory(pList);
        }

        return null;
    }

    public static ConfigCategory getMaterial(String mat) {
        if (mat != null) {
            return materialConfig.getCategory(mat);
        }

        return null;
    }

    public static void addToPropertyList(String pList, String propertyName, String value, Property.Type type) {
        if (pList != null) {
            ConfigCategory cat = propertyConfig.getCategory(pList);
            Property prop = new Property(propertyName, value, type);
            if (type == Property.Type.INTEGER) {
                prop.setMinValue(0);
            }
            cat.put(propertyName, prop);
        }
    }

    public static void addToMaterial(String mat, String propertyName, String value, Property.Type type) {
        if (mat != null) {
            ConfigCategory cat = materialConfig.getCategory(mat);
            Property prop = new Property(propertyName, value, type);
            if (type == Property.Type.INTEGER) {
                prop.setMinValue(0);
            }
            cat.put(propertyName, prop);
        }
    }
}
