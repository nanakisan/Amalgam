package amalgam.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import amalgam.common.Amalgam;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;

public class ItemAmalgamArmor extends ItemArmor implements ICastItem, ISpecialArmor {

    public static final String ABSORB_TAG = "absorb max";
    
    public ItemAmalgamArmor(ArmorMaterial mat, int renderIndex, int armorType) {
        super(mat, renderIndex, armorType);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return 0;
        }
        return stack.getTagCompound().getInteger(ItemAmalgamTool.ENCHANTABILITY_TAG);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return 1;
        }
        return stack.getTagCompound().getInteger(ItemAmalgamTool.DURABILITY_TAG);
    }

    @Override
    public ItemStack generateStackWithProperties(PropertyList pList, int stackSize) {
        if (pList == null) {
            return null;
        }

        float luster = pList.getValue(PropertyManager.LUSTER);
        float density = pList.getValue(PropertyManager.DENSITY);
        float hardness = pList.getValue(PropertyManager.HARDNESS);
        float maliability = pList.getValue(PropertyManager.MALIABILITY);

        ItemStack returnStack = new ItemStack(this, stackSize);

        NBTTagCompound toolTag = new NBTTagCompound();

        toolTag.setInteger(ItemAmalgamTool.ENCHANTABILITY_TAG, (int) (luster));
        int maxDurability = (int) ((density * density) * hardness);
        Amalgam.LOG.info("max durability: " + maxDurability);
        toolTag.setInteger(ItemAmalgamTool.DURABILITY_TAG, maxDurability);
        toolTag.setInteger(ABSORB_TAG, (int)(maliability / 2.0));

        returnStack.setTagCompound(toolTag);
        return returnStack;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        float absorbRatio = armor.getTagCompound().getInteger(ABSORB_TAG) * .04F;
        int absorbMax = (int)(absorbRatio * 20);
        // TODO right now priority is based on slot, might want to base it on something else
        int priority = slot;
        return new ArmorProperties(priority, absorbRatio, absorbMax);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return armor.getTagCompound().getInteger(ABSORB_TAG);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        stack.damageItem(1, entity);
    }

}
