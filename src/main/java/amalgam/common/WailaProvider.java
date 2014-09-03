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
import amalgam.common.tile.TileAmalgamContainer;

public class WailaProvider implements IWailaDataProvider {

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
        TileEntity te = accessor.getTileEntity();
        currenttip.add("test");
        if (te instanceof TileAmalgamContainer) {
            PropertyList l = ((TileAmalgamContainer) te).getAmalgamPropertyList();
            int currentVolume = ((TileAmalgamContainer) te).getFluidVolume();
            int capacity = ((TileAmalgamContainer) te).getTankCapacity();
            currenttip.add("Volume: " + currentVolume + "/" + capacity);

            // FIXME if we are holding an amalgable item and look at the crucible, show what the amalgam properties
            // would
            // be upon adding the item. For example (Density: 4.5 -> 5.1)

            if (currentVolume != 0 && accessor.getPlayer().isSneaking()) {
                currenttip.add("--------");
                addPropertiesToTooltip(currenttip, l);
            }
        }

        return currenttip;
    }

    public void addPropertiesToTooltip(List<String> currentTip, PropertyList pList) {
        for (Property p : Property.getAll()) {
            if (p == PropertyManager.COLOR) {
                continue;
            }

            currentTip.add(p.getName() + ": " + SpecialChars.WHITE + String.format("%.1f", pList.getValue(p)));
        }
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    public static void callbackRegister(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new WailaProvider(), Block.class);
    }

}
