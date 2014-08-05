package amalgam.common.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;

public class ItemAmalgamPick extends Item implements ICastItem {

    public ItemAmalgamPick() {
        super();
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamPick");
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entityBeingHit, EntityLivingBase entityHitting) {
        EntityPlayer player = (EntityPlayer) entityHitting;
        float damage = stack.getTagCompound().getInteger("Damage");
        DamageSource damageSource = DamageSource.causePlayerDamage(player);
        entityBeingHit.attackEntityFrom(damageSource, (int) damage);

        stack.damageItem(2, entityHitting);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity) {
        if ((double) block.getBlockHardness(world, x, y, z) != 0.0D) {
            stack.damageItem(1, entity);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List dataList, boolean b) {
        dataList.add("Duarbility: " + (getMaxDamage(stack) - getDamage(stack)) + "/" + getMaxDamage(stack));
        dataList.add("Harvest Level: " + stack.getTagCompound().getInteger("HarvestLevel"));
        dataList.add("Efficiency: " + stack.getTagCompound().getInteger("Efficiency"));
        dataList.add("Enchantability: " + stack.getTagCompound().getInteger("Enchantability"));
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return stack.getTagCompound().getInteger("MaxDurability");
    }

    @Override
    public int getDamage(ItemStack stack) {
        return stack.getTagCompound().getInteger("Durability");
    }

    @Override
    public void setDamage(ItemStack stack, int amount) {

        if (amount < 0) {
            amount = 0;
        }

        stack.getTagCompound().setInteger("Durability", amount);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return (double) getDamage(stack) / (double) getMaxDamage(stack);
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        if ("pickaxe".equals(toolClass)) {
            return stack.getTagCompound().getInteger("HarvestLevel");
        }

        return -1;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int metadata) {
        return stack.getTagCompound().getInteger("Efficiency");
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

        NBTTagCompound swordTag = new NBTTagCompound();

        swordTag.setInteger("HarvestLevel", (int) (hardness - 0.5));
        swordTag.setInteger("Enchantability", (int) (luster));
        swordTag.setInteger("Efficiency", (int) ((3 * luster * luster + 1.5 * density * density) / 120.0) + 1);
        int maxDurability = (int) ((density * density) * hardness);
        swordTag.setInteger("MaxDurability", maxDurability);
        swordTag.setInteger("Durability", 0);

        returnStack.setTagCompound(swordTag);
        return returnStack;
    }

}
