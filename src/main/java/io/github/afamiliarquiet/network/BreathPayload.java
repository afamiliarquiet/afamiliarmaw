package io.github.afamiliarquiet.network;

import io.github.afamiliarquiet.MawUtils;
import io.github.afamiliarquiet.entity.BreathProjectileEntity;
import io.github.afamiliarquiet.entity.MawEntities;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

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
        if (!MawUtils.canBreathe(player)) {
            return;
        }

        BreathProjectileEntity breathProjectileEntity = new BreathProjectileEntity(player, player.getServerWorld());
        breathProjectileEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 0.5F, 13F);
        breathProjectileEntity.setPosition(breathProjectileEntity.getPos().add(0, -.1, 0));
        player.getServerWorld().spawnEntity(breathProjectileEntity);

        RegistryEntry<StatusEffect> pyrexiaEntry = player.getWorld().getRegistryManager().get(RegistryKeys.STATUS_EFFECT).getEntry(MawEntities.PYREXIA_STATUS_EFFECT_ID).get();
        StatusEffectInstance oldInstance = player.getStatusEffect(pyrexiaEntry);
        if (oldInstance != null) {
            player.removeStatusEffect(pyrexiaEntry);
            if (oldInstance.getDuration() > 31) {
                player.addStatusEffect(new StatusEffectInstance(oldInstance.getEffectType(), oldInstance.getDuration() - 31, oldInstance.getAmplifier(), oldInstance.isAmbient(), oldInstance.shouldShowParticles(), oldInstance.shouldShowIcon()));
            }
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
