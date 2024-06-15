package io.github.afamiliarquiet.entity;

import io.github.afamiliarquiet.entity.effect.PyrexiaStatusEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import static io.github.afamiliarquiet.AFamiliarMaw.getNamespacedIdentifier;

public class MawEntities {
    public static final Identifier BREATH_PROJECTILE_ID = getNamespacedIdentifier("breath_projectile");
    public static final EntityType<BreathProjectileEntity> BREATH_PROJECTILE_TYPE = EntityType.Builder.<BreathProjectileEntity>create(BreathProjectileEntity::create, SpawnGroup.MISC)
            .dimensions(0.25f, 0.25f)
            .maxTrackingRange(4).trackingTickInterval(10).build();

    public static final Identifier PYREXIA_STATUS_EFFECT_ID = getNamespacedIdentifier("pyrexia");

    public static final StatusEffect PYREXIA_STATUS_EFFECT = new PyrexiaStatusEffect();

    // this is probably poor form but whatever
    public static final RegistryEntry<StatusEffect> PYREXIA_REGISTRY_ENTRY = RegistryEntry.of(PYREXIA_STATUS_EFFECT);

    public static void register() {
        Registry.register(Registries.ENTITY_TYPE, BREATH_PROJECTILE_ID, BREATH_PROJECTILE_TYPE);
        Registry.register(Registries.STATUS_EFFECT, PYREXIA_STATUS_EFFECT_ID, PYREXIA_STATUS_EFFECT);
    }
}
