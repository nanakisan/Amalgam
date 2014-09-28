package amalgam.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.ISpecialArmor;
import amalgam.common.Amalgam;
import amalgam.common.Config;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamArmor extends ItemArmor implements ICastItem, ISpecialArmor {

    public static final String ABSORB_TAG = "absorbMax";
    public IIcon[]             icons      = new IIcon[8];

    public ItemAmalgamArmor(ArmorMaterial mat, int renderIndex, int armorType) {
        super(mat, renderIndex, armorType);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        this.icons[0] = iconRegister.registerIcon("amalgam:amalgamHelmet");
        this.icons[1] = iconRegister.registerIcon("amalgam:amalgamHelmetOverlay");
        this.icons[2] = iconRegister.registerIcon("amalgam:amalgamChestplate");
        this.icons[3] = iconRegister.registerIcon("amalgam:amalgamChestplateOverlay");
        this.icons[4] = iconRegister.registerIcon("amalgam:amalgamLeggings");
        this.icons[5] = iconRegister.registerIcon("amalgam:amalgamLeggingsOverlay");
        this.icons[6] = iconRegister.registerIcon("amalgam:amalgamBoots");
        this.icons[7] = iconRegister.registerIcon("amalgam:amalgamBootsOverlay");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return icons[armorType * 2 + pass];
    }

    public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, String type) {
        if ("overlay".equals(type)) {
            if (itemstack.getItem() == Config.amalgamLegs) {
                return Amalgam.MODID + ":textures/models/armor/amalgamOverlay2.png";
            }
            return Amalgam.MODID + ":textures/models/armor/amalgamOverlay1.png";
        }

        if (itemstack.getItem() == Config.amalgamLegs) {
            return Amalgam.MODID + ":textures/models/armor/amalgamLayer2.png";
        }

        return Amalgam.MODID + ":textures/models/armor/amalgamLayer1.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List dataList, boolean b) {
        dataList.add((getMaxDamage(stack) - getDamage(stack)) + "/" + getMaxDamage(stack));
        dataList.add(EnumChatFormatting.DARK_GREEN + "+" + getItemEnchantability(stack) + " Enchantability");
        dataList.add(EnumChatFormatting.DARK_GREEN + "+" + getArmorDisplay(player, stack, 1) + " Armor");
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

    /**
     * Return the color for the specified armor ItemStack.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        if (renderPass == 1) {
            return 0xFFFFFF;
        }

        if (!Config.coloredAmalgam) {
            return (int) PropertyManager.COLOR.getDefaultValue();
        }

        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(ItemAmalgamTool.COLOR_TAG)) {
            return stack.stackTagCompound.getInteger(ItemAmalgamTool.COLOR_TAG);
        }

        return 0xFFFFFF;
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
        float armorTypeFactor = 1.0F;

        switch (this.armorType) {
            case 0:
                armorTypeFactor = 11.0F / 16F;
                break;
            case 1:
                armorTypeFactor = 1.0F;
                break;
            case 2:
                armorTypeFactor = 15.0F / 16.0F;
                break;
            default:
                armorTypeFactor = 13.0F / 16.0F;
                break;
        }

        int maxDurability = (int) (density * 200.0F + hardness * 75.0F + luster * 2.0F + malleability * 3.0F);
        toolTag.setInteger(ItemAmalgamTool.DURABILITY_TAG, maxDurability);

        switch (this.armorType) {
            case 0:
                armorTypeFactor = 3.0F / 8.0F;
                break;
            case 1:
                armorTypeFactor = 1.0F;
                break;
            case 2:
                armorTypeFactor = 6.0F / 8.0F;
                break;
            default:
                armorTypeFactor = 3.0F / 8.0F;
                break;
        }

        toolTag.setInteger(ABSORB_TAG, (int) (density * .2 + hardness * .2 + luster * .05 + malleability * .6));
        returnStack.setTagCompound(toolTag);

        return returnStack;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        float absorbRatio = armor.getTagCompound().getInteger(ABSORB_TAG) * .04F;
        int absorbMax = (int) (absorbRatio * 20);
        int priority = slot;

        return new ArmorProperties(priority, absorbRatio, absorbMax);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        if (armor.getTagCompound() == null) {
            return 1;
        }

        return armor.getTagCompound().getInteger(ABSORB_TAG);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        stack.damageItem(1, entity);
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        return true;
    }

    public int getColor(ItemStack stack) {
        if (!Config.coloredAmalgam) {
            return (int) PropertyManager.COLOR.getDefaultValue();
        }

        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(ItemAmalgamTool.COLOR_TAG)) {
            return stack.stackTagCompound.getInteger(ItemAmalgamTool.COLOR_TAG);
        }

        return 0xFFFFFF;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

}
