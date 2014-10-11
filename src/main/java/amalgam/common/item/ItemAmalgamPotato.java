package amalgam.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import amalgam.common.Config;
import amalgam.common.casting.ICastItem;
import amalgam.common.entity.EntityAmalgamPotato;
import amalgam.common.properties.AmalgamPropertyList;
import amalgam.common.properties.AmalgamPropertyManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamPotato extends Item implements ICastItem {

    public static final String  EXPLOSION_TAG = "exp";
    public static final String  VELOCITY_TAG  = "vel";
    public static final String  DAMAGE_TAG    = "dmg";

    IIcon                       base;

    private static final String COLOR_TAG     = "color";

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamPotatoOverlay");
        this.base = iconRegister.registerIcon("amalgam:amalgamPotatoBase");
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }

        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote) {
            NBTTagCompound comp = stack.stackTagCompound;
            if (comp != null) {
                float damage = comp.getFloat(DAMAGE_TAG);
                float velocity = comp.getFloat(VELOCITY_TAG);
                float explosion = comp.getFloat(EXPLOSION_TAG);
                int color = comp.getInteger(COLOR_TAG);
                world.spawnEntityInWorld(new EntityAmalgamPotato(world, player, velocity, explosion, damage, color));
            }
        }

        return stack;
    }

    @Override
    public ItemStack generateStackWithProperties(AmalgamPropertyList pList, ItemStack[] items, int stackSize) {
        ItemStack returnStack = new ItemStack(this, stackSize);
        NBTTagCompound toolTag = new NBTTagCompound();

        if (pList == null) {
            toolTag.setInteger(COLOR_TAG, (int) AmalgamPropertyManager.COLOR.getDefaultValue());
            returnStack.setTagCompound(toolTag);
            return returnStack;
        }

        float luster = pList.getValue(AmalgamPropertyManager.LUSTER);
        float density = pList.getValue(AmalgamPropertyManager.DENSITY);
        float hardness = pList.getValue(AmalgamPropertyManager.HARDNESS);
        float malleability = pList.getValue(AmalgamPropertyManager.MALLEABILITY);
        int color = (int) pList.getValue(AmalgamPropertyManager.COLOR);

        float v = (float) (0.2F + (2.5F / density));

        if (v > 1.5) {
            v = 1.5F;
        }
        toolTag.setFloat(VELOCITY_TAG, v);
        toolTag.setFloat(EXPLOSION_TAG, (luster / 7.0F) + 0.5F);
        toolTag.setFloat(DAMAGE_TAG, (malleability / 4.0F));

        toolTag.setInteger(COLOR_TAG, color);
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
                return (int) AmalgamPropertyManager.COLOR.getDefaultValue();
            }

            if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("color")) {
                return stack.stackTagCompound.getInteger("color");
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

        return this.base;
    }
}
