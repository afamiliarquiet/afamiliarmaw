package io.github.afamiliarquiet.network;

import io.github.afamiliarquiet.MawUtils;
import io.github.afamiliarquiet.entity.BreathProjectileEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import static io.github.afamiliarquiet.AFamiliarMaw.getNamespacedIdentifier;
import static io.github.afamiliarquiet.MawUtils.consumePyrexia;

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
        ServerPlayerEntity player = context.player();
        if (!MawUtils.canBreathe(player)) {
            return;
        }

        BreathProjectileEntity breathProjectileEntity = new BreathProjectileEntity(player, player.getServerWorld());
        breathProjectileEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 0.5F, 13F);
        breathProjectileEntity.setPosition(breathProjectileEntity.getPos().add(player.getRotationVector().multiply(0.5)).addRandom(player.getRandom(), 0.013f));
        player.getServerWorld().spawnEntity(breathProjectileEntity);

        consumePyrexia(player);
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.2f, (player.getRandom().nextFloat() * 0.13f + 1.3f));
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
}
