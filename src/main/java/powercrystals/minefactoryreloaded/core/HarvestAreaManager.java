package powercrystals.minefactoryreloaded.core;

import cofh.api.item.IAugmentItem;
import cofh.lib.util.position.Area;
import cofh.lib.util.position.BlockPosition;
import cofh.lib.util.position.IRotateableTile;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class HarvestAreaManager <T extends TileEntity & IRotateableTile>
{
	private T _owner;

	private int _originX;
	private int _originY;
	private int _originZ;
	private ForgeDirection _originOrientation;

	private ForgeDirection _overrideDirection;
	private Area _harvestArea;
	private int _radius;
	private int _areaUp;
	private int _areaDown;

	private int _originOffsetX;
	private int _originOffsetY;
	private int _originOffsetZ;

	private List<BlockPosition> _harvestedBlocks;
	private int _currentBlock;
	private boolean _usesBlocks;
	private boolean _upgradeVertical;

	private int _upgradeLevel;
	private float _upgradeModifier;

	public HarvestAreaManager(T owner, int harvestRadius,
			int harvestAreaUp, int harvestAreaDown, float upgradeModifier, boolean usesBlocks)
	{
		_owner = owner;
		_overrideDirection = ForgeDirection.UNKNOWN;
		_radius = harvestRadius;
		_areaUp = harvestAreaUp;
		_areaDown = harvestAreaDown;
		_upgradeModifier = upgradeModifier;

		_originX = owner.xCoord;
		_originY = owner.yCoord;
		_originZ = owner.zCoord;
		_originOrientation = owner.getDirectionFacing();
		_usesBlocks = usesBlocks;
		_upgradeVertical = false;
	}

	@Override
	public String toString()
	{
		return String.format("%s-> %s:%s:%s:%s", _owner, _upgradeLevel, _usesBlocks, _upgradeVertical, _upgradeModifier);
	}

	public void setOriginOffset(int x, int y, int z)
	{
		_originOffsetX = x;
		_originOffsetY = y;
		_originOffsetZ = z;
		checkRecalculate();
	}

	public Area getHarvestArea()
	{
		checkRecalculate();
		return _harvestArea;
	}

	public int getOriginX()
	{
		return _originX;
	}

	public int getOriginY()
	{
		return _originY;
	}

	public int getOriginZ()
	{
		return _originZ;
	}

	public int getRadius()
	{
		return _radius + _upgradeLevel;
	}

	public BlockPosition getNextBlock()
	{
		checkRecalculate();
		BlockPosition next = _harvestedBlocks.get(_currentBlock);
		_currentBlock++;
		if(_currentBlock >= _harvestedBlocks.size())
		{
			_currentBlock = 0;
		}

		return next;
	}

	public void rewindBlock()
	{
		_currentBlock--;
		if(_currentBlock < 0)
		{
			_currentBlock = _harvestedBlocks.size() - 1;
		}
	}

	public void setPosition(int position) {

		_currentBlock = position % _harvestedBlocks.size();
	}

	public int getPosition() {

		return _currentBlock;
	}

	public void setOverrideDirection(ForgeDirection dir)
	{
		_overrideDirection = dir;
	}

	public void setUpgradeVertical(boolean val)
	{
		_upgradeVertical = val;
	}

	public void setUpgradeLevel(int level)
	{
		_upgradeLevel = level;
		recalculateArea();
	}

	public void setAreaUp(int amt)
	{
		_areaUp = amt;
	}

	public void setAreaDown(int amt)
	{
		_areaDown = amt;
	}

	public int getUpgradeLevel()
	{
		return _upgradeLevel;
	}

	public Packet getUpgradePacket()
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("_upgradeLevel", _upgradeLevel);
		return new S35PacketUpdateTileEntity(_owner.xCoord, _owner.yCoord, _owner.zCoord, 255, data);
	}

	public void updateUpgradeLevel(ItemStack stack)
	{
		if (stack == null)
		{
			if (_upgradeLevel != 0)
				setUpgradeLevel(0);
			return;
		}

		int newUpgradeLevel = 0;
		if (stack.getItem() instanceof IAugmentItem)
		{
			IAugmentItem upgrade = (IAugmentItem)stack.getItem();
			int r = upgrade.getAugmentLevel(stack, "radius");
			if (r != 0)
				newUpgradeLevel = (int)(r * _upgradeModifier);
		}

		if(newUpgradeLevel != _upgradeLevel)
			setUpgradeLevel(newUpgradeLevel);
	}

	private void checkRecalculate()
	{
		if(_harvestArea == null)
		{
			recalculateArea();
			return;
		}

		if(		(_overrideDirection != ForgeDirection.UNKNOWN && _originOrientation != _overrideDirection)
				|| (_overrideDirection == ForgeDirection.UNKNOWN && _originOrientation != _owner.getDirectionFacing())
				|| _originX != _owner.xCoord + _originOffsetX
				|| _originY != _owner.yCoord + _originOffsetY
				|| _originZ != _owner.zCoord + _originOffsetZ)
		{
			recalculateArea();
		}
	}

	private void recalculateArea()
	{
		BlockPosition ourpos = BlockPosition.fromRotateableTile(_owner);
		if (_overrideDirection != ForgeDirection.UNKNOWN)
		{
			ourpos.orientation = _overrideDirection;
		}

		_originX = ourpos.x + _originOffsetX;
		_originY = ourpos.y + _originOffsetY;
		_originZ = ourpos.z + _originOffsetZ;
		_originOrientation = ourpos.orientation;

		int radius = _radius + _upgradeLevel;
		int areaUp = _areaUp;
		int areaDown = _areaDown;

		if (ourpos.orientation == ForgeDirection.UP || ourpos.orientation == ForgeDirection.DOWN)
		{
			if (_upgradeVertical)
				if (ourpos.orientation == ForgeDirection.UP)
					areaUp += _upgradeLevel * 2;
				else
					areaDown += _upgradeLevel * 2;
			ourpos.moveForwards(1);
		}
		else
		{
			ourpos.moveForwards(radius + 1);
		}

		ourpos.x += _originOffsetX;
		ourpos.y += _originOffsetY;
		ourpos.z += _originOffsetZ;

		_harvestArea = new Area(ourpos, radius, areaDown, areaUp);
		if (_usesBlocks)
			_harvestedBlocks = _harvestArea.getPositionsBottomFirst();
		_currentBlock = 0;
	}
}
