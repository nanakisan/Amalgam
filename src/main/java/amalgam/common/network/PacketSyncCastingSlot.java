package amalgam.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import amalgam.common.Amalgam;
import amalgam.common.container.ContainerCastingTable;
import amalgam.common.container.SlotCasting;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * This packet tells the server when the casting state of a slot in the casting table changes due to a player using the
 * casting table gui. Since the gui is Client Side only the server the server needs to be made aware so it can update
 * the casting table tile entity.
 */

public class PacketSyncCastingSlot implements IMessage, IMessageHandler<PacketSyncCastingSlot, IMessage> {

    private int xCoord, yCoord, zCoord;
    private int slot;
    private int castState;

    public PacketSyncCastingSlot() {
        // an empty constructor is necessary for an IMessage implementation
    }

    public PacketSyncCastingSlot(int x, int y, int z, int slot, int state) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.slot = slot;
        this.castState = state;
    }

    @Override
    public IMessage onMessage(PacketSyncCastingSlot message, MessageContext ctx) {
        Amalgam.LOG.info("Syncing casting slot: on message");
        TileEntity tileEntity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
        Container container = ctx.getServerHandler().playerEntity.openContainer;
        if (tileEntity != null && container != null && tileEntity instanceof TileCastingTable && container instanceof ContainerCastingTable) {
            ((TileCastingTable) tileEntity).setCastState(message.slot, message.castState);
            Slot slot = ((ContainerCastingTable) container).getSlot(message.slot);
            ((SlotCasting) slot).setCastState(message.castState);
        }
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.xCoord = buf.readInt();
        this.yCoord = buf.readInt();
        this.zCoord = buf.readInt();
        this.slot = buf.readByte();
        this.castState = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.xCoord);
        buf.writeInt(this.yCoord);
        buf.writeInt(this.zCoord);
        buf.writeByte(this.slot);
        buf.writeByte(this.castState);
    }

}
