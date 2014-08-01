package amalgam.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import amalgam.common.Amalgam;
import amalgam.common.casting.ICastItem;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;

public class ItemAmalgamSword extends Item implements ICastItem {

    public ItemAmalgamSword() {
        super();
        this.setCreativeTab(Amalgam.tab);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entityBeingHit, EntityLivingBase entityHitting) {
        // I am pretty sure this should work!!!!
        EntityPlayer player = (EntityPlayer) entityHitting;
        float damage = ItemAmalgamSword.getWeaponDamageFromStack(stack);
        DamageSource damageSource = DamageSource.causePlayerDamage(player);
        entityBeingHit.attackEntityFrom(damageSource, (int) damage);

        return true;
    }

    public static int getWeaponDamageFromStack(ItemStack stack) {
        // TODO return a default value if we can't find this entry in the nbt
        return stack.getTagCompound().getInteger("Damage");
    }

    public static float getHarvestLevelFromStack(ItemStack stack) {
        return stack.getTagCompound().getInteger("HarvestLevel");
    }

    public static float getMaxDurabilityFromStack(ItemStack stack) {
        return stack.getTagCompound().getInteger("MaxDurability");
    }

    public boolean canHarvestBlock(ItemStack stack, int meta, Block block, EntityPlayer player) {
        if (block.getHarvestLevel(meta) <= ItemAmalgamSword.getHarvestLevelFromStack(stack)) {
            return true;
        }
        return false;
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

        // Since enchantability can't be determined through NBT tags we need to
        // create separate items for different enchantability levels
        if (luster < 5) {
            // generate sword with enchantability of 5
        } else if (luster < 10) {
            // generate sword with enchantability of 10
        } else if (luster < 15) {
            // generate sword with enchantability of 15
        } else if (luster < 20) {
            // generate sword with enchantability of 20
        } else if (luster < 25) {
            // generate sword with enchantability of 25
        } else {
            // generate sword with enchantability of 30
        }

        ItemStack returnStack = new ItemStack(this, stackSize);

        NBTTagCompound swordTag = new NBTTagCompound();

        swordTag.setInteger("Damage", (int) (maliability + 4.0));
        swordTag.setInteger("HarvestLevel", (int) (hardness - 0.5));
        int maxDurability = (int) ((density * density) * hardness);
        swordTag.setInteger("MaxDurability", maxDurability);
        swordTag.setInteger("Durability", maxDurability);

        returnStack.setTagCompound(swordTag);
        return returnStack;
    }
}
