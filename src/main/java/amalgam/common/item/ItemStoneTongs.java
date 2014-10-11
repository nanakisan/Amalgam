package amalgam.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import amalgam.common.Config;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.IAmalgamContainerItem;
import amalgam.common.properties.AmalgamPropertyList;
import amalgam.common.properties.AmalgamPropertyManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStoneTongs extends Item implements IAmalgamContainerItem {

    private static final String AMALGAM_KEY = "Amalgam";
    private static final String AMOUNT_KEY  = "Amount";
    public static final int     CAPACITY    = Config.INGOT_AMOUNT;

    private IIcon               amalgamOverlay;
    private IIcon               emptyOverlay;

    public ItemStoneTongs() {
        super();
        this.setCreativeTab(Config.tab);
        this.maxStackSize = 1;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("amalgam:stoneTongs");
        this.amalgamOverlay = iconRegister.registerIcon("amalgam:stoneTongsOverlay");
        this.emptyOverlay = iconRegister.registerIcon("amalgam:stoneTongsEmpty");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (pass == 0) {
            return itemIcon;
        }
        if (this.getFluidAmount(stack) > 0) {
            return amalgamOverlay;
        }

        return emptyOverlay;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        if (renderPass == 1) {
            if (stack.getTagCompound() != null) {
                if (Config.coloredAmalgam) {
                    return (int) this.getFluid(stack).getProperties().getValue(AmalgamPropertyManager.COLOR);
                } else {
                    return (int) AmalgamPropertyManager.COLOR.getDefaultValue();
                }
            }
        }
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    public int getFluidAmount(ItemStack container) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey(AMALGAM_KEY)) {
            return 0;
        }

        NBTTagCompound containerNBT = container.stackTagCompound.getCompoundTag(AMALGAM_KEY);
        return containerNBT.getInteger(AMOUNT_KEY);
    }

    @Override
    public AmalgamStack getFluid(ItemStack container) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey(AMALGAM_KEY)) {
            return new AmalgamStack(0, new AmalgamPropertyList());
        }

        NBTTagCompound containerNBT = container.stackTagCompound.getCompoundTag(AMALGAM_KEY);
        int amount = containerNBT.getInteger(AMOUNT_KEY);
        AmalgamPropertyList pList = new AmalgamPropertyList();
        pList.readFromNBT(containerNBT.getCompoundTag("Tag"));
        return new AmalgamStack(amount, pList);
    }

    @Override
    public int fill(ItemStack container, AmalgamStack resource, boolean doFill) {
        if (resource == null) {
            return 0;
        }

        if (!doFill) {
            if (container.stackTagCompound == null || !container.stackTagCompound.hasKey(AMALGAM_KEY)) {
                return Math.min(CAPACITY, resource.amount);
            }

            NBTTagCompound containerNBT = container.stackTagCompound.getCompoundTag(AMALGAM_KEY);
            int amount = containerNBT.getInteger(AMOUNT_KEY);
            return Math.min(CAPACITY - amount, resource.amount);
        }

        if (container.stackTagCompound == null) {
            container.stackTagCompound = new NBTTagCompound();
        }

        if (!container.stackTagCompound.hasKey(AMALGAM_KEY)) {
            NBTTagCompound amalgamTag = resource.writeToNBT(new NBTTagCompound());

            if (CAPACITY < resource.amount) {
                amalgamTag.setInteger(AMOUNT_KEY, CAPACITY);
                container.stackTagCompound.setTag(AMALGAM_KEY, amalgamTag);
                return CAPACITY;
            }

            container.stackTagCompound.setTag(AMALGAM_KEY, amalgamTag);
            return resource.amount;
        }

        NBTTagCompound amalgamTag = container.stackTagCompound.getCompoundTag(AMALGAM_KEY);
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

        container.stackTagCompound.setTag(AMALGAM_KEY, stack.writeToNBT(amalgamTag));
        return filled;
    }

    @Override
    public AmalgamStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey(AMALGAM_KEY)) {
            return null;
        }

        AmalgamStack stack = AmalgamStack.loadAmalgamStackFromNBT(container.stackTagCompound.getCompoundTag(AMALGAM_KEY));
        stack.amount = Math.min(stack.amount, maxDrain);

        if (doDrain) {
            if (maxDrain >= CAPACITY) {
                container.stackTagCompound.removeTag(AMALGAM_KEY);
                if (container.stackTagCompound.hasNoTags()) {
                    container.stackTagCompound = null;
                }
                return stack;
            }

            NBTTagCompound amalgamTag = container.stackTagCompound.getCompoundTag(AMALGAM_KEY);
            amalgamTag.setInteger(AMOUNT_KEY, amalgamTag.getInteger(AMOUNT_KEY) - maxDrain);
            container.stackTagCompound.setTag(AMALGAM_KEY, amalgamTag);
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
        ItemStack emptyTongs = new ItemStack(Config.stoneTongs);

        if (droppedAmalgam.amount == 0) {
            return emptyTongs;
        }

        ItemStack droppedBlob = new ItemStack(Config.amalgamBlob, 1);
        ((ItemAmalgamBlob) Config.amalgamBlob).setProperties(droppedBlob, droppedAmalgam.getProperties());
        ((ItemAmalgamBlob) Config.amalgamBlob).setVolume(droppedBlob, droppedAmalgam.amount);

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
    }

}
