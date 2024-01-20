package powercrystals.minefactoryreloaded.core;

import cofh.core.network.PacketCoFHBase;
import cofh.core.util.nbt.NBTTagSmartByteArray;
import cofh.lib.util.position.Area;
import cofh.lib.util.position.BlockPosition;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.core.BlockPool.BlockNode;

public class TreeHarvestManager implements IHarvestManager {

	private BlockPool _blocks;
	private boolean _isDone;

	private Map<String, Boolean> _settings;
	private HarvestMode _harvestMode;
	private Area _area;
	private World _world;

	public TreeHarvestManager(NBTTagCompound tag, Map<String, Boolean> s) {

		readFromNBT(tag);
		_settings = s;
	}

	public TreeHarvestManager(World world, Area treeArea, HarvestMode harvestMode, Map<String, Boolean> s) {

		reset(world, treeArea, harvestMode, s);
		_isDone = true;
	}

	@Override
	public BlockPosition getNextBlock() {

		BlockNode bn = _blocks.shift();
		searchForTreeBlocks(bn);
		BlockPosition bp = new BlockPosition(bn.x, bn.y, bn.z);
		bn.free();
		return bp;
	}

	@Override
	public void moveNext() {

		if (_blocks.size() == 0) {
			_isDone = true;
		}
	}

	private void searchForTreeBlocks(BlockNode bn) {

		Map<Block, IFactoryHarvestable> harvestables = MFRRegistry.getHarvestables();
		BlockNode cur;

		HarvestType type = getType(bn, harvestables);
		if (type == null || type == HarvestType.TreeFruit)
			return;

		SideOffset[] sides = !_harvestMode.isInverted ? SideOffset.ADJACENT_CUBE :
				SideOffset.ADJACENT_CUBE_INVERTED;

		for (int i = 0, e = sides.length; i < e; ++i) {
			SideOffset side = sides[i];
			cur = BlockPool.getNext(bn.x + side.offsetX, bn.y + side.offsetY, bn.z + side.offsetZ);
			addIfValid(getType(cur, harvestables), cur);
		}
	}

	private void addIfValid(HarvestType type, BlockNode node) {

		if (type != null) {
			if (type == HarvestType.TreeFruit ||
					type == HarvestType.TreeLeaf) {
				_blocks.unshift(node);
				return;
			}
			else if (type == HarvestType.Tree ||
					type == HarvestType.TreeFlipped) {
				_blocks.push(node);
				return;
			}
		}
		node.free();
	}

	private HarvestType getType(BlockNode bp, Map<Block, IFactoryHarvestable> harvestables) {

		Area area = _area;
		if (bp.x < area.xMin || bp.x > area.xMax ||
				bp.y < area.yMin || bp.y > area.yMax ||
				bp.z < area.zMin || bp.z > area.zMax ||
				!_world.blockExists(bp.x, bp.y, bp.z))
			return null;

		Block block = _world.getBlock(bp.x, bp.y, bp.z);
		if (harvestables.containsKey(block)) {
			IFactoryHarvestable h = harvestables.get(block);
			if (h.canBeHarvested(_world, _settings, bp.x, bp.y, bp.z)) {
				return h.getHarvestType();
			}
		}
		return null;
	}

	@Override
	public void reset(World world, Area treeArea, HarvestMode harvestMode, Map<String, Boolean> settings) {

		setWorld(world);
		_harvestMode = harvestMode;
		_area = treeArea;
		free();
		_isDone = false;
		_blocks = new BlockPool();
		BlockPosition bp = treeArea.getOrigin();
		_blocks.push(BlockPool.getNext(bp.x, bp.y, bp.z));
		_settings = settings;
	}

	@Override
	public void setWorld(World world) {

		_world = world;
	}

	@Override
	public boolean getIsDone() {

		return _isDone;
	}

	@Override
	public BlockPosition getOrigin() {

		return _area.getOrigin();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		NBTTagCompound data = new NBTTagCompound();
		data.setBoolean("done", _isDone);
		data.setInteger("mode", _harvestMode.ordinal());
		BlockPosition o = getOrigin();
		data.setIntArray("area", new int[] { o.x - _area.xMin, o.y - _area.yMin, _area.yMax - o.y });
		data.setIntArray("origin", new int[] { o.x, o.y, o.z });
		NBTTagSmartByteArray list = new NBTTagSmartByteArray(_blocks.size() * 3 * 3);
		BlockNode bn = _blocks.poke();
		list.addVarInt(_blocks.size());
		while (bn != null) {
			list.addVarInt(bn.x).addVarInt(bn.y).addVarInt(bn.z);
			bn = bn.next;
		}
		data.setTag("curPos", list);
		tag.setTag("harvestManager", data);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		free();
		_blocks = new BlockPool();

		NBTTagCompound data = tag.getCompoundTag("harvestManager");
		_isDone = data.getBoolean("done");
		_harvestMode = HarvestMode.values()[data.getInteger("mode")];
		int[] area = data.getIntArray("area"), o = data.getIntArray("origin");
		if (area == null | o == null || o.length < 3 | area.length < 3) {
			_area = new Area(new BlockPosition(0, -1, 0), 0, 0, 0);
			_isDone = true;
			return;
		}
		_area = new Area(new BlockPosition(o[0], o[1], o[2]), area[0], area[1], area[2]);
		NBTBase baseList = data.getTag("curPos");
		if (baseList.getId() == Constants.NBT.TAG_BYTE_ARRAY) {
			PacketCoFHBase tempPacket = new PacketCoFHBase(((NBTTagByteArray)baseList).func_150292_c()){
				@Override public void handlePacket(EntityPlayer player, boolean isServer) {}
				};
			for (int length = tempPacket.getVarInt(); length-- > 0;) {
				_blocks.push(BlockPool.getNext(tempPacket.getVarInt(), tempPacket.getVarInt(), tempPacket.getVarInt()));
			}
		} else {
			NBTTagList list = (NBTTagList) baseList;
			if (list.func_150303_d() == Constants.NBT.TAG_INT_ARRAY) {
				for (int i = 0, e = list.tagCount(); i < e; ++i) {
					int[] p = list.func_150306_c(i);
					_blocks.push(BlockPool.getNext(p[0], p[1], p[2]));
				}
			}
			else
				for (int i = 0, e = list.tagCount(); i < e; ++i) {
					NBTTagCompound p = list.getCompoundTagAt(i);
					_blocks.push(BlockPool.getNext(p.getInteger("x"), p.getInteger("y"), p.getInteger("z")));
				}
		}
		if (_blocks.size() == 0)
			_isDone = true;
	}

	@Override
	public void free() {

		if (_blocks != null) while (_blocks.poke() != null)
			_blocks.shift().free();
		_isDone = true;
	}
}
