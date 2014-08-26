package amalgam.common;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import amalgam.common.properties.Property;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;
import amalgam.common.tile.TileCastingTable;
import amalgam.common.tile.TileStoneCrucible;

public class ProviderAmalgam implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        // TODO consolidate WAILA code for the tile entities
        
        TileEntity te = accessor.getTileEntity();
        if (te instanceof TileStoneCrucible) {
            PropertyList l = ((TileStoneCrucible) te).getAmalgamProperties();
            int currentVolume = ((TileStoneCrucible) te).getFluidVolume();
            int capacity = ((TileStoneCrucible) te).getTankCapacity();
            currenttip.add("Volume: " + currentVolume + "/" + capacity);
            
            if (currentVolume != 0 && accessor.getPlayer().isSneaking()) {
                currenttip.add("--------");
                
                for (Property p : Property.getAll()) {
                    if (p == PropertyManager.COLOR) {
                        continue;
                    }

                    currenttip.add(p.getName() + ": " + SpecialChars.WHITE +  String.format("%.1f",l.getValue(p)));
                }
            }
        }

        if (te instanceof TileCastingTable) {
            PropertyList l = ((TileCastingTable) te).getAmalgamPropertyList();
            int currentVolume = ((TileCastingTable) te).getTankAmount();
            int capacity = ((TileCastingTable) te).getCapacity();
            currenttip.add("Volume: " + currentVolume + "/" + capacity);
            
            if (currentVolume != 0 && accessor.getPlayer().isSneaking()) {
                currenttip.add("--------");
                
                for (Property p : Property.getAll()) {
                    if (p == PropertyManager.COLOR) {
                        continue;
                    }

                    currenttip.add(p.getName() + ": " + SpecialChars.WHITE + String.format("%.1f",l.getValue(p)));
                }
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    public static void callbackRegister(IWailaRegistrar registrar) {
        Config.LOG.info("in here !!!!");
        registrar.registerBodyProvider(new ProviderAmalgam(), Block.class);
    }

}
