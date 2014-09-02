package amalgam.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import amalgam.common.Amalgam;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncCastingState implements IMessage, IMessageHandler<PacketSyncCastingState, IMessage> {

    private int slot, state;
    private int  x, y, z;

    public PacketSyncCastingState() {
    }

    public PacketSyncCastingState(int slotNum, int slotState, int x, int y, int z) {
        this.slot = slotNum;
        this.state = slotState;
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
        state = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        
        buf.writeByte(slot);
        buf.writeByte(state);
    }

    @Override
    public IMessage onMessage(PacketSyncCastingState message, MessageContext ctx) {
        TileEntity te = Amalgam.proxy.getClientWorld().getTileEntity(message.x, message.y, message.z);

        if (te instanceof TileCastingTable) {
            ((TileCastingTable) te).setCastState(message.slot, message.state);
        }

        return null;
    }
}
