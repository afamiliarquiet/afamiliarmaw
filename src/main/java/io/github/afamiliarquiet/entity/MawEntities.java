package io.github.afamiliarquiet.entity;

import io.github.afamiliarquiet.entity.effect.DraconicOmenStatusEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static io.github.afamiliarquiet.MagnificentMaw.id;

public class MawEntities {
    public static final Identifier BREATH_PROJECTILE_ID = id("breath_projectile");
    public static final EntityType<BreathProjectileEntity> BREATH_PROJECTILE_TYPE = EntityType.Builder
            .<BreathProjectileEntity>create(BreathProjectileEntity::new, SpawnGroup.MISC)
            .dimensions(0.05f, 0.05f)
            .maxTrackingRange(4).trackingTickInterval(Integer.MAX_VALUE)
            .build();
    // idk about this whole tracking tick interval thing but arrow's interval is too long for it to affect these so .. whatever

    public static final Identifier DRACONIC_OMEN_STATUS_EFFECT_ID = id("draconic_omen");

    public static final StatusEffect DRACONIC_OMEN_STATUS_EFFECT = new DraconicOmenStatusEffect();

    public static void register() {
        Registry.register(Registries.ENTITY_TYPE, BREATH_PROJECTILE_ID, BREATH_PROJECTILE_TYPE);
        Registry.register(Registries.STATUS_EFFECT, DRACONIC_OMEN_STATUS_EFFECT_ID, DRACONIC_OMEN_STATUS_EFFECT);
    }
}
