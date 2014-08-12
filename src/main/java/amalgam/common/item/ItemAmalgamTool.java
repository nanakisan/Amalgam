package amalgam.common.item;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.EnumHelper;
import amalgam.common.Amalgam;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamTool extends ItemTool implements ICastItem {

    public static Item.ToolMaterial toolMatAmalgam     = EnumHelper.addToolMaterial("AMALGAM", 0, 1, 0.0F, 0.0F, 0);

    public static final String      DAMAGE_TAG         = "damage";
    public static final String      DURABILITY_TAG     = "durability";
    public static final String      EFFICIENCY_TAG     = "efficiency";
    public static final String      HARVEST_TAG        = "harvest level";
    public static final String      ENCHANTABILITY_TAG = "enchantability";

    public static final String      TOOL_CLASS_PICK    = "pickaxe";
    public static final String      TOOL_CLASS_AXE     = "axe";
    public static final String      TOOL_CLASS_SHOVEL  = "shovel";

    private String                  toolClass;

    protected ItemAmalgamTool(float damageMod, String toolClass, Set<Block> blocks) {
        super(damageMod, toolMatAmalgam, blocks);
        this.toolClass = toolClass;
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        if (ForgeHooks.isToolEffective(stack, block, meta)) {
            return stack.getTagCompound().getInteger(EFFICIENCY_TAG);
        }
        return super.getDigSpeed(stack, block, meta);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        if (toolClass != null && toolClass.equals(this.toolClass)) {
            return stack.getTagCompound().getInteger(HARVEST_TAG);
        } else {
            return super.getHarvestLevel(stack, toolClass);
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return stack.getTagCompound().getInteger(DURABILITY_TAG);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return stack.getTagCompound().getInteger(ENCHANTABILITY_TAG);
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        float damage = stack.getTagCompound().getInteger(DAMAGE_TAG);
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Tool modifier",
                (double) damage, 0));
        return multimap;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List dataList, boolean b) {
        dataList.add("Duarbility: " + (getMaxDamage(stack) - getDamage(stack)) + "/" + getMaxDamage(stack));
        dataList.add("Harvest Level: " + getHarvestLevel(stack, this.toolClass));
        dataList.add("Efficiency: " + stack.getTagCompound().getInteger(EFFICIENCY_TAG));
        dataList.add("Enchantability: " + getItemEnchantability(stack));
    }

    @Override
    public ItemStack generateStackWithProperties(PropertyList pList, int stackSize) {
        if (pList == null) {
            return null;
        }

        float luster = pList.getValue(PropertyManager.LUSTER);
        float density = pList.getValue(PropertyManager.DENSITY);
        float hardness = pList.getValue(PropertyManager.HARDNESS);

        ItemStack returnStack = new ItemStack(this, stackSize);

        NBTTagCompound toolTag = new NBTTagCompound();

        toolTag.setInteger(HARVEST_TAG, (int) (hardness - 0.5));
        toolTag.setInteger(ENCHANTABILITY_TAG, (int) (luster));
        toolTag.setInteger(EFFICIENCY_TAG, (int) ((3 * luster * luster + 1.5 * density * density) / 120.0) + 1);
        int maxDurability = (int) ((density * density) * hardness);
        Amalgam.LOG.info("max durability: " + maxDurability);
        toolTag.setInteger(DURABILITY_TAG, maxDurability);
        // TODO need to add dame here
        toolTag.setInteger(DAMAGE_TAG, 0);

        returnStack.setTagCompound(toolTag);
        return returnStack;
    }
}
