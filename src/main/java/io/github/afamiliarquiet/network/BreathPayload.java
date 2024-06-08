package io.github.afamiliarquiet.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import static io.github.afamiliarquiet.AFamiliarMaw.getNamespacedIdentifier;

public class BreathPayload implements CustomPayload {
    public enum Mode {
        START_BREATHING,
        STOP_BREATHING
    }
    private final Mode mode;

    public static final Id<BreathPayload> ID = new CustomPayload.Id<>(getNamespacedIdentifier("breath"));
    public static final PacketCodec<PacketByteBuf, BreathPayload> CODEC = PacketCodec.of(BreathPayload::write, BreathPayload::new);

    public static void receive(BreathPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        Random random = player.getRandom();
        Vec3d pos = player.getEyePos();
        Vec3d movement = player.getMovement();
        float pitch = player.getPitch();
        float yaw = player.getYaw();
        float power = 0.2f;
        float uncertainty = 13f;
        float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float g = -MathHelper.sin((pitch) * 0.017453292F);
        float h = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        Vec3d roto = (new Vec3d(f, g, h)).normalize().add(random.nextTriangular(0.0, 0.0172275 * (double)uncertainty), random.nextTriangular(0.0, 0.0172275 * (double)uncertainty), random.nextTriangular(0.0, 0.0172275 * (double)uncertainty)).multiply((double)power);
        Vec3d sumOffset = roto.add(movement.x, player.isOnGround() ? 0.0 : movement.y, movement.z);

        for (int i = 0; i < 5; i++) {

            player.getServerWorld().spawnParticles(ParticleTypes.FLAME,
                    pos.x + sumOffset.x, pos.y - 0.125 + sumOffset.y, pos.z + sumOffset.z,
                    0,
                    sumOffset.x, sumOffset.y, sumOffset.z, 1.0);
        }
    }

    public BreathPayload(Mode mode) {
        this.mode = mode;
    }

    private BreathPayload(PacketByteBuf buf) {
        this.mode = buf.readEnumConstant(Mode.class);
    }

    private void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.mode);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return BreathPayload.ID;
    }
}
