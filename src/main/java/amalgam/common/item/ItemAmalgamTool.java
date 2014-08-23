package amalgam.common.item;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.EnumHelper;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;

import com.google.common.collect.HashMultimap;
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
    public static final String      COLOR_TAG          = "color";

    public static final String      TOOL_CLASS_PICK    = "pickaxe";
    public static final String      TOOL_CLASS_AXE     = "axe";
    public static final String      TOOL_CLASS_SHOVEL  = "shovel";

    private final String            toolClass;
    private final float             damageMod;

    protected ItemAmalgamTool(float damageMod, String toolClass, Set<Block> blocks) {
        super(damageMod, toolMatAmalgam, blocks);
        this.toolClass = toolClass;
        this.damageMod = damageMod;
    }

    // FIXME in order to have only the head of tools change color we will need to do multiple rendering passes
    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (stack.getTagCompound() != null) {
            return stack.getTagCompound().getInteger(COLOR_TAG);
        }
        return -1;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entityBeingHit, EntityLivingBase entityHitting) {
        EntityPlayer player = (EntityPlayer) entityHitting;
        float damage = getDamageVsEntity(stack);
        DamageSource damageSource = DamageSource.causePlayerDamage(player);
        entityBeingHit.attackEntityFrom(damageSource, (int) damage);

        return super.hitEntity(stack, entityBeingHit, entityHitting);
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        if (ForgeHooks.isToolEffective(stack, block, meta)) {
            return getEfficiency(stack);
        }
        return super.getDigSpeed(stack, block, meta);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        if (toolClass != null && toolClass.equals(this.toolClass)) {
            if (stack.getTagCompound() == null) {
                return 0;
            }
            return stack.getTagCompound().getInteger(HARVEST_TAG);
        } else {
            return super.getHarvestLevel(stack, toolClass);
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return 1;
        }
        return stack.getTagCompound().getInteger(DURABILITY_TAG);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return 0;
        }
        return stack.getTagCompound().getInteger(ENCHANTABILITY_TAG);
    }

    public float getDamageVsEntity(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return 0.0F;
        }
        return stack.getTagCompound().getInteger(ItemAmalgamTool.DAMAGE_TAG);
    }

    public float getEfficiency(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return 1.0F;
        }
        return stack.getTagCompound().getInteger(ItemAmalgamTool.EFFICIENCY_TAG);
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        return HashMultimap.create();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List dataList, boolean b) {

        dataList.add((getMaxDamage(stack) - getDamage(stack)) + "/" + getMaxDamage(stack));
        dataList.add(EnumChatFormatting.DARK_GREEN + "+" + getItemEnchantability(stack) + " Enchantability");
        dataList.add(EnumChatFormatting.DARK_GREEN + "+" + (int) getEfficiency(stack) + " Efficiency");
        dataList.add(EnumChatFormatting.DARK_GREEN + "+" + getHarvestLevel(stack, this.toolClass) + " Harvest Level");
        dataList.add(EnumChatFormatting.BLUE + "+" + (int) getDamageVsEntity(stack) + " Attack Damage");
    }

    @Override
    public ItemStack generateStackWithProperties(PropertyList pList, ItemStack[] materials, int stackSize) {
        ItemStack returnStack = new ItemStack(this, stackSize);
        NBTTagCompound toolTag = new NBTTagCompound();

        if (pList == null) {
            toolTag.setInteger(COLOR_TAG, (int) PropertyManager.COLOR.getDefaultValue());
            returnStack.setTagCompound(toolTag);
            return returnStack;
        }

        float luster = pList.getValue(PropertyManager.LUSTER);
        float density = pList.getValue(PropertyManager.DENSITY);
        float hardness = pList.getValue(PropertyManager.HARDNESS);
        float maliability = pList.getValue(PropertyManager.MALIABILITY);
        int color = (int) pList.getValue(PropertyManager.COLOR);

        toolTag.setInteger(HARVEST_TAG, (int) Math.ceil(hardness));
        toolTag.setInteger(ENCHANTABILITY_TAG, (int) (luster));
        toolTag.setInteger(EFFICIENCY_TAG, (int) ((3 * luster * luster + 1.5 * density * density) / 120.0) + 1);
        int maxDurability = (int) ((density * density) * (hardness + 1));
        toolTag.setInteger(DURABILITY_TAG, maxDurability);
        toolTag.setFloat(DAMAGE_TAG, maliability + this.damageMod);

        toolTag.setInteger(COLOR_TAG, color);
        returnStack.setTagCompound(toolTag);
        return returnStack;
    }
}
