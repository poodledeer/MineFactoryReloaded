package powercrystals.minefactoryreloaded.gui.client;

import cofh.core.CoFHProps;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.gui.GuiBase;
import cofh.lib.util.RegistryUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.gui.slot.SlotFake;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class GuiFactoryInventory extends GuiBase {

	protected static DecimalFormat decimal_format = new DecimalFormat();
	{
		decimal_format.setMaximumFractionDigits(1);
		decimal_format.setMinimumFractionDigits(1);
	}
	protected TileEntityFactoryInventory _tileEntity;
	protected ContainerFactoryInventory _container;
	protected int _barSizeMax = 60;

	protected int _tankSizeMax = 60;
	protected int _tanksOffsetX = 122;
	protected int _tanksOffsetY = 15;
	protected int _xOffset = 8;

	protected boolean _renderTanks = true;

	public GuiFactoryInventory(ContainerFactoryInventory container, TileEntityFactoryInventory tileEntity) {

		super(container, new ResourceLocation(MineFactoryReloadedCore.guiFolder + tileEntity.getGuiBackground() + ".png"));
		_container = container;
		drawInventory = drawTitle = false;
		_tileEntity = tileEntity;
		if (CoFHProps.enableColorBlindTextures) {
			ResourceLocation t = new ResourceLocation(MineFactoryReloadedCore.guiFolder + _tileEntity.getGuiBackground() + "_cb.png");
			if (RegistryUtils.textureExists(t))
				texture = t;
		}
	}

	protected boolean isPointInRegion(int x, int y, int w, int h, int a, int b) {

		return func_146978_c(x, y, w, h, a, b);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {

		super.mouseClicked(x, y, button);

		x -= guiLeft;
		y -= guiTop;

		for (Object o : inventorySlots.inventorySlots) {
			if (!(o instanceof SlotFake)) {
				continue;
			}
			SlotFake s = (SlotFake) o;
			if (x >= s.xDisplayPosition && x <= s.xDisplayPosition + 16 && y >= s.yDisplayPosition &&
					y <= s.yDisplayPosition + 16) {
				Packets.sendToServer(Packets.FakeSlotChange, _tileEntity,
					Minecraft.getMinecraft().thePlayer.getEntityId(),
					s.slotNumber, (byte) button);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		fontRendererObj.drawString(_tileEntity.getInventoryName(), _xOffset, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), _xOffset, ySize - 96 + 3, 4210752);

		if (_renderTanks) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			FluidTankInfo[] tanks = _tileEntity.getTankInfo(ForgeDirection.UNKNOWN);
			int n = tanks.length > 3 ? 3 : tanks.length;
			if (n > 0) {
				for (int i = 0; i < n; ++i) {
					if (tanks[i].fluid == null) continue;
					int tankSize = tanks[i].fluid.amount * _tankSizeMax / tanks[i].capacity;
					drawTank(_tanksOffsetX - (i * 20), _tanksOffsetY + _tankSizeMax, tanks[i].fluid, tankSize);
				}
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float gameTicks) {

		super.drawScreen(mouseX, mouseY, gameTicks);

		drawTooltips(mouseX, mouseY);
	}

	protected void drawTooltips(int mouseX, int mouseY) {

		FluidTankAdv[] tanks = _tileEntity.getTanks();
		int n = tanks.length > 3 ? 3 : tanks.length;
		tanks: if (n > 0 && isPointInRegion(_tanksOffsetX - ((n - 1) * 20) + 1, _tanksOffsetY + 1,
			n * 20 - n - 1, _tankSizeMax - 2, mouseX, mouseY)) {
			int tankX = mouseX - this.guiLeft - _tanksOffsetX + (n - 1) * 20;
			if (tankX % 20 >= 16)
				break tanks;
			tankX /= 20;
			tankX = n - tankX - 1;
			drawTankTooltip(tanks[tankX], mouseX, mouseY);
		}
	}

	protected final void drawBar(int xOffset, int yOffset, int max, int current, int tOffset) {

		int size = max > 0 ? (int) (current * (long) _barSizeMax / max) : 0;
		if (size > _barSizeMax) size = max;
		if (size < 0) size = 0;
		bindTexture(texture);
		drawTexturedModalRect(xOffset, yOffset - size,
			xSize + tOffset * 8 + tOffset, 60 + _barSizeMax - size,
			8, size);
	}

	protected void drawTank(int xOffset, int yOffset, FluidStack stack, int level) {

		if (stack == null) return;
		Fluid fluid = stack.getFluid();
		if (fluid == null) return;

		IIcon icon = fluid.getIcon(stack);
		if (icon == null)
			icon = Blocks.flowing_lava.getIcon(0, 0);

		int vertOffset = 0;

		bindTexture(fluid);

		while (level > 0) {
			int texHeight = 0;

			if (level > 16) {
				texHeight = 16;
				level -= 16;
			} else {
				texHeight = level;
				level = 0;
			}

			drawTexturedModelRectFromIcon(xOffset, yOffset - texHeight - vertOffset, icon, 16, texHeight);
			vertOffset = vertOffset + 16;
		}

		bindTexture(texture);
		this.drawTexturedModalRect(xOffset, yOffset - 60, 176, 0, 16, 60);
	}

	protected void bindTexture(Fluid fluid) {

		if (fluid.getSpriteNumber() == 0)
			this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		else
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, fluid.getSpriteNumber());
	}

	protected void drawTankTooltip(FluidTankAdv tank, int x, int y) {

		FluidStack fluid = tank.getFluid();
		if (fluid != null)
			drawBarTooltip(MFRUtil.getFluidName(fluid), "mB", fluid.amount, tank.getCapacity(), x, y);
		else
			drawBarTooltip(MFRUtil.empty(), "mB", 0, tank.getCapacity(), x, y);
	}

	protected void drawBarTooltip(String name, String unit, int value, int max, int x, int y) {

		drawBarTooltip(name, unit, value, max, x, y, (String[]) null);
	}

	protected void drawBarTooltip(String name, String unit, int value, int max, int x, int y, String... extra) {

		List<String> lines = new ArrayList<String>(2);
		lines.add(name);
		String m = String.valueOf(max);
		String v = String.valueOf(value);
		while (v.length() < m.length())
			v = " " + v;
		lines.add(v + " / " + m + " " + unit);
		if (extra != null) {
			for (String l : extra) {
				lines.add(l);
			}
		}
		drawTooltip(lines, x, y);
	}

	protected void drawBarTooltip(String name, String unit, float value, float max, int x, int y) {

		List<String> lines = new ArrayList<String>(2);
		lines.add(name);
		String m = decimal_format.format(max);
		String v = decimal_format.format(value);
		while (v.length() < m.length())
			v = " " + v;
		lines.add(v + " / " + m + " " + unit);
		drawTooltip(lines, x, y);
	}

	protected void drawTooltip(List<String> lines, int x, int y) {

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);

		int tooltipWidth = 0;
		int tempWidth;
		int xStart;
		int yStart;

		for (int i = 0; i < lines.size(); i++) {
			tempWidth = this.fontRendererObj.getStringWidth(lines.get(i));

			if (tempWidth > tooltipWidth) {
				tooltipWidth = tempWidth;
			}
		}

		xStart = x + 12;
		yStart = y - 12;
		int tooltipHeight = 8;

		if (lines.size() > 1) {
			tooltipHeight += 2 + (lines.size() - 1) * 10;
		}

		if (xStart + tooltipWidth + 6 > width) {
			xStart -= 28 + tooltipWidth;
			xStart &= ~xStart >> 31;
		}

		if (guiTop + yStart + tooltipHeight + 6 > height) {
			yStart = height - tooltipHeight - guiTop - 6;
		}

		this.zLevel = 300.0F;
		itemRender.zLevel = 300.0F;
		int color1 = -267386864;
		drawGradientRect(xStart - 3, yStart - 4, xStart + tooltipWidth + 3, yStart - 3, color1, color1);
		drawGradientRect(xStart - 3, yStart + tooltipHeight + 3, xStart + tooltipWidth + 3, yStart + tooltipHeight + 4,
			color1, color1);
		drawGradientRect(xStart - 3, yStart - 3, xStart + tooltipWidth + 3, yStart + tooltipHeight + 3, color1, color1);
		drawGradientRect(xStart - 4, yStart - 3, xStart - 3, yStart + tooltipHeight + 3, color1, color1);
		drawGradientRect(xStart + tooltipWidth + 3, yStart - 3, xStart + tooltipWidth + 4, yStart + tooltipHeight + 3,
			color1, color1);
		int color2 = 1347420415;
		int color3 = (color2 & 16711422) >> 1 | color2 & -16777216;
		drawGradientRect(xStart - 3, yStart - 3 + 1, xStart - 3 + 1, yStart + tooltipHeight + 3 - 1, color2, color3);
		drawGradientRect(xStart + tooltipWidth + 2, yStart - 3 + 1, xStart + tooltipWidth + 3, yStart + tooltipHeight + 3 - 1, color2, color3);
		drawGradientRect(xStart - 3, yStart - 3, xStart + tooltipWidth + 3, yStart - 3 + 1, color2, color2);
		drawGradientRect(xStart - 3, yStart + tooltipHeight + 2, xStart + tooltipWidth + 3, yStart + tooltipHeight + 3, color3, color3);

		for (int stringIndex = 0; stringIndex < lines.size(); ++stringIndex) {
			String line = lines.get(stringIndex);

			if (stringIndex == 0) {
				line = "\u00a7F" + line;
			} else {
				line = "\u00a77" + line;
			}

			this.fontRendererObj.drawStringWithShadow(line, xStart, yStart, -1);

			if (stringIndex == 0) {
				yStart += 2;
			}

			yStart += 10;
		}

		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
	}

	@Override
	public void drawTexturedModelRectFromIcon(int x, int y, IIcon icon, int w, int h) {

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, this.zLevel, icon.getMinU(), icon.getInterpolatedV(h));
		tessellator.addVertexWithUV(x + w, y + h, this.zLevel, icon.getInterpolatedU(w), icon.getInterpolatedV(h));
		tessellator.addVertexWithUV(x + w, y + 0, this.zLevel, icon.getInterpolatedU(w), icon.getMinV());
		tessellator.addVertexWithUV(x + 0, y + 0, this.zLevel, icon.getMinU(), icon.getMinV());
		tessellator.draw();
	}
}
