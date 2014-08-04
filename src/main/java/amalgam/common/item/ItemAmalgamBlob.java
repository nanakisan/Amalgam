package amalgam.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import amalgam.common.Amalgam;
import amalgam.common.fluid.IAmalgableItem;
import amalgam.common.properties.PropertyList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamBlob extends Item implements IAmalgableItem {

    public ItemAmalgamBlob() {
        super();
        this.setCreativeTab(Amalgam.tab);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamBlob");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List dataList, boolean b) {
        dataList.add(this.getProperties(stack).toString());
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
}
