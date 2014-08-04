package amalgam.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import amalgam.common.Amalgam;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmalgamSword extends Item implements ICastItem {

    public ItemAmalgamSword() {
        super();
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public boolean func_150897_b(Block block) {
        return block == Blocks.web;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entityBeingHit, EntityLivingBase entityHitting) {
        EntityPlayer player = (EntityPlayer) entityHitting;
        float damage = getWeaponDamageFromStack(stack);
        DamageSource damageSource = DamageSource.causePlayerDamage(player);
        entityBeingHit.attackEntityFrom(damageSource, (int) damage);

        Amalgam.LOG.info("hitEntity");
        stack.damageItem(1, entityHitting);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity) {
        if ((double) block.getBlockHardness(world, x, y, z) != 0.0D) {
            Amalgam.LOG.info("blockDestoryed");
            stack.damageItem(2, entity);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List dataList, boolean b) {
        dataList.add("Duarbility: " + (getMaxDamage(stack) - getDamage(stack)) + "/" + getMaxDamage(stack));
        dataList.add("Damage: " + getWeaponDamageFromStack(stack));
    }

    public boolean canHarvestBlock(ItemStack stack, int meta, Block block, EntityPlayer player) {
        if (block.getHarvestLevel(meta) <= ItemAmalgamSword.getHarvestLevelFromStack(stack)) {
            return true;
        }
        return false;
    }

    public static int getWeaponDamageFromStack(ItemStack stack) {
        return stack.getTagCompound().getInteger("Damage");
    }

    public static int getHarvestLevelFromStack(ItemStack stack) {
        return stack.getTagCompound().getInteger("HarvestLevel");
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

        Amalgam.LOG.info("setting item durability to " + amount);
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
        swordTag.setInteger("HarvestLevel", (int) (hardness - 0.5));
        int maxDurability = (int) ((density * density) * hardness);
        swordTag.setInteger("MaxDurability", maxDurability);
        swordTag.setInteger("Durability", 0);

        returnStack.setTagCompound(swordTag);
        return returnStack;
    }
}
