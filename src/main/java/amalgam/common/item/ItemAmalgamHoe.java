package amalgam.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import amalgam.common.Config;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamHoe extends ItemHoe implements ICastItem {

    private IIcon hilt;

    public ItemAmalgamHoe() {
        super(ItemAmalgamTool.toolMatAmalgam);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamHoeBlade");
        this.hilt = iconRegister.registerIcon("amalgam:amalgamHoeHilt");
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return 1;
        }

        return stack.getTagCompound().getInteger(ItemAmalgamTool.DURABILITY_TAG);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return 0;
        }

        return stack.getTagCompound().getInteger(ItemAmalgamTool.ENCHANTABILITY_TAG);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List dataList, boolean b) {
        dataList.add((getMaxDamage(stack) - getDamage(stack)) + "/" + getMaxDamage(stack));
        dataList.add(EnumChatFormatting.DARK_GREEN + "+" + getItemEnchantability(stack) + " Enchantability");
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
        float maliability = pList.getValue(PropertyManager.MALIABILITY);
        float hardness = pList.getValue(PropertyManager.HARDNESS);
        int color = (int) pList.getValue(PropertyManager.COLOR);

        toolTag.setInteger(ItemAmalgamTool.COLOR_TAG, color);
        toolTag.setInteger(ItemAmalgamTool.ENCHANTABILITY_TAG, (int) (density * .1 + hardness * .2 + luster * 2.5 + maliability * .3));
        int maxDurability = (int) (density * 200.0F + hardness * 75.0F + luster * 2.0F + maliability * 3.0F);
        toolTag.setInteger(ItemAmalgamTool.DURABILITY_TAG, maxDurability);
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
