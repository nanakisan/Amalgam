package amalgam.common.properties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import amalgam.common.Config;
import amalgam.common.fluid.IAmalgableItem;
import amalgam.common.properties.Property.ComboType;

public final class PropertyManager {

    private static final PropertyManager            INSTANCE          = new PropertyManager();
    private static final Map<Item, List<Object>>    ITEM_REGISTRY     = new HashMap<Item, List<Object>>();
    private static final Map<Integer, List<Object>> ORE_DICT_REGISTRY = new HashMap<Integer, List<Object>>();

    public static final Property                    MALIABILITY       = new Property("Maliability", 1, ComboType.QUADAVERAGE);
    public static final Property                    DENSITY           = new Property("Density", 6, ComboType.QUADAVERAGE);
    public static final Property                    LUSTER            = new Property("Luster", 2, ComboType.QUADAVERAGE);
    public static final Property                    HARDNESS          = new Property("Hardness", 1, ComboType.QUADAVERAGE);

    public static final Property                    COLOR             = new Property("Color", 0x999999, ComboType.COLOR);

    private PropertyManager() {
    }

    public static PropertyManager getInstance() {
        return INSTANCE;
    }

    public static void registerItemProperties(ItemStack stack, PropertyList list, int volume) {
        Item item = stack.getItem();

        if (list != null && volume > 0) {
            List<Object> blob = Arrays.asList(new Object[] { list, Integer.valueOf(volume) });
            ITEM_REGISTRY.put(item, blob);
        } else if (stack.getItem() instanceof IAmalgableItem) {
            ITEM_REGISTRY.put(item, null);
        } else {
            Config.LOG.error("Trying to register an amalgable item with improper values or not implementing IAmalgableItem");
        }
    }

    public static void registerOreDictProperties(String dictName, PropertyList list, int volume) {
        if ("Unkown".equals(dictName)) {
            Config.LOG.error("can't register 'Unkown'");
            return;
        }

        int id = OreDictionary.getOreID(dictName);

        if (list != null && volume > 0) {
            List<Object> blob = Arrays.asList(new Object[] { list, Integer.valueOf(volume) });
            ORE_DICT_REGISTRY.put(id, blob);
        } else {
            Config.LOG.error("Trying to register an ore dictionary entry with improper values");
        }
    }

    public static void registerOreDictProperties(int oreDictID, PropertyList list, int volume) {
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

    public static PropertyList generatePropertiesFromToolMaterial(ToolMaterial mat) {

        PropertyList list = new PropertyList();

        list.add(DENSITY, (float) Math.sqrt(mat.getMaxUses() / (mat.getHarvestLevel() + 1)) / 5.0F);
        list.add(HARDNESS, mat.getHarvestLevel());
        list.add(LUSTER, mat.getEnchantability() / 5.0F);
        list.add(MALIABILITY, mat.getDamageVsEntity());

        return list;
    }

    public static PropertyList generatePropertiesFromArmorMaterial(ArmorMaterial mat) {

        PropertyList list = new PropertyList();

        list.add(DENSITY, mat.getDurability(1) / (27 * 5.0F));
        list.add(HARDNESS, mat.getDamageReductionAmount(1) / 2 - 1);
        list.add(LUSTER, mat.getEnchantability() / 5.0F);
        list.add(MALIABILITY, mat.getDamageReductionAmount(1) / 2 - 1);

        return list;
    }

    public static PropertyList getProperties(ItemStack stack) {
        Item item = stack.getItem();

        // First we check the item registry, then we fall back on the ore dict registry if we find no entry.
        if (ITEM_REGISTRY.containsKey(item)) {
            List<Object> blob = (List<Object>) ITEM_REGISTRY.get(item);

            if (blob == null) {
                return ((IAmalgableItem) item).getProperties(stack);
            }

            return (PropertyList) blob.get(0);
        }

        int ids[] = OreDictionary.getOreIDs(stack);

        for (int i = 0; i < ids.length; i++) {
            if (ORE_DICT_REGISTRY.containsKey(ids[i])) {
                List<Object> blob = ORE_DICT_REGISTRY.get(ids[i]);
                return (PropertyList) blob.get(0);
            }
        }

        return null;
    }

    public static int getVolume(ItemStack stack) {
        Item item = stack.getItem();

        if (ITEM_REGISTRY.containsKey(item)) {
            List<Object> blob = (List<Object>) ITEM_REGISTRY.get(item);

            if (blob == null) {
                return ((IAmalgableItem) item).getVolume(stack);
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
        Item item = stack.getItem();

        if (ITEM_REGISTRY.containsKey(item)) {
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

}
