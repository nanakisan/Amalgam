package amalgam.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;

public class ItemAmalgamSword extends Item{

	public ItemAmalgamSword() {
		super();
		// TODO figure out how to use NBT tags to determine durability, sword damage, enchantability etc
		// I think my damage implementation will already work, just need to check once I can actually craft things with amalgam!
		// Look at stone tongs for custom item durability display, just need to decrease an NBT durability counter on hits and break when it reaches 0.
		// For enchantability look in EnchantmentHelper, specifically at the calcItemStackEnchantability function, probably need to use reflection
		
		// As far as I can tell, getItemEnchantability is only ever called from EnchantmentHelper in its buildEnchantmentList and calcItemStackEnchantability functions.
		// I am asking for a stack based function, not sure if this is something the forge people could do or if it is out of their hands.
		// if I can't make the enchantability depend on nbt data I will have to make separate items for each level of enchantability (probably 5, 10, 15, 20, 25 and 30)
	}
	
	@Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entityBeingHit, EntityLivingBase entityDoingHitting){
        // get the property list from the stack
		
		// I am pretty sure this should work!!!!
		EntityPlayer player = (EntityPlayer)entityDoingHitting;
		float damage = ItemAmalgamSword.getWeaponDamageFromStack(stack);
		DamageSource damageSource = DamageSource.causePlayerDamage(player);
		entityBeingHit.attackEntityFrom(damageSource, (int)damage);

        return true;
    }

	public static float getWeaponDamageFromStack(ItemStack stack){
		PropertyList pList = new PropertyList();
		pList.readFromNBT(stack.getTagCompound());
		
		return (float)(pList.getValue(PropertyManager.Maliablity) + 4.0);
	}
	
	public static float getHarvestLevelFromStack(ItemStack stack){
		PropertyList pList = new PropertyList();
		pList.readFromNBT(stack.getTagCompound());
		
		return (float)(pList.getValue(PropertyManager.Hardness) - 1.0);
	}
	
	public static float getEnchantabilityFromStack(ItemStack stack){
		PropertyList pList = new PropertyList();
		pList.readFromNBT(stack.getTagCompound());
		
		return (float)(pList.getValue(PropertyManager.Luster));
	}
	
	public static float getMaxDurabilityFromStack(ItemStack stack){
		PropertyList pList = new PropertyList();
		pList.readFromNBT(stack.getTagCompound());
		float density = pList.getValue(PropertyManager.Density);
		float hardness = pList.getValue(PropertyManager.Hardness);
		return (density * density) * hardness;
	}
	
	public static float getEfficiencyFromStack(ItemStack stack){
		PropertyList pList = new PropertyList();
		pList.readFromNBT(stack.getTagCompound());
		
		//return (float)(pList.getValue(PropertyManager.Magnetism));
		return 1;
	}
	
	public boolean canHarvestBlock(ItemStack stack, int meta, Block block, EntityPlayer player){
		if(block.getHarvestLevel(meta) < ItemAmalgamSword.getHarvestLevelFromStack(stack)){
			return true;
		}
		return false;
	}
}

// ItemSword
