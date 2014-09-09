package amalgam.common.item;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import amalgam.common.Config;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamShroom extends ItemFood implements ICastItem {

    private static final String EFFECT_NUM_TAG   = "num";
    private static final String DURATION_MOD_TAG = "dur";
    private static final String EFFECT_MOD_TAG   = "eff";
    private static final String COLOR_TAG        = "color";

    IIcon                       base;

    public ItemAmalgamShroom() {
        super(1, false);
        this.setAlwaysEdible();
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamShroomTop");
        this.base = iconRegister.registerIcon("amalgam:amalgamShroomBase");
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && stack.hasTagCompound()) {
            // generate random potion effect based of amalgam properties

            Random random = new Random();
            int effects = stack.stackTagCompound.getInteger(EFFECT_NUM_TAG);
            int durMod = stack.stackTagCompound.getInteger(DURATION_MOD_TAG);
            int effMod = stack.stackTagCompound.getInteger(EFFECT_MOD_TAG);

            for (int i = 0; i < effects; i++) {
                int duration = random.nextInt(100) + 100 + 20 * random.nextInt(durMod);
                player.addPotionEffect(new PotionEffect(random.nextInt(22), duration, random.nextInt(effMod)));
            }
        }
    }

    @Override
    public ItemStack generateStackWithProperties(PropertyList pList, ItemStack[] items, int stackSize) {
        ItemStack returnStack = new ItemStack(this, stackSize);
        NBTTagCompound toolTag = new NBTTagCompound();

        if (pList == null) {
            toolTag.setInteger(COLOR_TAG, (int) PropertyManager.COLOR.getDefaultValue());
            returnStack.setTagCompound(toolTag);
            return returnStack;
        }

        float luster = pList.getValue(PropertyManager.LUSTER);
        float density = pList.getValue(PropertyManager.DENSITY);
        float hardness = pList.getValue(PropertyManager.HARDNESS);
        float maliability = pList.getValue(PropertyManager.MALIABILITY);
        int color = (int) pList.getValue(PropertyManager.COLOR);

        toolTag.setInteger(EFFECT_NUM_TAG, (int) (luster / 3.0 + 1));
        toolTag.setInteger(DURATION_MOD_TAG, (int) (density + hardness));
        toolTag.setInteger(EFFECT_MOD_TAG, (int) (maliability / 3.0 + 1));

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
                return (int) PropertyManager.COLOR.getDefaultValue();
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
