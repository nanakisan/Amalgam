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

    public static final String ABSORB_TAG = "absorb max";
    public IIcon               iconHelmet;
    public IIcon               iconChest;
    public IIcon               iconLegs;
    public IIcon               iconBoots;

    public ItemAmalgamArmor(ArmorMaterial mat, int renderIndex, int armorType) {
        super(mat, renderIndex, armorType);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        this.iconHelmet = iconRegister.registerIcon("amalgam:amalgamHelmet");
        this.iconChest = iconRegister.registerIcon("amalgam:amalgamChest");
        this.iconLegs = iconRegister.registerIcon("amalgam:amalgamLegs");
        this.iconBoots = iconRegister.registerIcon("amalgam:amalgamBoots");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        switch (this.armorType) {
            case 0:
                return this.iconHelmet;
            case 1:
                return this.iconChest;
            case 2:
                return this.iconLegs;
            case 3:
                return this.iconBoots;
            default:
                return null;
        }
    }

    public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, String type) {
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

        int maxDurability = (int) (density * 26 * armorTypeFactor);
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

        toolTag.setInteger(ABSORB_TAG, (int) Math.ceil((maliability + hardness + 3.0) * armorTypeFactor));

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

}
