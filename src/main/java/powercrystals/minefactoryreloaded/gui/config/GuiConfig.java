package powercrystals.minefactoryreloaded.gui.config;

import cofh.CoFHCore;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.MinecraftServer;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GuiConfig extends cpw.mods.fml.client.config.GuiConfig {

	public GuiConfig(GuiScreen parentScreen) {

		super(parentScreen, getConfigElements(parentScreen), CoFHCore.modId, false, false, CoFHCore.modName);
	}

	private static List<IConfigElement> getConfigElements(GuiScreen parent) {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		{
			MinecraftServer server = MinecraftServer.getServer();
			if (server == null || !server.isServerRunning()) {
				list.add(new DummyCategoryElement("Child Mods", "config.childMods", getClientConfigElements()));
			}
		}

		return list;
	}

	private static List<IConfigElement> getClientConfigElements() {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		//ModContainer container = Loader.instance().getIndexedModList().get(MineFactoryReloadedCore.modId);
		//container.get
		//for (int i = 0; i < CATEGORIES_CLIENT.length; i++) {
		//	list.add(new DummyConfigElement<ConfigCategory>();
		//}
		return list;
	}

}
