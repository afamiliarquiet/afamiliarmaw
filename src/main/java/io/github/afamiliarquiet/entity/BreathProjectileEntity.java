package io.github.afamiliarquiet.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BreathProjectileEntity extends ThrownEntity {
    protected BreathProjectileEntity(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
    }

    protected BreathProjectileEntity(EntityType<? extends ThrownEntity> type, double x, double y, double z, World world) {
        super(type, x, y, z, world);
    }

    public BreathProjectileEntity(LivingEntity owner, World world) {
        super(MawEntities.BREATH_PROJECTILE_TYPE, owner, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
//            Vec3d v = this.getVelocity();
//            this.getWorld().addParticle(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), v.x, v.y, v.z);
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
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
    }

    public static BreathProjectileEntity create(EntityType<BreathProjectileEntity> type, World world) {
        return new BreathProjectileEntity(type, world); // maybe this is useful? idk!
    }
}
