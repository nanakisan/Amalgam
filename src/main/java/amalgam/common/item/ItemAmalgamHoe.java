package amalgam.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import amalgam.common.Amalgam;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamHoe extends ItemHoe implements ICastItem {

    public ItemAmalgamHoe() {
        super(ItemAmalgamTool.toolMatAmalgam);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamHoe");
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
    public ItemStack generateStackWithProperties(PropertyList pList, int stackSize) {
        if (pList == null) {
            return null;
        }

        float luster = pList.getValue(PropertyManager.LUSTER);
        float density = pList.getValue(PropertyManager.DENSITY);
        float hardness = pList.getValue(PropertyManager.HARDNESS);

        ItemStack returnStack = new ItemStack(this, stackSize);

        NBTTagCompound toolTag = new NBTTagCompound();

        toolTag.setInteger(ItemAmalgamTool.ENCHANTABILITY_TAG, (int) (luster));
        int maxDurability = (int) ((density * density) * hardness);
        toolTag.setInteger(ItemAmalgamTool.DURABILITY_TAG, maxDurability);

        returnStack.setTagCompound(toolTag);
        return returnStack;
    }
}
