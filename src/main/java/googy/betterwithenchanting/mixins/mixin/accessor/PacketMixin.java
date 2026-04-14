package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.core.net.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Packet.class, remap = false)
public interface PacketMixin
{
	/// Not sure what that was used for

//	@Invoker("addIdClassMapping")
//	static void callAddIdClassMapping(int id, boolean clientPacket, boolean serverPacket, Class<? extends Packet> packetClass) {
//		throw new UnsupportedOperationException();
//	}
}
