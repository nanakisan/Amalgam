package amalgam.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import amalgam.common.container.ContainerCastingTable;
import amalgam.common.container.SlotCasting;
import amalgam.common.tile.TileCastingTable;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncCastingSlot implements IMessage, IMessageHandler<PacketSyncCastingSlot, IMessage>{

	int x, y, z;
	int slot;
	int castState;
	
	public PacketSyncCastingSlot(){};
	
	public PacketSyncCastingSlot(int x, int y, int z, int slot, int state){
		this.x = x;
		this.y = y;
		this.z = z;
		this.slot = slot;
		this.castState = state;
	}
	
	@Override
	public IMessage onMessage(PacketSyncCastingSlot message, MessageContext ctx) {
		//Amalgam.log.info("on message");
		TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
		Container c = ctx.getServerHandler().playerEntity.openContainer;
		if(te != null && c != null){
			if(te instanceof TileCastingTable && c instanceof ContainerCastingTable){
				((TileCastingTable) te).setCastState(message.slot, message.castState);
				Slot s = ((ContainerCastingTable) c).getSlot(message.slot);
				((SlotCasting) s).setCastState(message.castState);
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
		this.slot = buf.readByte();
		this.castState = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		//Amalgam.log.info("writing buffer");
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeByte(this.slot);
		buf.writeByte(this.castState);

	}

}
