package net.mateo.duckmod.entity.custom;

import net.mateo.duckmod.entity.ModEntities;
import net.mateo.duckmod.item.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class DuckEggProjectileEntity extends ThrowableItemProjectile {

    public DuckEggProjectileEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DuckEggProjectileEntity(Level pLevel, LivingEntity pShooter) {
        super(ModEntities.DUCK_EGG_PROJECTILE.get(), pShooter, pLevel);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.DUCK_EGG.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (!this.level().isClientSide) {
            Entity entity = pResult.getEntity();
            Entity owner = this.getOwner();

            // Deal damage to the hit entity
            entity.hurt(this.damageSources().thrown(this, owner), 1.0F);
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);

        if (!this.level().isClientSide) {
            // Spawn particles
            this.level().broadcastEntityEvent(this, (byte)3);

            // Spawn baby duck with 1/16 chance (chickens are 1/8)
            if (this.random.nextInt(16) == 0) {
                int duckCount = 1;
                if (this.random.nextInt(64) == 0) {
                    duckCount = 3; // Rare chance for 3 ducks
                }

                for (int j = 0; j < duckCount; ++j) {
                    DuckEntity duck = ModEntities.DUCK.get().create(this.level());
                    if (duck != null) {
                        duck.setAge(-24000); // Set as baby duck
                        duck.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                        this.level().addFreshEntity(duck);
                    }
                }
            }

            // Remove the projectile
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 3) {
            // Create particle effect
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, this.getItem()),
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        ((double)this.random.nextFloat() - 0.5D) * 0.08D,
                        ((double)this.random.nextFloat() - 0.5D) * 0.08D,
                        ((double)this.random.nextFloat() - 0.5D) * 0.08D
                );
            }
        }
    }
}