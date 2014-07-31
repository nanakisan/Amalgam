package amalgam.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import amalgam.common.Amalgam;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.IAmalgamContainerItem;
import amalgam.common.properties.PropertyList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStoneTongs extends Item implements IAmalgamContainerItem {

    // TODO figure out how to steadily and constantly drain amalgam while
    // holding down the item use button instead of having to do it repeatedly

    public static final int CAPACITY = Amalgam.INGOTAMOUNT;
    private IIcon           fullIcon;
    private IIcon           emptyIcon;

    public ItemStoneTongs() {
        super();
        this.setCreativeTab(Amalgam.tab);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.emptyIcon = iconRegister.registerIcon("amalgam:stoneTongs");
        this.fullIcon = iconRegister.registerIcon("amalgam:stoneTongsFull");

        this.itemIcon = this.emptyIcon;

    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (this.getFluidAmount(stack) > 0) {
            return fullIcon;
        }
        return emptyIcon;
    }

    public int getFluidAmount(ItemStack container) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Amalgam")) {
            return 0;
        }

        NBTTagCompound containerNBT = container.stackTagCompound.getCompoundTag("Amalgam");
        return containerNBT.getInteger("Amount");
    }

    @Override
    public AmalgamStack getFluid(ItemStack container) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Amalgam")) {
            return new AmalgamStack(0, new PropertyList());
        }

        NBTTagCompound containerNBT = container.stackTagCompound.getCompoundTag("Amalgam");
        int amount = containerNBT.getInteger("Amount");
        PropertyList pList = new PropertyList();
        pList.readFromNBT(containerNBT.getCompoundTag("Tag"));
        return new AmalgamStack(amount, pList);
    }

    @Override
    public int fill(ItemStack container, AmalgamStack resource, boolean doFill) {
        if (resource == null) {
            return 0;
        }

        if (!doFill) {
            if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Amalgam")) {
                return Math.min(CAPACITY, resource.amount);
            }

            NBTTagCompound containerNBT = container.stackTagCompound.getCompoundTag("Amalgam");
            int amount = containerNBT.getInteger("Amount");
            return Math.min(CAPACITY - amount, resource.amount);
        }

        if (container.stackTagCompound == null) {
            container.stackTagCompound = new NBTTagCompound();
        }

        if (!container.stackTagCompound.hasKey("Amalgam")) {
            NBTTagCompound amalgamTag = resource.writeToNBT(new NBTTagCompound());

            if (CAPACITY < resource.amount) {
                amalgamTag.setInteger("Amount", CAPACITY);
                container.stackTagCompound.setTag("Amalgam", amalgamTag);
                return CAPACITY;
            }

            container.stackTagCompound.setTag("Amalgam", amalgamTag);
            return resource.amount;
        }
        NBTTagCompound amalgamTag = container.stackTagCompound.getCompoundTag("Amalgam");
        AmalgamStack stack = AmalgamStack.loadAmalgamStackFromNBT(amalgamTag);

        if (stack.fluidID != resource.fluidID) {
            return 0;
        }

        int filled = CAPACITY - stack.amount;
        if (resource.amount < filled) {
            stack = AmalgamStack.combine(stack, (AmalgamStack) resource);
            filled = resource.amount;
        } else {
            AmalgamStack temp = new AmalgamStack((AmalgamStack) resource, filled);
            stack = AmalgamStack.combine(stack, temp);
        }

        container.stackTagCompound.setTag("Amalgam", stack.writeToNBT(amalgamTag));
        return filled;
    }

    @Override
    public AmalgamStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Amalgam")) {
            return null;
        }

        AmalgamStack stack = AmalgamStack.loadAmalgamStackFromNBT(container.stackTagCompound.getCompoundTag("Amalgam"));
        stack.amount = Math.min(stack.amount, maxDrain);

        if (doDrain) {
            if (maxDrain >= CAPACITY) {
                container.stackTagCompound.removeTag("Amalgam");
                if (container.stackTagCompound.hasNoTags()) {
                    container.stackTagCompound = null;
                }
                return stack;
            }

            NBTTagCompound amalgamTag = container.stackTagCompound.getCompoundTag("Amalgam");
            amalgamTag.setInteger("Amount", amalgamTag.getInteger("Amount") - maxDrain);

            container.stackTagCompound.setTag("Amalgam", amalgamTag);
        }

        return stack;
    }

    @Override
    public int getCapacity(ItemStack container) {
        return CAPACITY;
    }

    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        if (world.getTileEntity(x, y, z) instanceof IFluidHandler) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        AmalgamStack droppedAmalgam = this.getFluid(stack);
        ItemStack emptyTongs = new ItemStack(Amalgam.stoneTongs);

        if (droppedAmalgam.amount == 0) {
            return emptyTongs;
        }

        ItemStack droppedBlob = new ItemStack(Amalgam.amalgamBlob, 1);

        ((ItemAmalgamBlob) Amalgam.amalgamBlob).setProperties(droppedBlob, droppedAmalgam.getProperties());
        ((ItemAmalgamBlob) Amalgam.amalgamBlob).setVolume(droppedBlob, droppedAmalgam.amount);

        if (!world.isRemote) {
            player.entityDropItem(droppedBlob, 1);
        }

        return emptyTongs;
    }

    public int getEmptySpace(ItemStack stack) {
        return CAPACITY - this.getFluidAmount(stack);
    }

    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    public double getDurabilityForDisplay(ItemStack stack) {

        return 1.0 - (float) this.getFluidAmount(stack) / (float) CAPACITY;
        // return (double)stack.getItemDamageForDisplay() /
        // (double)stack.getMaxDamage();
    }
}
