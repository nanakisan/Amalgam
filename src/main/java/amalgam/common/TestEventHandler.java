package amalgam.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TestEventHandler {
	
	public TestEventHandler(){
	}

	@SubscribeEvent
	public void onDamage(LivingAttackEvent event){
		float a = event.ammount;
		EntityLivingBase target = event.entityLiving;
		DamageSource source = event.source;
		
		if(source.getSourceOfDamage() != null){
			Amalgam.log.error(target.toString() + " " + source.damageType + ":" + a + " " + source.getSourceOfDamage().toString());
		}
	}

}
