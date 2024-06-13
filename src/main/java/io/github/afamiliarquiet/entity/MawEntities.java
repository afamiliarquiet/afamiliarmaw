package io.github.afamiliarquiet.entity;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static io.github.afamiliarquiet.AFamiliarMaw.getNamespacedIdentifier;

public class MawEntities {
    public static final Identifier BREATH_PROJECTILE_ID = getNamespacedIdentifier("breath_projectile");
    public static final EntityType<BreathProjectileEntity> BREATH_PROJECTILE_TYPE = EntityType.Builder.<BreathProjectileEntity>create(BreathProjectileEntity::create, SpawnGroup.MISC)
            .dimensions(0.25f, 0.25f)
            .maxTrackingRange(4).trackingTickInterval(10).build();
    public static void register() {
        Registry.register(Registries.ENTITY_TYPE, BREATH_PROJECTILE_ID, BREATH_PROJECTILE_TYPE);
    }
}
