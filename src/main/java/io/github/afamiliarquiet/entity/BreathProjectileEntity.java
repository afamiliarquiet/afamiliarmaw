package io.github.afamiliarquiet.entity;

import io.github.afamiliarquiet.AFamiliarMaw;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

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

            for (StatusEffectInstance statusEffectInstance : this.statusEffects) {
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
            if (random.nextFloat() > 0.31f) {
                Vec3d p = this.getPos().addRandom(random, this.age * 0.1f);
                Vec3d v = this.getVelocity().addRandom(random, 0.031f);
                if (!this.statusEffects.isEmpty()) {
                    ParticleEffect particle = Util.getRandom(statusEffects, this.getRandom()).createParticle();
                    this.getWorld().addImportantParticle(particle, p.x, p.y, p.z, v.x, v.y, v.z);
                }
            }
        } else {
            if (this.age > 13) {
                this.discard();
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

        // these flames don't seem to respect pvp by default (i hoped extending projectile would do that)
        // so we be safe and respectful by checking here and stopping if requested :)
        // (including not burning pets because burning pets is bad and this is just for a fest (tho it sounds like pvp will be on for the fest so this won't matter but whatever!!! the point is you shouldn't try to burn pets. i could just make that always the case actually but.. ehhhh. eh.)
        boolean skipBadStuff = (!entity.getWorld().getServer().isPvpEnabled() &&
                entity instanceof PlayerEntity ||
                entity instanceof TameableEntity possiblePet && possiblePet.isTamed());

        // dunno if these checks for fire/splash immunity are necessary but..
        // it's good to be respectful to the entity's wishes anyway
        if (!entity.isFireImmune()) {
            entity.setOnFireForTicks(skipBadStuff ? 13 : 20);
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entityHitResult.getEntity();

            if (this.statusEffects != null && livingEntity.isAffectedBySplashPotions() && !livingEntity.isFireImmune()) {
                // splat on a copy of every status effect we can, except for the fiery stuff (it has combusted)
                for (StatusEffectInstance statusEffect : this.statusEffects) {
                    if (livingEntity.canHaveStatusEffect(statusEffect) && !(skipBadStuff && statusEffect.getEffectType().value().getCategory().equals(StatusEffectCategory.HARMFUL))) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(statusEffect), this);
                    }
                }
            }
        }
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);

        Vec3d v = (new Vec3d(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ())).addRandom(random, 0.031f);
        Vec3d p = this.getPos().addRandom(this.getRandom(), this.age * 0.1f).add(v.multiply(0.25));

        ParticleEffect particle;
        if (!this.statusEffects.isEmpty() && this.getRandom().nextFloat() < 0.31f) {
            AFamiliarMaw.LOGGER.info("ploppin a status swirl");
            particle = Util.getRandom(statusEffects, this.getRandom()).createParticle();
        } else {
            particle = ParticleTypes.FLAME;
        }

        this.getWorld().addImportantParticle(particle, p.x, p.y, p.z, v.x, v.y, v.z);
        if (this.getRandom().nextFloat() < 0.13f) {
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.5f, (this.getRandom().nextFloat() * 0.1f + 0.4f));
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
