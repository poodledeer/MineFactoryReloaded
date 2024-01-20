package powercrystals.minefactoryreloaded.tile.tank;

import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.lib.util.position.BlockPosition;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import powercrystals.minefactoryreloaded.core.IDelayedValidate;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

public class TileEntityTank extends TileEntityFactory implements ITankContainerBucketable, IDelayedValidate {

	public static int CAPACITY = FluidHelper.BUCKET_VOLUME * 4;
	TankNetwork grid;
	FluidTankAdv _tank;
	protected byte sides;

	public TileEntityTank() {

		super(null);
		setManageFluids(true);
		_tank = new FluidTankAdv(CAPACITY);
	}

	@Override
	public boolean canUpdate() {

		return false;
	}

	@Override
	public void invalidate() {

		if (grid != null) {
			for (ForgeDirection to : ForgeDirection.VALID_DIRECTIONS) {
				if ((sides & (1 << to.ordinal())) == 0)
					continue;
				TileEntityTank tank = BlockPosition.getAdjacentTileEntity(this, to, TileEntityTank.class);
				if (tank != null)
					tank.part(to.getOpposite());
			}
			if (grid != null)
				grid.removeNode(this);
			grid = null;
		}

		super.invalidate();
	}

	@Override
	public final boolean isNotValid() {

		return isInvalid();
	}

	@Override
	public void firstTick() {

		if (!inWorld) return;
		for (ForgeDirection to : ForgeDirection.VALID_DIRECTIONS) {
			if (to.offsetY != 0 || !BlockPosition.blockExists(this, to))
				continue;
			TileEntityTank tank = BlockPosition.getAdjacentTileEntity(this, to, TileEntityTank.class);
			if (tank != null && tank.grid != null && FluidHelper.isFluidEqualOrNull(tank.grid.getStorage().getFluid(), _tank.getFluid())) {
				if (tank.grid != null)
					if (tank.grid == grid || tank.grid.addNode(this)) {
						tank.join(to.getOpposite());
						join(to);
					}
			}
		}
		if (grid == null)
			grid = new TankNetwork(this);
	}

	@Override
	public void cofh_validate() {

		super.cofh_validate();
		if (worldObj.isRemote)
			return;
		firstTick();
	}

	public void join(ForgeDirection from) {

		sides |= (1 << from.ordinal());
		markChunkDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void part(ForgeDirection from) {

		sides &= ~(1 << from.ordinal());
		markChunkDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public boolean isInterfacing(ForgeDirection to) {

		return 0 != (sides & (1 << to.ordinal()));
	}

	int interfaceCount() {

		return Integer.bitCount(sides);
	}

	@Override
	public Packet getDescriptionPacket() {

		if (grid == null)
			return null;
		NBTTagCompound data = new NBTTagCompound();
		FluidStack fluid = grid.getStorage().drain(1, false);
		if (fluid != null)
			data.setTag("fluid", fluid.writeToNBT(new NBTTagCompound()));
		data.setByte("sides", sides);
		S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, data);
		return packet;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {

		super.onDataPacket(net, pkt);
		NBTTagCompound data = pkt.func_148857_g();
		switch (pkt.func_148853_f()) {
		case 0:
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(data.getCompoundTag("fluid"));
			_tank.setFluid(fluid);
			sides = data.getByte("sides");
			break;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		worldObj.func_147451_t(xCoord, yCoord, zCoord);
	}

	@Override
	public String getDataType() {

		return "tile.mfr.tank.name";
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (_tank.getFluidAmount() != 0)
			tag.setTag("tank", _tank.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		_tank.readFromNBT(tag.getCompoundTag("tank"));
	}

	public FluidStack getFluid() {

		if (grid == null)
			return _tank.getFluid();
		return grid.getStorage().getFluid();
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (grid == null)
			return 0;
		return grid.getStorage().fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (grid == null)
			return null;
		return grid.getStorage().drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (grid == null)
			return worldObj.isRemote ? _tank.drain(maxDrain, false) : null;
		return grid.getStorage().drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return grid != null;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return grid != null;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		if (grid == null)
			return FluidHelper.NULL_TANK_INFO;
		return new FluidTankInfo[] { grid.getStorage().getInfo() };
	}

	@Override
	public boolean allowBucketFill(ItemStack stack) {

		return stack.getItem() != MFRThings.plasticTankItem;
	}

	@Override
	public boolean allowBucketDrain(ItemStack stack) {

		return true;
	}

	@Override
	public void getTileInfo(List<IChatComponent> info, ForgeDirection side, EntityPlayer player, boolean debug) {

		if (debug) {
			info.add(new ChatComponentText("Grid: " + grid));
			if (grid != null)
				info.add(new ChatComponentText(Arrays.toString(grid.getStorage().tanks)));
		}
		if (grid == null) {
			info.add(new ChatComponentText("Null Grid!!"));
			if (debug)
				info.add(new ChatComponentText("FluidForGrid: " +
						StringHelper.getFluidName(_tank.getFluid(), "") + "@" + _tank.getFluidAmount()));
			return;
		}
		if (grid.getStorage().getFluidAmount() == 0)
			info.add(new ChatComponentText(MFRUtil.empty()));
		else
			info.add(new ChatComponentText(MFRUtil.getFluidName(grid.getStorage().getFluid())));
		info.add(new ChatComponentText((grid.getStorage().getFluidAmount() / (float) grid.getStorage().getCapacity() * 100f) + "%"));
		if (debug) {
			info.add(new ChatComponentText("Sides: " + Integer.toBinaryString(sides)));
			info.add(new ChatComponentText(grid.getStorage().getFluidAmount() + " / " + grid.getStorage().getCapacity()));
			info.add(new ChatComponentText("Size: " + grid.getSize() + " | FluidForGrid: " +
					StringHelper.getFluidName(_tank.getFluid(), "") + "@" + _tank.getFluidAmount()));
			info.add(new ChatComponentText("Length: " + grid.getStorage().length + " | Index: " + grid.getStorage().index +
					" | Reserve: " + grid.getStorage().tanks.length));
		}
	}

}
