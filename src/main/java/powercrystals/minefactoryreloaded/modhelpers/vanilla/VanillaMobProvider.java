package powercrystals.minefactoryreloaded.modhelpers.vanilla;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.RandomMob;
import powercrystals.minefactoryreloaded.core.AutoEnchantmentHelper;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.farmables.grindables.GrindableZombiePigman;

public class VanillaMobProvider implements IRandomMobProvider
{
	@SuppressWarnings("unchecked")
	@Override
	public List<RandomMob> getRandomMobs(World world)
	{
		List<RandomMob> mobs = new ArrayList<RandomMob>();
		
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityChicken.class,   world), 130));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntitySheep.class,     world), 100));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityCow.class,       world), 100));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityPig.class,       world), 100));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityBat.class,       world),  35));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntitySquid.class,     world),  30));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityCreeper.class,   world),  25));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityMooshroom.class, world),  20));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntitySlime.class,     world),  20));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityOcelot.class,    world),  20));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityWolf.class,      world),  20));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityHorse.class,     world),  20));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityGhast.class,     world),  15));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityWitch.class,     world),  10));
		
		EntityXPOrb batJockey = prepareXPOrb(world);
		EntityBat invisibat = MFRUtil.prepareMob(EntityBat.class, world);
		invisibat.addPotionEffect(new PotionEffect(Potion.invisibility.id, Short.MAX_VALUE));
		batJockey.mountEntity(invisibat);
		mobs.add(new RandomMob(invisibat, 55));
		
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityMinecartHopper.class, world), 15));
		
		EntityPig sheep = MFRUtil.prepareMob(EntityPig.class, world);
		for (EntityAITaskEntry a : (List<EntityAITaskEntry>)sheep.tasks.taskEntries)
			if (a.action instanceof EntityAIPanic)
			{
				sheep.tasks.removeTask(a.action);
				break;
			}
		sheep.tasks.addTask(1, new EntityAIAttackOnCollide(sheep, EntityPlayer.class, 1.5D, true));
		sheep.targetTasks.addTask(1, new EntityAIHurtByTarget(sheep, false));
		sheep.setCurrentItemOrArmor(0, new ItemStack(Items.golden_axe, 1, 5));
		sheep.setEquipmentDropChance(0, Float.NEGATIVE_INFINITY);
		sheep.setCustomNameTag("SHEEP");
		sheep.setAlwaysRenderNameTag(true);
		mobs.add(new RandomMob(sheep, 10));
		
		EntityCreeper chargedCreeper = MFRUtil.prepareMob(EntityCreeper.class, world);
		NBTTagCompound creeperNBT = new NBTTagCompound(); 
		chargedCreeper.writeToNBT(creeperNBT);
		creeperNBT.setBoolean("powered", true);
		creeperNBT.setShort("Fuse", (short)120);
		chargedCreeper.readFromNBT(creeperNBT);
		mobs.add(new RandomMob(chargedCreeper, 5));
		
		EntityTNTPrimed armedTNT = MFRUtil.prepareMob(EntityTNTPrimed.class, world);
		armedTNT.fuse = 120;
		mobs.add(new RandomMob(armedTNT, 5));
		
		EntitySlime invisislime = MFRUtil.prepareMob(EntitySlime.class, world);
		invisislime.addPotionEffect(new PotionEffect(Potion.invisibility.id, 120 * 20));
		mobs.add(new RandomMob(invisislime, 5));
		
		EntityMooshroom invisishroom = MFRUtil.prepareMob(EntityMooshroom.class, world);
		invisishroom.addPotionEffect(new PotionEffect(Potion.invisibility.id, 120 * 20));
		mobs.add(new RandomMob(invisishroom, 5));
		
		EntityWolf invisiwolf = MFRUtil.prepareMob(EntityWolf.class, world);
		invisiwolf.addPotionEffect(new PotionEffect(Potion.invisibility.id, 120 * 20));
		invisiwolf.setAngry(true);
		mobs.add(new RandomMob(invisiwolf, 5));

		EntityTNTPrimed tntJockey = MFRUtil.prepareMob(EntityTNTPrimed.class, world);
		EntityBat tntMount = MFRUtil.prepareMob(EntityBat.class, world);
		tntJockey.fuse = 120;
		tntJockey.mountEntity(tntMount);
		mobs.add(new RandomMob(tntMount, 2));
		
		EntitySkeleton skeleton1 = MFRUtil.prepareMob(EntitySkeleton.class, world);
		EntitySkeleton skeleton2 = MFRUtil.prepareMob(EntitySkeleton.class, world);
		EntitySkeleton skeleton3 = MFRUtil.prepareMob(EntitySkeleton.class, world);
		EntitySkeleton skeleton4 = MFRUtil.prepareMob(EntitySkeleton.class, world);
		skeleton4.mountEntity(skeleton3);
		skeleton3.mountEntity(skeleton2);
		skeleton2.mountEntity(skeleton1);
		mobs.add(new RandomMob(skeleton1, 2));
		
		EntityBlaze blazeJockey = MFRUtil.prepareMob(EntityBlaze.class, world);
		EntityGhast blazeMount = MFRUtil.prepareMob(EntityGhast.class, world);
		blazeJockey.mountEntity(blazeMount);
		mobs.add(new RandomMob(blazeMount, 2));
		
		EntityCreeper creeperJockey = MFRUtil.prepareMob(EntityCreeper.class, world);
		EntityCaveSpider creeperMount = MFRUtil.prepareMob(EntityCaveSpider.class, world);
		creeperJockey.mountEntity(creeperMount);
		mobs.add(new RandomMob(creeperMount, 2));

		tntJockey = MFRUtil.prepareMob(EntityTNTPrimed.class, world);
		EntityXPOrb tntMount2 = prepareXPOrb(world);
		tntJockey.fuse = 120;
		tntJockey.mountEntity(tntMount2);
		mobs.add(new RandomMob(tntMount2, 2));

		EntityPigZombie derp = MFRUtil.prepareMob(EntityPigZombie.class, world);
		derp.onSpawnWithEgg(null);
		derp.addPotionEffect(new PotionEffect(Potion.regeneration.id, 120 * 20));
		derp.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(1.1);
		derp.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(18);
		derp.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32);
		derp.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1);
		derp.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50);
		derp.stepHeight = 2;
		{
			ItemStack armor = new ItemStack(Items.leather_leggings);
			int i = EntityLiving.getArmorPosition(armor);
			armor.setStackDisplayName(new String(new char[]{77, 97, 110, 32, 80, 97, 110, 116, 115}));
			if (world.rand.nextBoolean()) {
				derp.setCustomNameTag("Super " + new String(new char[]{90, 105, 115, 116, 101, 97, 117}));
				armor = AutoEnchantmentHelper.addRandomEnchantment(derp.getRNG(), armor, 60000, true);
				derp.setCurrentItemOrArmor(i, armor);
				derp.setEquipmentDropChance(i, 0.01F);
				armor = derp.getRNG().nextInt(10) == 0 ? new ItemStack(Items.lava_bucket) : GrindableZombiePigman.sign.copy();
				derp.setCurrentItemOrArmor(0, armor);
				derp.setEquipmentDropChance(0, 2.0F);
			} else {
				derp.setCustomNameTag(new String(new char[]{80, 105, 103, 68, 101, 114, 112}));
				armor = AutoEnchantmentHelper.addRandomEnchantment(derp.getRNG(), armor, 90, true);
				derp.setCurrentItemOrArmor(i, armor);
				derp.setEquipmentDropChance(i, 0.05F);
				armor = new ItemStack(Items.lava_bucket);
				derp.setCurrentItemOrArmor(0, armor);
				derp.setEquipmentDropChance(0, 0.5F);
			}
			derp.setAlwaysRenderNameTag(true);
			derp.func_110163_bv();
		}
		mobs.add(new RandomMob(derp, 1, false));
		
		creeperJockey = MFRUtil.prepareMob(EntityCreeper.class, world);
		EntityXPOrb creeperMount2 = prepareXPOrb(world);
		creeperJockey.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 20));
		creeperJockey.onStruckByLightning(null);
		creeperJockey.mountEntity(creeperMount2);
		mobs.add(new RandomMob(creeperMount2, 1));

		EntityEnderman direBane = MFRUtil.prepareMob(EntityEnderman.class, world);
		direBane.addPotionEffect(new PotionEffect(Potion.regeneration.id, 120 * 20));
		direBane.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 120 * 20));
		direBane.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(120);
		direBane.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7);
		direBane.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(15);
		direBane.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32);
		direBane.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1);
		direBane.stepHeight = 2;
		EntityPlayer player = world.getPlayerEntityByName("direwolf20");
		if (player != null)
		{
			direBane.setCustomNameTag("Bane of direwolf");
			direBane.setAlwaysRenderNameTag(true);
			direBane.func_110163_bv();
			ItemStack armor = new ItemStack(Items.golden_chestplate);
			armor = AutoEnchantmentHelper.addRandomEnchantment(direBane.getRNG(), armor, 60, true);
			int i = EntityLiving.getArmorPosition(armor);
			direBane.setCurrentItemOrArmor(i, armor);
			direBane.setEquipmentDropChance(i, 2.0F);
		}
		mobs.add(new RandomMob(direBane, 1));
		
		return mobs;
	}
	
	private EntityXPOrb prepareXPOrb(World world)
	{
		EntityXPOrb orb = MFRUtil.prepareMob(EntityXPOrb.class, world);
		orb.xpValue = 1;
		orb.xpOrbAge = Short.MIN_VALUE + 6001;
		orb.field_70532_c = Short.MAX_VALUE;
		return orb;
	}
}
