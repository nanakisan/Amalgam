package amalgam.common.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import amalgam.common.Amalgam;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.tile.TileStoneCrucible;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncAmalgamTank implements IMessage, IMessageHandler<PacketSyncAmalgamTank, IMessage> {

    private AmalgamStack amalgamStack;
    private int          x, y, z;

    public PacketSyncAmalgamTank() {
    }

    public PacketSyncAmalgamTank(AmalgamStack amalgamStack, int x, int y, int z) {
        this.amalgamStack = amalgamStack;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();

        PacketBuffer pb = new PacketBuffer(buf);
        try {
            amalgamStack = AmalgamStack.loadAmalgamStackFromNBT(pb.readNBTTagCompoundFromBuffer());
        } catch (IOException e) {
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);

        PacketBuffer pb = new PacketBuffer(buf);
        try {
            pb.writeNBTTagCompoundToBuffer(amalgamStack.writeToNBT(new NBTTagCompound()));
        } catch (IOException e) {
        }
    }

    @Override
    public IMessage onMessage(PacketSyncAmalgamTank message, MessageContext ctx) {
        TileEntity te = Amalgam.proxy.getClientWorld().getTileEntity(message.x, message.y, message.z);

        if (te instanceof TileStoneCrucible) {
            ((TileStoneCrucible) te).setAmalgam(message.amalgamStack);
        }

        return null;
    }
}
