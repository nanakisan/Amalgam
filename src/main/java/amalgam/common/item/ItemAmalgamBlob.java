package amalgam.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import amalgam.common.Config;
import amalgam.common.fluid.IAmalgableItem;
import amalgam.common.properties.AmalgamPropertyList;
import amalgam.common.properties.AmalgamPropertyManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamBlob extends Item implements IAmalgableItem {

    public ItemAmalgamBlob() {
        super();
        this.setCreativeTab(Config.tab);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamBlob");
    }

    public void setProperties(ItemStack stack, AmalgamPropertyList properties) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        properties.writeToNBT(tag);
    }

    public void setVolume(ItemStack stack, int volume) {
        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }

        tag.setInteger("Volume", volume);
    }

    @Override
    public AmalgamPropertyList getProperties(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();

        AmalgamPropertyList properties = new AmalgamPropertyList();
        properties.readFromNBT(tag);

        return properties;
    }

    @Override
    public int getVolume(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            return tag.getInteger("Volume");
        }

        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (stack.getTagCompound() != null) {
            if (Config.coloredAmalgam) {
                return (int) this.getProperties(stack).getValue(AmalgamPropertyManager.COLOR);
            } else {
                return (int) AmalgamPropertyManager.COLOR.getDefaultValue();
            }
        }

        return -1;
    }
}
