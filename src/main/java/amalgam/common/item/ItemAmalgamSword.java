package amalgam.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamSword extends Item implements ICastItem {

    public ItemAmalgamSword() {
        super();
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:amalgamSword");
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entityBeingHit, EntityLivingBase entityHitting) {
        EntityPlayer player = (EntityPlayer) entityHitting;
        float damage = stack.getTagCompound().getInteger("Damage");
        DamageSource damageSource = DamageSource.causePlayerDamage(player);
        entityBeingHit.attackEntityFrom(damageSource, (int) damage);

        stack.damageItem(1, entityHitting);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity) {
        if ((double) block.getBlockHardness(world, x, y, z) != 0.0D) {

            stack.damageItem(2, entity);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List dataList, boolean b) {
        dataList.add("Duarbility: " + (getMaxDamage(stack) - getDamage(stack)) + "/" + getMaxDamage(stack));
        dataList.add("Damage: " + stack.getTagCompound().getInteger("Damage"));
        dataList.add("Enchantability: " + stack.getTagCompound().getInteger("Enchantability"));
    }

    public boolean canHarvestBlock(ItemStack stack, int meta, Block block, EntityPlayer player) {
        return block == Blocks.web;
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
    public ItemStack generateStackWithProperties(PropertyList pList, int stackSize) {
        if (pList == null) {
            return null;
        }

        float luster = pList.getValue(PropertyManager.LUSTER);
        float density = pList.getValue(PropertyManager.DENSITY);
        float hardness = pList.getValue(PropertyManager.HARDNESS);
        float maliability = pList.getValue(PropertyManager.MALIABILITY);

        ItemStack returnStack = new ItemStack(this, stackSize);

        NBTTagCompound swordTag = new NBTTagCompound();

        swordTag.setInteger("Damage", (int) maliability);
        swordTag.setInteger("Enchantability", (int) (luster));
        int maxDurability = (int) ((density * density) * hardness);
        swordTag.setInteger("MaxDurability", maxDurability);
        swordTag.setInteger("Durability", 0);

        returnStack.setTagCompound(swordTag);
        return returnStack;
    }
}
