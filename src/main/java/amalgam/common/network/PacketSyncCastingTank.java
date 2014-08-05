package amalgam.common.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import amalgam.common.Amalgam;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncCastingTank implements IMessage, IMessageHandler<PacketSyncCastingTank, IMessage> {

    private int          xCoord, yCoord, zCoord;
    private AmalgamStack fluid;

    public PacketSyncCastingTank() {
    }

    public PacketSyncCastingTank(int x, int y, int z, AmalgamStack fluid) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.fluid = fluid;
    }

    @Override
    public IMessage onMessage(PacketSyncCastingTank message, MessageContext ctx) {

        TileEntity tileEntity = Amalgam.proxy.getClientWorld().getTileEntity(message.xCoord, message.yCoord, message.zCoord);

        if (tileEntity instanceof TileCastingTable) {
            ((TileCastingTable) tileEntity).setTankFluid(message.fluid);
        }
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.xCoord = buf.readInt();
        this.yCoord = buf.readInt();
        this.zCoord = buf.readInt();
        PacketBuffer pBuf = new PacketBuffer(buf);
        try {
            new AmalgamStack(0, null);
            this.fluid = AmalgamStack.loadAmalgamStackFromNBT(pBuf.readNBTTagCompoundFromBuffer());
        } catch (IOException e) {
            Amalgam.LOG.error("Error reading from packetBuffer in PacketSyncCastingTank");
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.xCoord);
        buf.writeInt(this.yCoord);
        buf.writeInt(this.zCoord);

        PacketBuffer pBuf = new PacketBuffer(buf);
        try {
            pBuf.writeNBTTagCompoundToBuffer(this.fluid.writeToNBT(new NBTTagCompound()));
        } catch (IOException e) {
            Amalgam.LOG.error("Error writing to packetBuffer in PacketSyncCastingTank");
        }
    }

}