package powercrystals.minefactoryreloaded.setup.village;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

public class VillageCreationHandler implements IVillageCreationHandler
{
	@Override
	public PieceWeight getVillagePieceWeight(Random random, int i)
	{
		return new PieceWeight(ComponentZoologistHouse.class, 20, random.nextInt(1) + i);
	}
	
	@Override
	public Class<?> getComponentClass()
	{
		MapGenStructureIO.func_143031_a(ComponentZoologistHouse.class, "minefactoryreloaded:ZoologistHouseStructure");
		return ComponentZoologistHouse.class;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5)
	{
		return ComponentZoologistHouse.buildComponent(villagePiece, startPiece, pieces, random, p1, p2, p3, p4, p5);
	}
}
