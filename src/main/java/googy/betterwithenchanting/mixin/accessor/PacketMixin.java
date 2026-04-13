package googy.betterwithenchanting.mixin.accessor;

import net.minecraft.core.net.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Packet.class, remap = false)
public interface PacketMixin
{
	/// Not sure what that was used for

//	@Invoker("addIdClassMapping")
//	static void callAddIdClassMapping(int id, boolean clientPacket, boolean serverPacket, Class<? extends Packet> packetClass) {
//		throw new UnsupportedOperationException();
//	}
}
