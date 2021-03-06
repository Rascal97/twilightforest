package twilightforest.entity.boss;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import java.util.List;

public class EntityTFNagaSegment extends MultiPartEntityPart {

	private final EntityTFNaga naga;
	private final int segment;
	private int deathCounter;

	public EntityTFNagaSegment(EntityTFNaga myNaga, int segNum) {
		super(myNaga, "segment"+segNum, 0, 0);
		this.naga = myNaga;
		this.segment = segNum;
		this.stepHeight = 2;
		deactivate();
	}

	@Override
	public boolean attackEntityFrom(DamageSource src, float damage) {
		return super.attackEntityFrom(src, damage * 2F / 3F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		++this.ticksExisted;

		if (!isInvisible())
			collideWithOthers();

		if (deathCounter > 0)
		{
			deathCounter--;
			if (deathCounter <= 0)
			{
				for (int k = 0; k < 20; k++)
				{
					double d = rand.nextGaussian() * 0.02D;
					double d1 = rand.nextGaussian() * 0.02D;
					double d2 = rand.nextGaussian() * 0.02D;
					ParticleTypes explosionType = rand.nextBoolean() ? ParticleTypes.EXPLOSION_LARGE : ParticleTypes.EXPLOSION_NORMAL;

					this.world.spawnParticle(explosionType, (getX() + rand.nextFloat() * width * 2.0F) - width, getY() + rand.nextFloat() * height, (getZ() + rand.nextFloat() * width * 2.0F) - width, d, d1, d2);
				}

				deactivate();
			}
		}
	}

	private void collideWithOthers() {
		List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().grow(0.2D, 0.0D, 0.2D));

		for (Entity entity : list) {
			if (entity.canBePushed()) {
				this.collideWithEntity(entity);
			}
		}
	}

	private void collideWithEntity(Entity entity) {
		entity.applyEntityCollision(this);

		// attack anything that's not us
		if ((entity instanceof LivingEntity) && !(entity instanceof EntityTFNaga) && !(entity instanceof EntityTFNagaSegment)) {
			int attackStrength = 2;

			// get rid of nearby deer & look impressive
			if (entity instanceof AnimalEntity) {
				attackStrength *= 3;
			}

			entity.attackEntityFrom(DamageSource.causeMobDamage(naga), attackStrength);
		}
	}

	public void deactivate() {
		setSize(0, 0);
		setInvisible(true);
	}

	public void activate() {
		setSize(1.8F, 1.8F);
		setInvisible(false);
	}

	// make public
	@Override
	public void setRotation(float yaw, float pitch) {
		super.setRotation(yaw, pitch);
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {}

	public void selfDestruct() {
		this.deathCounter = 10;
	}

	@Override
	public boolean isNonBoss() {
		return false;
	}
}
