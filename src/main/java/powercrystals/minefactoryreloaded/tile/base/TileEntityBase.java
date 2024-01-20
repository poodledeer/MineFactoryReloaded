package powercrystals.minefactoryreloaded.tile.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityBase extends net.minecraft.tileentity.TileEntity
{
	protected String _invName;
	protected boolean inWorld;

	public void setBlockName(String name)
	{
		if (name != null && name.length() == 0)name = null;
		this._invName = name;
	}

	public String getBlockName()
	{
		return _invName;
	}

	@Override
	public void invalidate()
	{
		super.invalidate();
	}

	public void cofh_validate() {
		inWorld = true;
	}

	public void cofh_invalidate() {
		invalidate();
		inWorld = false;
		markChunkDirty();
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		markChunkDirty();
	}

	protected final IChatComponent text(String str)
	{
		return new ChatComponentText(str);
	}

	public void markChunkDirty()
	{
		worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
	}

	public void notifyNeighborTileChange()
	{
		if (getBlockType() != null)
		{
			worldObj.func_147453_f(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
		}
	}

	public void onNeighborTileChange(int x, int y, int z) {}

	public void onNeighborBlockChange() {}

	public void onMatchedNeighborBlockChange() {}

	public void getTileInfo(List<IChatComponent> info, ForgeDirection side, EntityPlayer player, boolean debug) {}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}

    @Override
	@SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return -1D;
    }

    @Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRenderInPass(int pass)
    {
        return pass == 0 && getMaxRenderDistanceSquared() != -1D;
    }

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		if (tag.hasKey("x") && tag.hasKey("y") && tag.hasKey("z"))
		{
			super.readFromNBT(tag);
		}

		if (tag.hasKey("display"))
		{
			NBTTagCompound display = tag.getCompoundTag("display");
			if (display.hasKey("Name"))
			{
				this.setBlockName(display.getString("Name"));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		writeItemNBT(tag);
	}

	public void writeItemNBT(NBTTagCompound tag)
	{
		if (_invName != null)
		{
			NBTTagCompound display = new NBTTagCompound();
			display.setString("Name", _invName);
			tag.setTag("display", display);
		}
	}

	private static final long HASH_A = 0x1387D;
	private static final long HASH_C = 0x3A8F05C5;

	@Override
	public int hashCode() {
		final int xTransform = (int)((HASH_A * (xCoord ^ 0x1AFF2BAD) + HASH_C) & 0xFFFFFFFF);
		final int zTransform = (int)((HASH_A * (zCoord ^ 0x25C8B353) + HASH_C) & 0xFFFFFFFF);
		final int yTransform = (int)((HASH_A * (yCoord ^ 0x39531FCD) + HASH_C) & 0xFFFFFFFF);
		return xTransform ^ zTransform ^ yTransform;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof TileEntityBase)
		{
			TileEntityBase te = (TileEntityBase)obj;
			return (te.xCoord == xCoord) & te.yCoord == yCoord & te.zCoord == zCoord &
					worldObj == te.worldObj && te.isInvalid() == isInvalid();
		}
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(x="+xCoord+",y="+yCoord+",z="+zCoord+")@"+System.identityHashCode(this);
	}
}
