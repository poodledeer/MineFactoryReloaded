package powercrystals.minefactoryreloaded.block.fluid;

import cofh.core.fluid.BlockFluidCoFHBase;
import cofh.lib.util.RegistryUtils;
import cofh.lib.util.WeightedRandomItemStack;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class BlockFactoryFluid extends BlockFluidCoFHBase implements IRedNetDecorative { // TODO: convert to BlockFluidFinite

	private static DamageSource steam = new DamageSource("steam"); {
		steam.setDamageBypassesArmor().setFireDamage().setDifficultyScaled();
		steam.setDamageIsAbsolute();
	}
	public static final Material material = new MaterialLiquid(MapColor.waterColor);

	@SideOnly(Side.CLIENT)
	protected IIcon _iconFlowing;
	@SideOnly(Side.CLIENT)
	protected IIcon _iconStill;
	@SideOnly(Side.CLIENT)
	public int color;
	protected String fluidName;

	private static Fluid ensureFluid(String name) {

		Fluid fluid = FluidRegistry.getFluid(name);
		if (fluid.canBePlacedInWorld())
			ReflectionHelper.setPrivateValue(Fluid.class, fluid, null, "block");
		return fluid;
	}

	public BlockFactoryFluid(String liquidName) {

		this(liquidName, Material.water);
	}

	public BlockFactoryFluid(String liquidName, Material material) {

		super(ensureFluid(liquidName), material, liquidName);
		setBlockName("mfr." + liquidName + ".still");
		setHardness(100.0F);
		setLightOpacity(3);
		setDisplaceFluids(true);
		fluidName = liquidName;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

		super.onEntityCollidedWithBlock(world, x, y, z, entity);

		if (entity instanceof EntityLivingBase) {
			NBTTagCompound data = entity.getEntityData();
			if (world.isRemote || data.getLong("mfr:fluidTimer" + fluidName) > world.getTotalWorldTime()) {
				return;
			}
			data.setLong("mfr:fluidTimer" + fluidName, world.getTotalWorldTime() + 40);

			EntityLivingBase ent = (EntityLivingBase) entity;
			if (this == MFRThings.milkLiquid) {
				ent.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 6 * 20, 0, true));
			}
			else if (this == MFRThings.sludgeLiquid) {
				ent.addPotionEffect(new PotionEffect(Potion.wither.id, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(Potion.weakness.id, 12 * 20, 0, true));
				ent.addPotionEffect(new PotionEffect(Potion.confusion.id, 16 * 20, 0, true));
			}
			else if (this == MFRThings.sewageLiquid) {
				ent.addPotionEffect(new PotionEffect(Potion.hunger.id, 12 * 20, 0, true));
				ent.addPotionEffect(new PotionEffect(Potion.poison.id, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 12 * 20, 0, true));
			}
			else if (this == MFRThings.essenceLiquid) {
				ent.addPotionEffect(new PotionEffect(Potion.nightVision.id, 60 * 20, 0, true));
			}
			else if (this == MFRThings.biofuelLiquid) {
				ent.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 12 * 20, 0, true));
			}
			else if (this == MFRThings.steamFluid) {
				ent.attackEntityFrom(steam, 6);
			}
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {

		int light = super.getLightValue(world, x, y, z);
		if (maxScaledLight != 0)
			light = Math.max(light, 2);
		return light;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {

		super.onBlockAdded(world, x, y, z);
		checkCanStay(world, x, y, z, world.rand);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {

		checkCanStay(world, x, y, z, rand);
		super.updateTick(world, x, y, z, rand);
	}

	protected void checkCanStay(World world, int x, int y, int z, Random rand) {

		/*BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		l: if (biome != null && biome.getFloatTemperature() > 1.9f)//*/
		l: if (world.provider.isHellWorld)
		{
			if (!isSourceBlock(world, x, y, z)) {
				if (world.setBlockToAir(x, y, z))
					return;
				break l;
			}
			ItemStack drop = null;
			Block block = Blocks.air;
			if (this == MFRThings.milkLiquid) {
				if (rand.nextInt(50) == 0)
					drop = new ItemStack(Items.dye, rand.nextInt(2), 15);
			}
			else if (this == MFRThings.sludgeLiquid) {
				drop = ((WeightedRandomItemStack) WeightedRandom.
						getRandomItem(rand, MFRRegistry.getSludgeDrops())).getStack();
			}
			else if (this == MFRThings.sewageLiquid) {
				drop = new ItemStack(MFRThings.fertilizerItem, 1 + rand.nextInt(2));
			}
			else if (this == MFRThings.essenceLiquid) {
				if (world.setBlockToAir(x, y, z)) {
					int i = rand.nextInt(5) + 10;
					while (i > 0) {
						int j = EntityXPOrb.getXPSplit(i);
						i -= j;
						world.spawnEntityInWorld(new EntityXPOrb(world,
								x + rand.nextDouble(), y + rand.nextDouble(), z + rand.nextDouble(), j));
					}
					fizz(world, x, y, z, rand);
					return;
				}
				break l;
			}
			else if (this == MFRThings.meatLiquid) {
				if (rand.nextInt(5) != 0)
					drop = new ItemStack(MFRThings.meatIngotRawItem, rand.nextInt(2));
				else
					drop = new ItemStack(MFRThings.meatIngotCookedItem, rand.nextInt(2));
			}
			else if (this == MFRThings.pinkSlimeLiquid) {
				if (rand.nextBoolean())
					drop = new ItemStack(MFRThings.pinkSlimeItem, rand.nextInt(3));
				else if (rand.nextInt(5) != 0)
					drop = new ItemStack(MFRThings.meatNuggetRawItem, rand.nextInt(2));
				else
					drop = new ItemStack(MFRThings.meatNuggetCookedItem, rand.nextInt(2));
			}
			else if (this == MFRThings.chocolateMilkLiquid) {
				if (rand.nextBoolean())
					drop = new ItemStack(Items.dye, rand.nextInt(2), 3);
			}
			else if (this == MFRThings.mushroomSoupLiquid) {
				if (rand.nextInt(5) == 0)
					block = (rand.nextBoolean() ? Blocks.brown_mushroom : Blocks.red_mushroom);
				else if (rand.nextBoolean())
					drop = new ItemStack(Blocks.brown_mushroom, rand.nextInt(2));
				else
					drop = new ItemStack(Blocks.red_mushroom, rand.nextInt(2));
			}
			if (world.setBlock(x, y, z, block, 0, 3)) {
				if (drop != null && drop.stackSize > 0)
					this.dropBlockAsItem(world, x, y, z, drop);

				fizz(world, x, y, z, rand);
				return;
			}
		}
	}

	protected void fizz(World world, int x, int y, int z, Random rand) {

		world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D,
			"random.fizz", 0.5F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F);
		for (int l = 0; l < 8; ++l) {
			world.spawnParticle("largesmoke",
				x + rand.nextDouble(), y + rand.nextDouble(), z + rand.nextDouble(),
				0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public String getUnlocalizedName() {

		return "fluid." + this.unlocalizedName;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		String pre = "minefactoryreloaded:", name = getUnlocalizedName();
		if (!RegistryUtils.blockTextureExists(pre + name)) {
			pre += "fluid/";
		}
		setIcons(ir.registerIcon(name = pre + name), ir.registerIcon(name.replace(".still", ".flowing")));
	}

	@SideOnly(Side.CLIENT)
	public void setIcons(IIcon still, IIcon flowing) {

		_iconStill = still;
		_iconFlowing = flowing;
		setParticleColor(color = RegistryUtils.getBlockTextureColor(still.getIconName()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {

		return side <= 1 ? _iconStill : _iconFlowing;
	}

}
