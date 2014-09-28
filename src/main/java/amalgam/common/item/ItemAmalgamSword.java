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
import net.minecraft.util.IIcon;
import amalgam.common.Config;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamSword extends ItemSword implements ICastItem {

    private IIcon hilt;

    public ItemAmalgamSword() {
        super(ItemAmalgamTool.toolMatAmalgam);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamSwordBlade");
        this.hilt = iconRegister.registerIcon("amalgam:amalgamSwordHilt");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List dataList, boolean b) {
        dataList.add((getMaxDamage(stack) - getDamage(stack)) + "/" + getMaxDamage(stack));
        dataList.add(EnumChatFormatting.DARK_GREEN + "+" + getItemEnchantability(stack) + " Enchantability");
        dataList.add(EnumChatFormatting.BLUE + "+" + String.format("%.1f", getDamageVsEntity(stack)) + " Attack Damage");
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

    public float getDamageVsEntity(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return 0.0F;
        }

        return stack.getTagCompound().getFloat(ItemAmalgamTool.DAMAGE_TAG);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entityBeingHit, EntityLivingBase entityHitting) {
        EntityPlayer player = (EntityPlayer) entityHitting;
        float damage = getDamageVsEntity(stack);
        DamageSource damageSource = DamageSource.causePlayerDamage(player);
        entityBeingHit.attackEntityFrom(damageSource, damage);

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
        float malleability = pList.getValue(PropertyManager.MALLEABILITY);
        int color = (int) pList.getValue(PropertyManager.COLOR);

        toolTag.setInteger(ItemAmalgamTool.COLOR_TAG, color);
        toolTag.setInteger(ItemAmalgamTool.ENCHANTABILITY_TAG, (int) (density * .1 + hardness * .2 + luster * 2.5 + malleability * .3));
        // added a resonance between density and hardness. When they are close to the same value you get more durability
        // (up to +1000)
        int maxDurability = (int) (density * 200.0F + hardness * 75.0F + luster * 2.0F + malleability * 3.0F);
        toolTag.setInteger(ItemAmalgamTool.DURABILITY_TAG, maxDurability);
        // added a resonance between luster and malleability. When they are close to the same value you get more damage
        // (up to +3)
        float damage = 4.0F + (float) (hardness * .1 + malleability * .7);
        toolTag.setFloat(ItemAmalgamTool.DAMAGE_TAG, damage);
        returnStack.setTagCompound(toolTag);

        return returnStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        if (renderPass == 1) {
            if (!Config.coloredAmalgam) {
                return (int) PropertyManager.COLOR.getDefaultValue();
            }
            if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(ItemAmalgamTool.COLOR_TAG)) {
                return stack.stackTagCompound.getInteger(ItemAmalgamTool.COLOR_TAG);
            }

            return 0xFFFFFF;
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass) {
        if (renderPass == 1) {
            return this.itemIcon;
        }

        return this.hilt;
    }
}
