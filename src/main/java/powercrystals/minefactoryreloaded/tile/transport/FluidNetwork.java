package powercrystals.minefactoryreloaded.tile.transport;

import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.LinkedHashList;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.position.BlockPosition;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.core.ArrayHashList;
import powercrystals.minefactoryreloaded.core.IGrid;
import powercrystals.minefactoryreloaded.net.GridTickHandler;

public class FluidNetwork implements IGrid {

	public static final int TRANSFER_RATE = 80;
	public static final int STORAGE = TRANSFER_RATE * 6;
	static final GridTickHandler<FluidNetwork, TileEntityPlasticPipe> HANDLER =
			GridTickHandler.fluid;

	private ArrayHashList<TileEntityPlasticPipe> nodeSet = new ArrayHashList<TileEntityPlasticPipe>();
	private LinkedHashList<TileEntityPlasticPipe> conduitSet;
	private TileEntityPlasticPipe master;
	private int overflowSelector;
	private boolean regenerating = false;
	FluidTankAdv storage = new FluidTankAdv(320);

	public int distribution;
	public int distributionSide;

	protected FluidNetwork() {
		storage.setCapacity(0);
	}


	public FluidNetwork(TileEntityPlasticPipe base) { this();
		conduitSet = new LinkedHashList<TileEntityPlasticPipe>();
		regenerating = true;
		addConduit(base);
		regenerating = false;
	}

	public int getNodeShare(TileEntityPlasticPipe cond) {
		int size = nodeSet.size();
		if (size == 1)
			return storage.getFluidAmount();
		int amt = 0;
		if (master == cond) amt = storage.getFluidAmount() % size;
		return amt + storage.getFluidAmount() / size;
	}

	public boolean addConduit(TileEntityPlasticPipe cond) {
		if (conduitSet.add(cond))
			if (!conduitAdded(cond))
				return false;
		if (cond.isNode) {
			if (nodeSet.add(cond)) {
				nodeAdded(cond);
			}
		} else if (!nodeSet.isEmpty()) {
			int share = getNodeShare(cond);
			if (nodeSet.remove(cond)) {
				cond.fluidForGrid = storage.drain(share, true);
				nodeRemoved(cond);
			}
		}
		return true;
	}

	public void removeConduit(TileEntityPlasticPipe cond) {
		conduitSet.remove(cond);
		if (!nodeSet.isEmpty()) {
			int share = getNodeShare(cond);
			if (nodeSet.remove(cond)) {
				cond.fluidForGrid = storage.drain(share, true);
				nodeRemoved(cond);
			} else {
				cond.fluidForGrid = storage.drain(0, false);
			}
		} else {
			cond.fluidForGrid = storage.drain(0, false);
		}
	}

	public void regenerate() {
		regenerating = true;
		HANDLER.regenerateGrid(this);
	}

	public boolean isRegenerating() {
		return regenerating;
	}

	@Override
	public void markSweep() {
		destroyGrid();
		if (conduitSet.isEmpty())
			return;
		TileEntityPlasticPipe main = conduitSet.poke();
		LinkedHashList<TileEntityPlasticPipe> oldSet = conduitSet;
		nodeSet.clear();
		conduitSet = new LinkedHashList<TileEntityPlasticPipe>(Math.min(oldSet.size() / 6, 5));
		rebalanceGrid();

		LinkedHashList<TileEntityPlasticPipe> toCheck = new LinkedHashList<TileEntityPlasticPipe>();
		LinkedHashList<TileEntityPlasticPipe> checked = new LinkedHashList<TileEntityPlasticPipe>();
		BlockPosition bp = new BlockPosition(0,0,0);
		ForgeDirection[] dir = ForgeDirection.VALID_DIRECTIONS;
		toCheck.add(main);
		checked.add(main);
		while (!toCheck.isEmpty()) {
			main = toCheck.shift();
			addConduit(main);
			World world = main.getWorldObj();
			for (int i = 6; i --> 0; ) {
				bp.x = main.xCoord; bp.y = main.yCoord; bp.z = main.zCoord;
				bp.step(dir[i]);
				if (world.blockExists(bp.x, bp.y, bp.z)) {
					TileEntity te = bp.getTileEntity(world);
					if (te instanceof TileEntityPlasticPipe) {
						TileEntityPlasticPipe tep = (TileEntityPlasticPipe)te;
						if (main.canInterface(tep, dir[i^1]) && checked.add(tep))
							toCheck.add(tep);
					}
				}
			}
			oldSet.remove(main);
		}
		if (!oldSet.isEmpty()) {
			FluidNetwork newGrid = new FluidNetwork();
			newGrid.conduitSet = oldSet;
			newGrid.regenerating = true;
			newGrid.markSweep();
		}
		if (nodeSet.isEmpty())
			HANDLER.removeGrid(this);
		else
			HANDLER.addGrid(this);
		regenerating = false;
	}

	public void destroyGrid() {
		master = null;
		regenerating = true;
		for (TileEntityPlasticPipe curCond : nodeSet)
			destroyNode(curCond);
		for (TileEntityPlasticPipe curCond : conduitSet)
			destroyConduit(curCond);
		HANDLER.removeGrid(this);
	}

	public void destroyNode(TileEntityPlasticPipe cond) {
		cond.fluidForGrid = storage.drain(getNodeShare(cond), false);
		cond._grid = null;
	}

	public void destroyConduit(TileEntityPlasticPipe cond) {
		cond.fluidForGrid = storage.drain(0, false);
		cond._grid = null;
	}

	@Override
	public void doGridPreUpdate() {
		if (regenerating)
			return;
		if (nodeSet.isEmpty()) {
			HANDLER.removeGrid(this);
			return;
		}
		FluidTankAdv tank = storage;
		if (tank.getSpace() <= 0)
			return;
		ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;

		for (TileEntityPlasticPipe cond : nodeSet)
			for (int i = 6; i --> 0; )
				cond.extract(directions[i], tank);
	}

	@Override
	public void doGridUpdate() {
		if (regenerating)
			return;
		if (nodeSet.isEmpty()) {
			HANDLER.removeGrid(this);
			return;
		}
		FluidTankAdv storage = this.storage;
		if (storage.getFluidAmount() <= 0)
			return;
		ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
		int size = nodeSet.size();
		int toDistribute = storage.getFluidAmount() / size;
		int sideDistribute = toDistribute / 6;
		Fluid fluid = storage.getFluid().getFluid();

		distribution = toDistribute;
		distributionSide = sideDistribute;
		FluidStack stack = storage.drain(sideDistribute, false);

		int overflow = overflowSelector = (overflowSelector + 1) % size;
		TileEntityPlasticPipe master = nodeSet.get(overflow);

		if (sideDistribute > 0) for (TileEntityPlasticPipe cond : nodeSet)
			if (cond != master) {
				int e = 0;
				for (int i = 6; i --> 0; )
					e += cond.transfer(directions[i], stack, fluid);
				if (e > 0) storage.drain(e, true);
			}

		toDistribute += storage.getFluidAmount() % size;
		sideDistribute = toDistribute / 6;
		stack.amount = sideDistribute;

		if (sideDistribute > 0) {
			int e = 0;
			for (int i = 6; i --> 0; )
				e += master.transfer(directions[i], stack, fluid);
			if (e > 0) storage.drain(e, true);
		} else if (toDistribute > 0) {
			stack.amount = toDistribute;
			int e = 0;
			for (int i = 6; i --> 0 && e < toDistribute; ) {
				e += master.transfer(directions[i], stack, fluid);
				stack.amount = toDistribute - e;
			}
			if (e > 0) storage.drain(e, true);
		}
	}

	public boolean canMergeGrid(FluidNetwork grid) {
		if (grid == null) return false;
		return FluidHelper.isFluidEqual(grid.storage.getFluid(), storage.getFluid());
	}

	public void mergeGrid(FluidNetwork grid) {
		if (grid == this) return;
		if (storage.getFluid() == null && grid.storage.getFluid() != null) {
			grid.mergeGrid(this);
			return;
		}
		boolean r = regenerating | grid.regenerating;
		grid.destroyGrid();
		if (!regenerating & r)
			regenerate();

		regenerating = true;
		for (TileEntityPlasticPipe cond : grid.conduitSet)
			addConduit(cond);
		regenerating = r;

		grid.conduitSet.clear();
		grid.nodeSet.clear();
	}

	public void nodeAdded(TileEntityPlasticPipe cond) {
		if (master == null) {
			master = cond;
			HANDLER.addGrid(this);
		}
		storage.fill(cond.fluidForGrid, true);
		cond.fluidForGrid = storage.drain(0, false);
		rebalanceGrid();
	}

	public void nodeRemoved(TileEntityPlasticPipe cond) {
		if (cond == master) {
			if (nodeSet.isEmpty()) {
				master = null;
				HANDLER.removeGrid(this);
			} else {
				master = nodeSet.get(0);
			}
		}
		rebalanceGrid();
	}

	public boolean conduitAdded(TileEntityPlasticPipe cond) {
		if (cond._grid != null) {
			if (cond._grid != this) {
				conduitSet.remove(cond);
				if (canMergeGrid(cond._grid)) {
					mergeGrid(cond._grid);
				} else
					return false;
			} else
				return false;
		} else if (cond.fluidForGrid != null) {
			if (!FluidHelper.isFluidEqualOrNull(cond.fluidForGrid, storage.getFluid())) {
				conduitSet.remove(cond);
				return false;
			} else {
				cond.setGrid(this);
				storage.fill(new FluidStack(cond.fluidForGrid, 0), true);
			}
		} else {
			cond.setGrid(this);
		}
		return true;
	}

	public void rebalanceGrid() {
		storage.setCapacity(getNodeCount() * STORAGE);
	}

	public int getConduitCount() {
		return conduitSet.size();
	}

	public int getNodeCount() {
		return nodeSet.size();
	}

	@Override
	public String toString() {
		return "FluidNetwork@" + Integer.toString(hashCode()) + "; master:" + master +
				"; regenerating:" + regenerating + "; isTicking:" + HANDLER.isGridTicking(this);
	}
}
