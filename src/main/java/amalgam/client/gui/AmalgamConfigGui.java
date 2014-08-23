package amalgam.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import amalgam.common.Amalgam;
import amalgam.common.Config;
import cpw.mods.fml.client.config.GuiConfig;

public class AmalgamConfigGui extends GuiConfig {
    public AmalgamConfigGui(GuiScreen parent) {
        super(parent, new ConfigElement<Object>(Config.configFile.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), Amalgam.MODID,
                false, false, GuiConfig.getAbridgedConfigPath(Config.configFile.toString()));
    }
}
