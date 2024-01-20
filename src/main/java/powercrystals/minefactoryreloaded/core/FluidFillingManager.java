package powercrystals.minefactoryreloaded.core;

import cofh.lib.util.position.Area;
import cofh.lib.util.position.BlockPosition;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.core.BlockPool.BlockNode;

public class FluidFillingManager implements IHarvestManager
{
	private BlockPool _blocks;
	private boolean _isDone;
	
	private Area _area;
	private World _world;

	public FluidFillingManager(World world, Area area)
	{
		reset(world, area, null, null);
		_isDone = true;
	}

	@Override
	public BlockPosition getNextBlock()
	{
		BlockNode bn = _blocks.poke();
		return new BlockPosition(bn.x, bn.y, bn.z);
	}

	@Override
	public void moveNext()
	{
		searchForFreeBlocks(_blocks.shift());
		if (_blocks.size() == 0)
		{
			_isDone = true;
		}
	}

	private void searchForFreeBlocks(BlockNode bn)
	{
		BlockNode cur;

		SideOffset[] sides = SideOffset.ADJACENT;

		for (int i = 0, e = sides.length; i < e; ++i)
		{
			SideOffset side = sides[i];
			cur = BlockPool.getNext(bn.x + side.offsetX, bn.y + side.offsetY, bn.z + side.offsetZ);
			if (isValid(cur))
				_blocks.push(cur);
			else
				cur.free();
		}

		bn.free();
	}

	private boolean isValid(BlockNode bp)
	{
		Area area = _area;
		if (bp.x < area.xMin || bp.x > area.xMax ||
				bp.y < area.yMin || bp.y > area.yMax ||
				bp.z < area.zMin || bp.z > area.zMax ||
				!_world.blockExists(bp.x, bp.y, bp.z))
			return false;

		Block block = _world.getBlock(bp.x, bp.y, bp.z);
		return block.isReplaceable(_world, bp.x, bp.y, bp.z);
	}

	@Override
	public void reset(World world, Area area, HarvestMode harvestMode, Map<String, Boolean> s)
	{
		setWorld(world);
		_area = area;
		free();
		_isDone = false;
		_blocks = new BlockPool();
		BlockPosition bp = area.getOrigin();
		_blocks.push(BlockPool.getNext(bp.x, bp.y, bp.z));
	}

	@Override
	public void setWorld(World world)
	{
		_world = world;
	}

	@Override
	public boolean getIsDone()
	{
		return _isDone;
	}

	@Override
	public BlockPosition getOrigin()
	{
		return _area.getOrigin();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setBoolean("done", _isDone);
		BlockPosition o = getOrigin();
		data.setIntArray("area", new int[] {o.x - _area.xMin, o.y - _area.yMin, _area.yMax - o.y});
		data.setIntArray("origin", new int[] {o.x, o.y, o.z});
		NBTTagList list = new NBTTagList();
		BlockNode bn = _blocks.poke();
		while (bn != null)
		{
			NBTTagCompound p = new NBTTagCompound();
			p.setInteger("x", bn.x);
			p.setInteger("y", bn.y);
			p.setInteger("z", bn.z);
			list.appendTag(p);
			bn = bn.next;
		}
		data.setTag("curPos", list);
		tag.setTag("harvestManager", data);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		free();
		_blocks = new BlockPool();

		NBTTagCompound data = tag.getCompoundTag("harvestManager");
		_isDone = data.getBoolean("done");
		int[] area = data.getIntArray("area"), o = data.getIntArray("origin");
		if (area == null | o == null || o.length < 3 | area.length < 3)
		{
			_area = new Area(new BlockPosition(0,-1,0),0,0,0);
			_isDone = true;
			return;
		}
		_area = new Area(new BlockPosition(o[0], o[1], o[2]), area[0], area[1], area[2]);
		NBTTagList list = (NBTTagList)data.getTag("curPos");
		for (int i = 0, e = list.tagCount(); i < e; ++i)
		{
			NBTTagCompound p = list.getCompoundTagAt(i);
			_blocks.push(BlockPool.getNext(p.getInteger("x"), p.getInteger("y"), p.getInteger("z")));
		}
	}

	@Override
	public void free()
	{
		if (_blocks != null) while (_blocks.poke() != null)
			_blocks.shift().free();
		_isDone = true;
	}
}
