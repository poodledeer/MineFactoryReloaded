package powercrystals.minefactoryreloaded.block.decor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockPinkSlime extends BlockBreakable {

	public static Block.SoundType slime = new SoundType("slime", 1f, 1f);

	public BlockPinkSlime() {

		super("minefactoryreloaded:tile.mfr.pinkslime.block", Material.clay, false); // FIXME: this doesn't take a string in 1.8
		setCreativeTab(MFRCreativeTab.tab);
		setBlockName("mfr.pinkslime.block");
		setBlockTextureName("minefactoryreloaded:" + getUnlocalizedName());
		slipperiness = 0.8f;
		setHardness(0.5f);
		setHarvestLevel("shovel", 0);
		setStepSound(slime);
	}

	@Override
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {

		final float f = 0.125F;
		return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1 - f, z + 1);
	}

	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {

		if (entity.isSneaking())
			super.onFallenUpon(world, x, y, z, entity, fallDistance);
		else {
			entity.fallDistance = 0;
			if (entity.motionY < 0) // FIXME: this has its own method in 1.8 (applies to non-living)
				entity.getEntityData().setDouble("mfr:slime", -entity.motionY);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

		NBTTagCompound data = entity.getEntityData();
		if (data.hasKey("mfr:slime")) {
			entity.motionY = data.getDouble("mfr:slime");
			data.removeTag("mfr:slime");
		}

		if (Math.abs(entity.motionY) < 0.1 && !entity.isSneaking()) {
			double d = 0.4 + Math.abs(entity.motionY) * 0.2;
			entity.motionX *= d;
			entity.motionZ *= d;
		}
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
	}

	public static class SoundType extends Block.SoundType {

		public SoundType(String name, float volume, float frequency) {

			super(name, volume, frequency);
		}

		@Override
		public String getBreakSound() {

			return "mob.slime.big";
		}

		@Override
		public String getStepResourcePath() {

			return "mob.slime.big";
		}

		@Override
		public String func_150496_b() {

			return "mob.slime.small";
		}
	}

}
