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
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Util;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class BreathProjectileEntity extends ThrownEntity {
    private List<StatusEffectInstance> statusEffects;

    protected BreathProjectileEntity(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
        this.statusEffects = List.of();
    }

    protected BreathProjectileEntity(EntityType<? extends ThrownEntity> type, double x, double y, double z, World world) {
        super(type, x, y, z, world);
        this.statusEffects = List.of();
    }

    public BreathProjectileEntity(LivingEntity owner, World world) {
        super(MawEntities.BREATH_PROJECTILE_TYPE, owner, world);
        this.statusEffects = owner.getStatusEffects()
                .stream().filter((statusEffect)-> !(statusEffect.getEffectType().matchesId(MawEntities.PYREXIA_STATUS_EFFECT_ID)))
                .toList();
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("active_effects", 9)) {
            NbtList nbtList = nbt.getList("active_effects", 10);

            for(int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
                if (statusEffectInstance != null) {
                    this.statusEffects.add(statusEffectInstance);
                }
            }
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (!this.statusEffects.isEmpty()) {
            NbtList nbtList = new NbtList();
            Iterator statusIterator = this.statusEffects.iterator();

            while(statusIterator.hasNext()) {
                StatusEffectInstance statusEffectInstance = (StatusEffectInstance) statusIterator.next();
                nbtList.add(statusEffectInstance.writeNbt());
            }

            nbt.put("active_effects", nbtList);
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();

        this.setVelocity(this.getVelocity().multiply(0.96));

        if (this.getWorld().isClient) {
            Random random = this.getRandom();
            if (random.nextDouble() > 0.31) {
                Vec3d p = this.getPos().addRandom(random, this.age * 0.1f);
                Vec3d v = this.getVelocity().addRandom(random, 0.031f);
                if (!this.statusEffects.isEmpty()) {
                    ParticleEffect particle = Util.getRandom(statusEffects, this.getRandom()).createParticle();
                    this.getWorld().addImportantParticle(particle, p.x, p.y, p.z, v.x, v.y, v.z);
                }
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
            if (this.age > 13) {
                this.kill();
            }
        }
    }

    @Override
    public double getGravity() {
        return 0.00;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // in theory, extending Projectile means this won't
        // hit the breather or their attached entities
        super.onEntityHit(entityHitResult);

        if (this.getWorld().isClient()) {
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
                    if (livingEntity.canHaveStatusEffect(statusEffect)) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(statusEffect), this);
                    }
                }
            }
        }
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);

        // todo - remove this
        if (!this.getWorld().isClient()) {
            AFamiliarMaw.LOGGER.warn("turns out S2C packet can get sent to server world too");
        }

        Vec3d v = (new Vec3d(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ())).addRandom(random, 0.031f);
        Vec3d p = this.getPos().addRandom(this.getRandom(), this.age * 0.1f).add(v.multiply(0.25));

        ParticleEffect particle;
        if (!this.statusEffects.isEmpty() && this.getRandom().nextDouble() < 0.31) {
            AFamiliarMaw.LOGGER.info("ploppin a status swirl");
            particle = Util.getRandom(statusEffects, this.getRandom()).createParticle();
        } else {
            particle = ParticleTypes.FLAME;
        }

        this.getWorld().addImportantParticle(particle, p.x, p.y, p.z, v.x, v.y, v.z);
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
    }

    public static BreathProjectileEntity create(EntityType<BreathProjectileEntity> type, World world) {
        return new BreathProjectileEntity(type, world); // maybe this is useful? idk!
    }
}
