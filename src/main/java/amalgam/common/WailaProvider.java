package amalgam.common;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.IAmalgamContainerItem;
import amalgam.common.properties.AmalgamProperty;
import amalgam.common.properties.AmalgamPropertyManager;
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
        if (te instanceof TileAmalgamContainer) {
            AmalgamStack amalgStack = ((TileAmalgamContainer) te).getAmalgamStack();
            int capacity = ((TileAmalgamContainer) te).getTankCapacity();
            currenttip.add("Volume: " + amalgStack.amount + "/" + capacity);

            if (amalgStack.amount != 0 && accessor.getPlayer().isSneaking()) {
                currenttip.add("--------");
                addPropertiesToTooltip(currenttip, amalgStack, accessor.getPlayer());
            }
        }

        return currenttip;
    }

    public void addPropertiesToTooltip(List<String> currentTip, AmalgamStack amalgStack, EntityPlayer player) {
        ItemStack currentItem = player.inventory.getCurrentItem();

        if (currentItem != null && AmalgamPropertyManager.itemIsAmalgable(currentItem)) {
            AmalgamStack a = new AmalgamStack(AmalgamPropertyManager.getVolume(currentItem), AmalgamPropertyManager.getProperties(currentItem));
            a = AmalgamStack.combine(a, amalgStack);

            for (AmalgamProperty p : AmalgamProperty.getAll()) {
                if (p == AmalgamPropertyManager.COLOR) {
                    continue;
                }

                float currentValue = amalgStack.getProperties().getValue(p);
                float newValue = a.getProperties().getValue(p);
                currentTip.add(p.getName() + ": " + SpecialChars.WHITE + String.format("%.1f", currentValue) + "->"
                        + (currentValue > newValue ? SpecialChars.RED : SpecialChars.GREEN) + String.format("%.1f", newValue));
            }

        } else if (currentItem != null && currentItem.getItem() instanceof IAmalgamContainerItem) {
            AmalgamStack a = ((IAmalgamContainerItem) currentItem.getItem()).getFluid(currentItem);
            a = AmalgamStack.combine(a, amalgStack);

            for (AmalgamProperty p : AmalgamProperty.getAll()) {
                if (p == AmalgamPropertyManager.COLOR) {
                    continue;
                }

                float currentValue = amalgStack.getProperties().getValue(p);
                float newValue = a.getProperties().getValue(p);
                currentTip.add(p.getName() + ": " + SpecialChars.WHITE + String.format("%.1f", currentValue) + "->"
                        + (currentValue > newValue ? SpecialChars.RED : SpecialChars.GREEN) + String.format("%.1f", newValue));
            }
        } else {
            for (AmalgamProperty p : AmalgamProperty.getAll()) {
                if (p == AmalgamPropertyManager.COLOR) {
                    continue;
                }

                currentTip.add(p.getName() + ": " + SpecialChars.WHITE + String.format("%.1f", amalgStack.getProperties().getValue(p)));
            }
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
