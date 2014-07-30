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

/**
 * This packet tells the client when amalgam has been added to the casting table
 * tank. Since we need to know how much amalgam is in the tank to render the
 * casting table gui, we need to send a packet of info about the tank to the
 * clients whenever it changes on the server.
 */

public class PacketSyncCastingTank implements IMessage, IMessageHandler<PacketSyncCastingTank, IMessage> {

    private int xCoord, yCoord, zCoord;
    private AmalgamStack fluid;

    // private AmalgamStack fluid = null;

    public PacketSyncCastingTank() {
        // an empty constructor is necessary for an IMessage implementation
    }

    public PacketSyncCastingTank(int x, int y, int z, AmalgamStack fluid) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.fluid = fluid;
    }

    @Override
    public IMessage onMessage(PacketSyncCastingTank message, MessageContext ctx) {
        Amalgam.LOG.info("Syncing casting tank: on message");

        TileEntity tileEntity = Amalgam.proxy.getClientWorld().getTileEntity(message.xCoord, message.yCoord, message.zCoord);

        if (tileEntity instanceof TileCastingTable) {
            ((TileCastingTable) tileEntity).tank.setFluid(message.fluid);
        }
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // Amalgam.log.info("reading buffer");
        this.xCoord = buf.readInt();
        this.yCoord = buf.readInt();
        this.zCoord = buf.readInt();
        PacketBuffer pBuf = new PacketBuffer(buf);
        try {
            new AmalgamStack(0, null);
            this.fluid = AmalgamStack.loadAmalgamStackFromNBT(pBuf.readNBTTagCompoundFromBuffer());
        } catch (IOException e) {
            // e.printStackTrace();
            Amalgam.LOG.error("Error reading from packetBuffer in PacketSyncCastingTank");
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // Amalgam.log.info("writing buffer");
        buf.writeInt(this.xCoord);
        buf.writeInt(this.yCoord);
        buf.writeInt(this.zCoord);

        PacketBuffer pBuf = new PacketBuffer(buf);
        try {
            // Amalgam.log.info("setting fluid to nbt: " +
            // this.fluid.tag.toString());
            pBuf.writeNBTTagCompoundToBuffer(this.fluid.writeToNBT(new NBTTagCompound()));
        } catch (IOException e) {
            // e.printStackTrace();
            Amalgam.LOG.error("Error writing to packetBuffer in PacketSyncCastingTank");
        }
    }

}