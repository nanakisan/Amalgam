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

public class PacketSyncCastingTank implements IMessage, IMessageHandler<PacketSyncCastingTank, IMessage>{

	int x, y, z;
	//int amount;
	AmalgamStack fluid = null;
	
	public PacketSyncCastingTank(){};
	
	public PacketSyncCastingTank(int x, int y, int z, AmalgamStack fluid){//int amount){
		this.x = x;
		this.y = y;
		this.z = z;
		this.fluid = fluid;
	}
	
	@Override
	public IMessage onMessage(PacketSyncCastingTank message, MessageContext ctx) {
		Amalgam.log.info("Syncing casting tank: on message");
		
		TileEntity te = Amalgam.proxy.getClientWorld().getTileEntity(message.x, message.y, message.z);
		
		if(te != null){
			if(te instanceof TileCastingTable){
				//Amalgam.log.info("setting the fluid to " + message.fluid.getProperties().toString());
				((TileCastingTable) te).tank.setFluid(message.fluid);
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
		PacketBuffer p =new PacketBuffer(buf);
		try {
			new AmalgamStack(0, null);
			this.fluid = AmalgamStack.loadAmalgamStackFromNBT(p.readNBTTagCompoundFromBuffer());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		//Amalgam.log.info("writing buffer");
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		
		PacketBuffer p =new PacketBuffer(buf);
		try {
			// Amalgam.log.info("setting fluid to nbt: " + this.fluid.tag.toString());
			p.writeNBTTagCompoundToBuffer(this.fluid.writeToNBT(new NBTTagCompound()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}