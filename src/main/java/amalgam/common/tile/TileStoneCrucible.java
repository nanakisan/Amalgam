package amalgam.common.tile;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import amalgam.common.Config;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.fluid.AmalgamTank;
import amalgam.common.properties.PropertyList;
import amalgam.common.properties.PropertyManager;

import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileStoneCrucible extends AbstractTileAmalgamContainer implements IInventory {

    private static Set<Block>   heatSources   = Sets.newHashSet(new Block[] { Blocks.fire, Blocks.lava });
    private static final String HEAT_TAG      = "heat";
    private boolean             hasHeat;

    private ItemStack           buffer;

    private static final int    UPDATE_PERIOD = 50;
    private int                 ticksSinceUpdate;

    public TileStoneCrucible() {
        super();
        tank = new AmalgamTank(Config.INGOT_AMOUNT * 15);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        tank.readFromNBT(tag);
        this.hasHeat = tag.getBoolean(HEAT_TAG);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tank.writeToNBT(tag);
        tag.setBoolean(HEAT_TAG, this.hasHeat);
    }

    @Override
    public void updateEntity() {
        ticksSinceUpdate++;
        meltBufferItem();

        if (ticksSinceUpdate > UPDATE_PERIOD) {
            ticksSinceUpdate = 0;
            Block test = this.worldObj.getBlock(this.xCoord, this.yCoord - 1, this.zCoord);

            if (TileStoneCrucible.heatSources.contains(test)) {
                this.hasHeat = true;
            } else {
                this.hasHeat = false;
            }
        }
    }

    private void meltBufferItem() {
        if (buffer != null && this.isHot()) {
            ItemStack stack = this.decrStackSize(0, 1);
            int amount = PropertyManager.getVolume(stack);

            if (amount > 0 && amount < this.getEmptySpace()) {
                PropertyList amalgProperties = PropertyManager.getProperties(stack);
                AmalgamStack amalg = new AmalgamStack(amount, amalgProperties);

                if (amalgProperties == null) {
                    Config.LOG.error("No properties!!!!!");
                }

                this.fill(ForgeDirection.UNKNOWN, amalg, true);
                // FIXME need to update clientside!
            }
        }
    }

    public static void addHeatSource(Block source) {
        TileStoneCrucible.heatSources.add(source);
    }

    public boolean isHot() {
        return this.hasHeat;
    }

    public float getFluidHeight() {
        return 0.3F + 0.69F * ((float) this.tank.getFluidAmount() / (float) this.tank.getCapacity());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tank.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, this.blockMetadata, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.func_148857_g();
        this.tank.readFromNBT(tag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == 0) {
            return buffer;
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot == 0) {
            return buffer.splitStack(amount);
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (slot == 0) {
            return buffer;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot == 0 && buffer == null) {
            buffer = stack;
        }
    }

    @Override
    public String getInventoryName() {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack != null && PropertyManager.itemIsAmalgable(stack) && PropertyManager.getVolume(stack) <= this.getEmptySpace()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean receiveClientEvent(int eventNum, int arg) {
        return false;
    }

}
