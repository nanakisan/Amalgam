package amalgam.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamSword extends ItemSword implements ICastItem {

    public ItemAmalgamSword() {
        super(ItemAmalgamTool.toolMatAmalgam);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamSword");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List dataList, boolean b) {
        dataList.add((getMaxDamage(stack) - getDamage(stack)) + "/" + getMaxDamage(stack));
        dataList.add(EnumChatFormatting.DARK_GREEN + "+" + getItemEnchantability(stack) + " Enchantability");
        dataList.add(EnumChatFormatting.BLUE + "+" + (int) getDamageVsEntity(stack) + " Attack Damage");
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
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

    public float getDamageVsEntity(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return 0.0F;
        }
        return stack.getTagCompound().getInteger(ItemAmalgamTool.DAMAGE_TAG);
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
    public Multimap getItemAttributeModifiers() {
        return HashMultimap.create();
    }

    @Override
    public ItemStack generateStackWithProperties(PropertyList pList, ItemStack[] materials, int stackSize) {
        ItemStack returnStack = new ItemStack(this, stackSize);
        NBTTagCompound toolTag = new NBTTagCompound();

        if (pList == null) {
            returnStack.setTagCompound(toolTag);
            return returnStack;
        }

        float luster = pList.getValue(PropertyManager.LUSTER);
        float density = pList.getValue(PropertyManager.DENSITY);
        float hardness = pList.getValue(PropertyManager.HARDNESS);
        float maliability = pList.getValue(PropertyManager.MALIABILITY);

        toolTag.setInteger(ItemAmalgamTool.ENCHANTABILITY_TAG, (int) (luster));
        int maxDurability = (int) ((density * density) * (hardness + 1));

        toolTag.setInteger(ItemAmalgamTool.DURABILITY_TAG, maxDurability);
        float damage = maliability + 4;
        toolTag.setFloat(ItemAmalgamTool.DAMAGE_TAG, damage);

        returnStack.setTagCompound(toolTag);
        return returnStack;
    }

}
