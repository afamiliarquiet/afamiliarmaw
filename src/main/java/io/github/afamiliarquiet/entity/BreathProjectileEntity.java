package io.github.afamiliarquiet.entity;

import io.github.afamiliarquiet.AFamiliarMaw;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Collection;

public class BreathProjectileEntity extends ThrownEntity {
    private Collection<StatusEffectInstance> statusEffects;

    protected BreathProjectileEntity(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
        this.statusEffects = null;
    }

    protected BreathProjectileEntity(EntityType<? extends ThrownEntity> type, double x, double y, double z, World world) {
        super(type, x, y, z, world);
        this.statusEffects = null;
    }

    public BreathProjectileEntity(LivingEntity owner, World world) {
        super(MawEntities.BREATH_PROJECTILE_TYPE, owner, world);
        this.statusEffects = owner.getStatusEffects();
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            Random random = this.getRandom();
            if (random.nextDouble() > 0.31) {
                Vec3d p = this.getPos().addRandom(random, this.age * 0.1f);
                Vec3d v = this.getVelocity().addRandom(random, 0.031f);
                this.getWorld().addImportantParticle(ParticleTypes.FLAME, p.x, p.y, p.z, v.x, v.y, v.z);

            }
        } else {
//            Random random = this.getRandom();
//            Vec3d pos = this.getPos();
//            Vec3d movement = this.getMovement();
//            float pitch = this.getPitch();
//            float yaw = this.getYaw();
//            float power = 0.2f;
//            float uncertainty = 13f;
//            float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
//            float g = -MathHelper.sin((pitch) * 0.017453292F);
//            float h = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
//            Vec3d roto = (new Vec3d(f, g, h)).normalize().add(random.nextTriangular(0.0, 0.0172275 * (double)uncertainty), random.nextTriangular(0.0, 0.0172275 * (double)uncertainty), random.nextTriangular(0.0, 0.0172275 * (double)uncertainty)).multiply((double)power);
//            Vec3d sumOffset = roto.add(movement.x, movement.y, movement.z);
//
//            ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.FLAME,
//                    pos.x, pos.y, pos.z,
//                    0,
//                    movement.x, movement.y, movement.z, 1.0);
            if (this.age > 7) {
                this.kill();
            }
        }
    }

    @Override
    public double getGravity() {
        return -0.01;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // in theory, extending Projectile means this won't
        // hit the breather or their attached entities
        super.onEntityHit(entityHitResult);

        if (this.getWorld().isClient()) {
            AFamiliarMaw.LOGGER.warn("yea so sometimes its client world");
            return;
        }

        Entity entity = entityHitResult.getEntity();

        // dunno if these checks for fire/splash immunity are necessary but..
        // it's good to be respectful to the entity's wishes anyway
        if (!entity.isFireImmune()) {
            entity.setOnFireForTicks(20);
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entityHitResult.getEntity();

            if (this.statusEffects != null && livingEntity.isAffectedBySplashPotions() && !livingEntity.isFireImmune()) {
                // splat on a copy of every status effect we can, except for the fiery stuff (it has combusted)
                for (StatusEffectInstance statusEffect : this.statusEffects) {
                    if (statusEffect.getEffectType().matchesId(MawEntities.PYREXIA_STATUS_EFFECT_ID)) {
                        continue;
                    }
                    if (livingEntity.canHaveStatusEffect(statusEffect)) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(statusEffect), this);
                    }
                }
            }
        }
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
    }

    public static BreathProjectileEntity create(EntityType<BreathProjectileEntity> type, World world) {
        return new BreathProjectileEntity(type, world); // maybe this is useful? idk!
    }
}
