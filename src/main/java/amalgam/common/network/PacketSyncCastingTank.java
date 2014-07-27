package amalgam.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import amalgam.common.Amalgam;
import amalgam.common.fluid.AmalgamStack;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncCastingTank implements IMessage, IMessageHandler<PacketSyncCastingTank, IMessage>{

	int x, y, z;
	int amount;
	
	public PacketSyncCastingTank(){};
	
	public PacketSyncCastingTank(int x, int y, int z, int amount){
		this.x = x;
		this.y = y;
		this.z = z;
		this.amount = amount;
	}
	
	@Override
	public IMessage onMessage(PacketSyncCastingTank message, MessageContext ctx) {
		Amalgam.log.info("on message");
		
		TileEntity te = Amalgam.proxy.getClientWorld().getTileEntity(message.x, message.y, message.z);
		
		if(te != null){
			if(te instanceof TileCastingTable){
				AmalgamStack a = (AmalgamStack) ((TileCastingTable) te).tank.getFluid();
				a.amount = message.amount;
				
			}
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		//Amalgam.log.info("reading buffer");
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.amount = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		//Amalgam.log.info("writing buffer");
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.amount);
	}

}