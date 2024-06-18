package io.github.afamiliarquiet.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import static io.github.afamiliarquiet.AFamiliarMaw.LOGGER;
import static io.github.afamiliarquiet.AFamiliarMaw.getNamespacedIdentifier;

public class BreathPacket implements CustomPayload {
    // todo - either properly implement start/stop or just remove it
    public enum Mode {
        START_BREATHING,
        STOP_BREATHING
    }
    private final Mode mode;

    public static final Id<BreathPacket> ID = new CustomPayload.Id<>(getNamespacedIdentifier("breath"));
    public static final PacketCodec<PacketByteBuf, BreathPacket> CODEC = PacketCodec.of(BreathPacket::write, BreathPacket::new);

    public static void receive(BreathPacket payload, ServerPlayNetworking.Context context) {
        if (context.player() instanceof MawBearer mawBearer) {
            mawBearer.maw$setBreathing(payload.shouldBreathe());
        }
    }

    public BreathPacket(Mode mode) {
        this.mode = mode;
    }

    private BreathPacket(PacketByteBuf buf) {
        this.mode = buf.readEnumConstant(Mode.class);
    }

    private void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.mode);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return BreathPacket.ID;
    }

    public boolean shouldBreathe() {
        if (this.mode.equals(Mode.START_BREATHING)) {
            return true;
        } else if (this.mode.equals(Mode.STOP_BREATHING)) {
            return false;
        } else {
            LOGGER.warn("Unknown BreathPacket mode received: " + this.mode);
            return false;
        }
    }
}
