package amalgam.common.properties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import amalgam.common.Amalgam;
import amalgam.common.fluid.IAmalgableItem;
import amalgam.common.properties.Property.ComboType;

public final class PropertyManager {

    private static final PropertyManager INSTANCE = new PropertyManager();
    private static final Map<Item, List<Object>> REGISTRY = new HashMap<Item, List<Object>>();
    public static final Property MALIABILITY = new Property("Maliability", 4, ComboType.QUADAVERAGE); // used
                                                                                                      // for
                                                                                                      // weapon/armor
                                                                                                      // strength
    public static final Property DENSITY = new Property("Density", 6, ComboType.QUADAVERAGE);
    public static final Property LUSTER = new Property("Luster", 10, ComboType.QUADAVERAGE);
    public static final Property HARDNESS = new Property("Hardness", 1, ComboType.QUADAVERAGE);

    private PropertyManager() {
    }

    public static PropertyManager getInstance() {
        return INSTANCE;
    }

    // Pass null for PropertyList to use an Items NBT tags for properties
    // instead of pre-registered values
    // Volume is generally 9 for an ingot, 1 for a nugget
    public static void registerItemProperties(ItemStack stack, PropertyList list, int volume) {
        Item item = stack.getItem();
        if (list != null && volume > 0) {
            List<Object> blob = Arrays.asList(new Object[] { list, Integer.valueOf(volume) });
            REGISTRY.put(item, blob);
        } else if (stack.getItem() instanceof IAmalgableItem) {
            REGISTRY.put(item, null);
        } else {
            Amalgam.LOG.error("Trying to register an amalgable item with improper values or not implementing IAmalgableItem");
        }
    }

    public static PropertyList generatePropertiesFromToolMaterial(ToolMaterial mat) {
        PropertyList list = new PropertyList();

        list.add(DENSITY, (float) Math.sqrt(mat.getMaxUses() / (mat.getHarvestLevel() + 1)));
        list.add(HARDNESS, mat.getHarvestLevel() + 1);
        list.add(LUSTER, mat.getEnchantability());
        list.add(MALIABILITY, mat.getDamageVsEntity() * 2 + 2);

        return list;
    }

    public static PropertyList generatePropertiesFromArmorMaterial(ArmorMaterial mat) {
        PropertyList list = new PropertyList();

        list.add(DENSITY, mat.getDurability(1) / 11);
        list.add(HARDNESS, mat.getDamageReductionAmount(1) / 2);
        list.add(LUSTER, mat.getEnchantability());
        list.add(MALIABILITY, mat.getDamageReductionAmount(1));

        return list;
    }

    public static PropertyList getProperties(ItemStack stack) {
        Item item = stack.getItem();
        if (!REGISTRY.containsKey(item)) {
            Amalgam.LOG.error("trying to get the properties of an item which has not registered its properties");
            return null;
        }

        List<Object> blob = (List<Object>) REGISTRY.get(item);

        // if the blob is null we get the property list using this
        if (blob == null) {
            return ((IAmalgableItem) item).getProperties(stack);
        }

        // otherwise we get the property list using the registered value
        return (PropertyList) blob.get(0);
    }

    public static int getVolume(ItemStack stack) {
        Item item = stack.getItem();
        List<Object> blob = (List<Object>) REGISTRY.get(item);

        // if the blob is null we get the volume using this
        if (blob == null) {
            return ((IAmalgableItem) item).getVolume(stack);
        }

        // otherwise we get the volume using the registered value
        return (Integer) blob.get(1);
    }

    public static boolean itemIsAmalgable(ItemStack stack) {
        Item item = stack.getItem();
        if (REGISTRY.containsKey(item)) {
            return true;
        }
        return false;
    }
}
