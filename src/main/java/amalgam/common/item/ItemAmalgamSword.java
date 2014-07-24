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
		// Not sure how to do enchantability of efficiency yet
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
		
		return (float)(pList.getValue(PropertyManager.Hardness));
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
		return (float)(density * density);
	}
	
	public static float getEfficiencyFromStack(ItemStack stack){
		PropertyList pList = new PropertyList();
		pList.readFromNBT(stack.getTagCompound());
		
		return (float)(pList.getValue(PropertyManager.Magnetism));
	}
	
	public boolean canHarvestBlock(ItemStack stack, int meta, Block block, EntityPlayer player){
		if(block.getHarvestLevel(meta) < ItemAmalgamSword.getHarvestLevelFromStack(stack)){
			return true;
		}
		return false;
	}

}

// ItemSword
