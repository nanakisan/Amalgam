package amalgam.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import amalgam.common.Config;
import amalgam.common.fluid.IAmalgableItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
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

    public void setProperties(ItemStack stack, PropertyList properties) {
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
    public PropertyList getProperties(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();

        PropertyList properties = new PropertyList();
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
                return (int) this.getProperties(stack).getValue(PropertyManager.COLOR);
            } else {
                return (int) PropertyManager.COLOR.getDefaultValue();
            }
        }

        return -1;
    }
}
