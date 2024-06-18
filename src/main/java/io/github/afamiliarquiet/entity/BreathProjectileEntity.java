package io.github.afamiliarquiet.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.List;

public class BreathProjectileEntity extends ThrownEntity {
    private final List<StatusEffectInstance> statusEffects;
    private static final TrackedData<List<ParticleEffect>> POTION_SWIRLS = DataTracker.registerData(BreathProjectileEntity.class, TrackedDataHandlerRegistry.PARTICLE_LIST);

    protected BreathProjectileEntity(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
        this.statusEffects = List.of();
    }

    @SuppressWarnings("unused") // nyeh. it's maybe used by /summon or something... it stays.
    protected BreathProjectileEntity(EntityType<? extends ThrownEntity> type, double x, double y, double z, World world) {
        super(type, x, y, z, world);
        this.statusEffects = List.of();
    }

    public BreathProjectileEntity(LivingEntity owner, World world) {
        super(MawEntities.BREATH_PROJECTILE_TYPE, owner, world);
        this.statusEffects = owner.getStatusEffects()
                .stream().filter((statusEffect)-> !(statusEffect.getEffectType().matchesId(MawEntities.DRACONIC_OMEN_STATUS_EFFECT_ID)))
                .toList();
        updateSwirls();
    }

    private void updateSwirls() {
        List<ParticleEffect> list = this.statusEffects.stream().filter(StatusEffectInstance::shouldShowParticles).map(StatusEffectInstance::createParticle).toList();
        this.dataTracker.set(POTION_SWIRLS, list);
    }

    // i'm not really sure if i need this stuff but whatever!
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("active_effects", 9)) {
            NbtList nbtList = nbt.getList("active_effects", 10);

            for(int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
                if (statusEffectInstance != null) {
                    //noinspection DataFlowIssue nyeh!
                    this.statusEffects.add(statusEffectInstance);
                }
            }

            updateSwirls();
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
        builder.add(POTION_SWIRLS, List.of());
    }

    @Override
    public void tick() {
        super.tick();

        // friction copied from fire particle.. its ok :thumbsup:
        // also means that gravity has a little more takeover power, as original v will be fricted but gravity keep goin
        this.setVelocity(this.getVelocity().multiply(0.96));
        this.calculateDimensions();

        if (this.getWorld().isClient) {
            List<ParticleEffect> particles = this.dataTracker.get(POTION_SWIRLS);
            if (!particles.isEmpty()) {
                if (this.random.nextInt(31) == 0) {
                    this.getWorld().addParticle(Util.getRandom(particles, this.random),
                            this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5),
                            0, 0.13, 0);
                }
            }
            this.getWorld().playSound(null, this.getBlockPos(),
                    SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS,
                    0.5f, (this.getRandom().nextFloat() * 0.1f + 0.4f));
        } else {
            if (this.age > 13) {
                this.discard();
            }
        }
    }

    @Override
    public double getGravity() {
        return -0.015;
    }

    @Override
    public boolean isOnFire() {
        // why bother with particles when you can just burn the thing that actually does collisions and get free fire?
        return true;
        //return !this.isSubmergedInWater();
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
        // (including not burning pets because burning pets is bad and this is just for a fest
        // (tho it sounds like pvp will be on for the fest so this won't matter but whatever!!!
        // the point is you shouldn't try to burn pets. i could just make that always the case actually but.. ehhhh. eh.)
        // ok well it looks like i did eventually make it so pets never get harmed. maybe. cool!
        boolean skipBadStuff = (!(entity.getWorld().getServer() != null && entity.getWorld().getServer().isPvpEnabled()) &&
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
                // maybe source should be the owner of this instead? idk
                for (StatusEffectInstance statusEffect : this.statusEffects) {
                    if (livingEntity.canHaveStatusEffect(statusEffect) &&
                            !(skipBadStuff && statusEffect.getEffectType().value().getCategory().equals(StatusEffectCategory.HARMFUL))) {
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

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        // bigger... BIGGER (vwoosh)
        // this actually feels like a nice way of getting the expanding fireball effect!
        // is it more expensive than maybe would be reasonable? (and like everything else,)
        // idk! find out when someone reads this and says "well there's yer problem"!
        float size = this.age * 0.05f + 0.05f;
        return EntityDimensions.changing(size, size);
    }
}
