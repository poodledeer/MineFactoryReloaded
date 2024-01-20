package powercrystals.minefactoryreloaded.setup.village;

import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.CHEST_GEN;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.common.ChestGenHooks;

import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ComponentZoologistHouse extends StructureVillagePieces.Village
{
	private Block brickId, paneId;
	private int brickMeta, paneMeta;
	private int benchMeta;
	private int lightMeta;
	private boolean hasMadeChest;

	public ComponentZoologistHouse() {}

	public ComponentZoologistHouse(Start startPiece, int componentType, Random rand, StructureBoundingBox sbb, int coordBaseMode)
	{
		super(startPiece, componentType);
		this.coordBaseMode = coordBaseMode;
		boundingBox = sbb;

		brickId = MFRThings.factoryDecorativeBrickBlock;
		brickMeta = rand.nextInt(16);
		if (brickMeta > 0) ++brickMeta; // 1 is glowstone small bricks
		if (brickMeta > 6) ++brickMeta; // 7 is glowstone large bricks
		if (brickMeta > 11) // 12 & 13 are meat
			brickMeta += 2;
		if (brickMeta == 15) // 15 is unused
		{
			brickId = Blocks.brick_block; // because large red bricks are also an option
			brickMeta = 0; // no need for weird meta, and makes lights correct size
		}

		if (brickMeta > 15) // covers the 4 skipped blocks in factoryDecorativeBrickBlock
		{
			brickId = MFRThings.factoryDecorativeStoneBlock;
			brickMeta -= 12; // shift into proper range (16 - 12 = 4: first stone brick block)
			lightMeta = brickMeta < 6 ? 7 : 1;
		}
		else
			lightMeta = brickMeta < 6 ? 1 : 7;

		paneId = Blocks.glass_pane;
		paneMeta = 0;
		// getBiomeSpecificBlock
		if (func_151558_b(paneId, paneMeta) == paneId)
		{
			paneId = MFRThings.factoryGlassPaneBlock;
			paneMeta = rand.nextInt(16);
		}

		benchMeta = rand.nextInt(10) == 0 ? 12 : 0;
	}

	@Override // write to NBT
	protected void func_143012_a(NBTTagCompound tag)
	{
		super.func_143012_a(tag);
		tag.setBoolean("Chest", this.hasMadeChest);
		tag.setIntArray("blocks", new int[]{brickMeta, paneMeta, lightMeta, benchMeta});
		tag.setString("brick", Block.blockRegistry.getNameForObject(brickId));
		tag.setString("pane", Block.blockRegistry.getNameForObject(paneId));
	}

	@Override // read from NBT
	protected void func_143011_b(NBTTagCompound tag)
	{
		super.func_143011_b(tag);
		this.hasMadeChest = tag.getBoolean("Chest");
		int[] blocks = tag.getIntArray("blocks");
		if (blocks == null || blocks.length != 4)
			return;

		brickMeta = blocks[0];
		paneMeta = blocks[1];
		lightMeta = blocks[2];
		benchMeta = blocks[3];
		brickId = Block.getBlockFromName(tag.getString("brick"));
		paneId = Block.getBlockFromName(tag.getString("pane"));
	}

	@SuppressWarnings("rawtypes")
	public static ComponentZoologistHouse buildComponent(PieceWeight villagePiece,
			Start startPiece, List pieces, Random random, int p1, int p2,
			int p3, int p4, int p5)
	{
		StructureBoundingBox sbb = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3,
				0, 0, 0, 9, 9, 6, p4);
		return (!canVillageGoDeeper(sbb)) || (StructureComponent.findIntersecting(pieces, sbb) != null)
				? null : new ComponentZoologistHouse(startPiece, p5, random, sbb, p4);
	}

	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb)
	{
		if (field_143015_k < 0)
		{
			field_143015_k = getAverageGroundLevel(world, sbb);

			if (field_143015_k < 0)
			{
				return true;
			}

			boundingBox.offset(0, field_143015_k - boundingBox.maxY + 9 - 1, 0);
		}
		Block mfrBrickId = MFRThings.factoryDecorativeBrickBlock;
		Block mfrLogId = MFRThings.rubberWoodBlock;
		int logMeta = 12;

		fillWithBlocks(world, sbb, 1, 1, 1, 7, 5, 4, Blocks.air, Blocks.air, false);
		fillWithMetadataBlocks(world, sbb, 0, 0, 0, 8, 0, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 0, 5, 0, 8, 5, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 0, 6, 1, 8, 6, 4, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 0, 7, 2, 8, 7, 3, brickId, brickMeta, brickId, brickMeta, false);
		int i = getMetadataWithOffset(Blocks.oak_stairs, 3);
		int j = getMetadataWithOffset(Blocks.oak_stairs, 2);
		int k;
		int l;

		for (k = -1; k <= 2; ++k)
		{
			for (l = 0; l <= 8; ++l)
			{
				placeBlockAtCurrentPosition(world, Blocks.oak_stairs, i, l, 6 + k, k, sbb);
				placeBlockAtCurrentPosition(world, Blocks.oak_stairs, j, l, 6 + k, 5 - k, sbb);
			}
		}

		fillWithMetadataBlocks(world, sbb, 0, 1, 0, 0, 1, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 1, 1, 5, 8, 1, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 8, 1, 0, 8, 1, 4, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 2, 1, 0, 7, 1, 0, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 0, 2, 0, 0, 4, 0, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 0, 2, 5, 0, 4, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 8, 2, 5, 8, 4, 5, brickId, brickMeta, brickId, brickMeta, false);
		fillWithMetadataBlocks(world, sbb, 8, 2, 0, 8, 4, 0, brickId, brickMeta, brickId, brickMeta, false);
		fillWithBlocks(world, sbb, 0, 2, 1, 0, 4, 4, Blocks.planks, Blocks.planks, false);
		fillWithBlocks(world, sbb, 1, 2, 5, 7, 4, 5, Blocks.planks, Blocks.planks, false);
		fillWithBlocks(world, sbb, 8, 2, 1, 8, 4, 4, Blocks.planks, Blocks.planks, false);
		fillWithBlocks(world, sbb, 1, 2, 0, 7, 4, 0, Blocks.planks, Blocks.planks, false);
		//{ Windows
		//{ Large front window
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 3, 2, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 4, 2, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 5, 2, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 6, 2, 0, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 7, 2, 0, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 3, 3, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 4, 3, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 5, 3, 0, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 6, 3, 0, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 7, 3, 0, sbb);
		//}
		//{ Side window near door
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 0, 2, 1, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 0, 2, 2, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 0, 2, 3, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 0, 2, 4, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 0, 3, 1, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 0, 3, 2, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 0, 3, 3, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 0, 3, 4, sbb);
		//}
		//{ Window opposite above window
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 8, 2, 1, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 8, 2, 2, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 8, 2, 3, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 8, 2, 4, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 8, 3, 1, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 8, 3, 2, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 8, 3, 3, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 8, 3, 4, sbb);
		//}
		//{ Two back windows
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 1, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 2, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 3, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 4, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 5, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, paneId, paneMeta, 6, 2, 5, sbb);
		placeBlockAtCurrentPosition(world, mfrLogId,logMeta, 7, 2, 5, sbb);
		//}
		//}
		fillWithBlocks(world, sbb, 1, 4, 1, 7, 4, 1, Blocks.planks, Blocks.planks, false);
		fillWithBlocks(world, sbb, 1, 4, 4, 7, 4, 4, Blocks.planks, Blocks.planks, false);
		fillWithBlocks(world, sbb, 1, 3, 4, 7, 3, 4, Blocks.bookshelf, Blocks.bookshelf, false);
		//{ Wrap around bench
		k = getMetadataWithOffset(Blocks.oak_stairs, 0);
		placeBlockAtCurrentPosition(world, Blocks.oak_stairs, k, 7, 1, 4, sbb);
		placeBlockAtCurrentPosition(world, Blocks.oak_stairs, k, 7, 1, 3, sbb);
		k = getMetadataWithOffset(Blocks.oak_stairs, 3);
		placeBlockAtCurrentPosition(world, Blocks.oak_stairs, k, 6, 1, 4, sbb);
		placeBlockAtCurrentPosition(world, Blocks.oak_stairs, k, 5, 1, 4, sbb);
		placeBlockAtCurrentPosition(world, Blocks.oak_stairs, k, 4, 1, 4, sbb);
		placeBlockAtCurrentPosition(world, Blocks.oak_stairs, k, 3, 1, 4, sbb);
		//{ "Tables"
		placeBlockAtCurrentPosition(world, Blocks.fence, 0, 6, 1, 3, sbb);
		placeBlockAtCurrentPosition(world, Blocks.wooden_pressure_plate, 0, 6, 2, 3, sbb);
		placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 1, 3, sbb);
		placeBlockAtCurrentPosition(world, Blocks.wooden_pressure_plate, 0, 4, 2, 3, sbb);
		//}
		//}
		k = benchMeta; // workbench or meat
		placeBlockAtCurrentPosition(world, k == 0 ? Blocks.crafting_table : mfrBrickId, k, 7, 1, 1, sbb);

		k = lightMeta; // overhead lights (match large/small bricks)
		fillWithMetadataBlocks(world, sbb, 1, 5, 2, 7, 5, 3, mfrBrickId, k, mfrBrickId, k, false);
		//{ Door
		placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 1, 0, sbb);
		placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 2, 0, sbb);
		placeDoorAtCurrentPosition(world, sbb, random, 1, 1, 0, getMetadataWithOffset(Blocks.wooden_door, 1));
		//}

		if (getBlockAtCurrentPosition(world, 1, 0, -1, sbb).equals(Blocks.air) &&
				!getBlockAtCurrentPosition(world, 1, -1, -1, sbb).equals(Blocks.air))
		{
			placeBlockAtCurrentPosition(world, Blocks.stone_stairs,
					getMetadataWithOffset(Blocks.stone_stairs, 3), 1, 0, -1, sbb);
		}

		for (l = 0; l < 6; ++l)
		{
			for (int i1 = 0; i1 < 9; ++i1)
			{
				clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
				func_151554_b(world, brickId, brickMeta, i1, -1, l, sbb);
				// fillCurrentPositionBlocksDownwards ^
			}
		}
		if (!this.hasMadeChest)
		{
			i = this.getYWithOffset(1);
			j = this.getXWithOffset(1, 4);
			k = this.getZWithOffset(1, 4);

			if (sbb.isVecInside(j, i, k))
			{
				this.hasMadeChest = true;
				generateStructureChestContents(world, sbb, random, 1, 1, 4,
						ChestGenHooks.getItems(CHEST_GEN, random), ChestGenHooks.getCount(CHEST_GEN, random));
			}
		}

		spawnVillagers(world, sbb, 2, 1, 2, 1);
		return true;
	}

	@Override
	protected int getVillagerType(int par1)
	{
		return MFRConfig.zoolologistEntityId.getInt();
	}
}
