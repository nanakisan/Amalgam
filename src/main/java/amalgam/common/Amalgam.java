package amalgam.common;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import amalgam.common.block.BlockStoneCrucible;
import amalgam.common.item.ItemAmalgamBlob;
import amalgam.common.item.ItemStoneTongs;
import amalgam.common.item.ItemStoneTongsFull;
import amalgam.common.properties.PropertyManager;
import amalgam.common.tile.TileStoneCrucible;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Amalgam.MODID, version = Amalgam.VERSION)
public class Amalgam{
    public static final String MODID = "amalgam";
    public static final String VERSION = "0.0.1";
    
    public static final int BASEAMOUNT = 10;
    
    public static final Logger log = LogManager.getLogger("Amalgam");
    
    @Instance("Amalgam")
    public static Amalgam instance;
    
    @SidedProxy(clientSide="amalgam.client.ClientProxy", serverSide="amalgam.common.CommonProxy")
    public static CommonProxy proxy;
    
    public static PropertyManager propertyManager = PropertyManager.getInstance();
	public static Fluid fluidAmalgam;
	public static Block stoneCrucible;
	public static Item stoneTongs;
	public static Item stoneTongsFull;
	public static Item amalgamBlob;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    	fluidAmalgam = new Fluid("Amalgam");
    	FluidRegistry.registerFluid(fluidAmalgam);
    	
    	stoneCrucible = new BlockStoneCrucible().setBlockName("stoneCrucible");
    	GameRegistry.registerBlock(stoneCrucible, "StoneCrucible");
    	
    	stoneTongs = new ItemStoneTongs().setUnlocalizedName("stoneTongs");
    	stoneTongsFull = new ItemStoneTongsFull().setUnlocalizedName("stoneTongsFull");
    	amalgamBlob = new ItemAmalgamBlob().setUnlocalizedName("amalgamBlob");
    	GameRegistry.registerItem(stoneTongs, "stoneTongs");
    	GameRegistry.registerItem(stoneTongsFull, "stoneTongsFull");
    	GameRegistry.registerItem(amalgamBlob, "amalgamBlob");
    	
    	GameRegistry.registerTileEntity(TileStoneCrucible.class, "stoneCrucible");
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event){
		PropertyManager.registerItemProperties(new ItemStack(Items.iron_ingot), ToolMaterial.IRON, BASEAMOUNT*8);
		PropertyManager.registerItemProperties(new ItemStack(Items.gold_ingot), ToolMaterial.GOLD, BASEAMOUNT*8);
		PropertyManager.registerItemProperties(new ItemStack(Items.diamond), ToolMaterial.EMERALD, BASEAMOUNT*8);
		PropertyManager.registerItemProperties(new ItemStack(Blocks.iron_block), ToolMaterial.IRON, BASEAMOUNT*72);
		PropertyManager.registerItemProperties(new ItemStack(Blocks.gold_block), ToolMaterial.GOLD, BASEAMOUNT*72);
		PropertyManager.registerItemProperties(new ItemStack(Blocks.diamond_block), ToolMaterial.EMERALD, BASEAMOUNT*72);
		
		PropertyManager.registerItemProperties(new ItemStack(amalgamBlob));
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
    	
    }

}