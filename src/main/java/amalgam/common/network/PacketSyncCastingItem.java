package amalgam.common.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import amalgam.common.Amalgam;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncCastingItem implements IMessage, IMessageHandler<PacketSyncCastingItem, IMessage> {

    private ItemStack itemStack;
    private int       slot;
    private int       x, y, z;

    public PacketSyncCastingItem() {
    }

    public PacketSyncCastingItem(ItemStack itemStack, int slot, int x, int y, int z) {
        this.itemStack = itemStack;
        this.slot = slot;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();

        slot = buf.readByte();

        PacketBuffer pb = new PacketBuffer(buf);
        try {
            itemStack = pb.readItemStackFromBuffer();
        } catch (IOException e) {
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);

        buf.writeByte(slot);

        PacketBuffer pb = new PacketBuffer(buf);
        try {
            pb.writeItemStackToBuffer(itemStack);
        } catch (IOException e) {
        }

    }

    @Override
    public IMessage onMessage(PacketSyncCastingItem message, MessageContext ctx) {
        TileEntity te = Amalgam.proxy.getClientWorld().getTileEntity(message.x, message.y, message.z);

        if (te instanceof TileCastingTable) {
            // Config.LOG.info("PACKET SYNC AMALGAM TANK: on message, setting amalgam tank,  new volume: " +
            // message.amalgamStack.amount);
            ((TileCastingTable) te).setInventorySlotContents(message.slot, message.itemStack);
        }

        return null;
    }
}
