package googy.betterwithenchanting.network;

import googy.betterwithenchanting.mixins.interfaces.ContainerHotbarLocking;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class UpdateLockState implements NetworkMessage {
	int state;

	public UpdateLockState(){}

	public UpdateLockState(int state){
		this.state = state;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeInt(state);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.state = packet.readInt();
	}

	@Override
	public void handle(NetworkContext context) {
		((ContainerHotbarLocking)context.player.inventory).enchanted$setValue(this.state);
	}
}
