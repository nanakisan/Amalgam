package amalgam.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import amalgam.common.Amalgam;
import amalgam.common.Config;
import amalgam.common.properties.AmalgamPropertyManager;
import cpw.mods.fml.client.config.ConfigGuiType;
import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.BooleanEntry;
import cpw.mods.fml.client.config.GuiConfigEntries.ButtonEntry;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.GuiConfigEntries.IConfigEntry;
import cpw.mods.fml.client.config.GuiConfigEntries.SelectValueEntry;
import cpw.mods.fml.client.config.GuiConfigEntries.StringEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class AmalgamConfigGui extends GuiConfig {
    public AmalgamConfigGui(GuiScreen parent) {
        super(parent, getConfigElements(), Amalgam.MODID, false, false, GuiConfig.getAbridgedConfigPath(Config.configFile.toString()));
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.add(new DummyCategoryElement("amalgamGeneralConfig", "configgui.generalConfig", GeneralEntry.class));
        list.add(new DummyCategoryElement("amalgamOreConifg", "configgui.oreConfig", OreDefinitionEntry.class));
        list.add(new DummyCategoryElement("amalgamItemPropertyConfig", "configgui.itemConfig", OreDefinitionEntry.class));
        return list;
    }

    /**
     * This custom list entry provides the General Settings entry on the Amalgam Configuration screen. It extends the
     * base Category entry class and defines the IConfigElement objects that will be used to build the child screen.
     */
    public static class GeneralEntry extends CategoryEntry {
        public GeneralEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            // This GuiConfig object specifies the configID of the object and as such will force-save when it is closed.
            // The parent GuiConfig object's entryList will also be refreshed to reflect the changes.
            return new GuiConfig(this.owningScreen,
                    (new ConfigElement<Object>(Config.configFile.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements()),
                    this.owningScreen.modID, Configuration.CATEGORY_GENERAL, this.configElement.requiresWorldRestart()
                            || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart()
                            || this.owningScreen.allRequireMcRestart, GuiConfig.getAbridgedConfigPath(Config.configFile.toString()));
        }
    }

    /**
     * This custom list entry provides the Mod Overrides entry on the Forge Chunk Loading config screen. It extends the
     * base Category entry class and defines the IConfigElement objects that will be used to build the child screen. In
     * this case it adds the custom entry for adding a new mod override and lists the existing mod overrides.
     */
    public static class OreDefinitionEntry extends CategoryEntry {
        public OreDefinitionEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);

        }

        /**
         * This method is called in the constructor and is used to set the childScreen field.
         */
        @Override
        protected GuiScreen buildChildScreen() {
            List<IConfigElement> list = new ArrayList<IConfigElement>();

            // TODO add a way to delete entries that have been added

            list.add(new DummyCategoryElement("addForgeChunkLoadingModCfg", "forge.configgui.ctgy.forgeChunkLoadingAddModConfig", AddOreEntry.class));
            for (ConfigCategory ore : AmalgamPropertyManager.getOreCategories())
                list.add(new ConfigElement(ore));

            return new GuiConfig(this.owningScreen, list, this.owningScreen.modID, "ores", this.configElement.requiresWorldRestart()
                    || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                    this.owningScreen.title, I18n.format("forge.configgui.ctgy.forgeChunkLoadingModConfig"));
        }

        /**
         * By overriding the enabled() method and checking the value of the "enabled" entry this entry is
         * enabled/disabled based on the value of the other entry.
         */
        @Override
        public boolean enabled() {
            for (IConfigEntry entry : this.owningEntryList.listEntries) {
                if (entry.getName().equals("enabled") && entry instanceof BooleanEntry) {
                    return Boolean.valueOf(entry.getCurrentValue().toString());
                }
            }

            return true;
        }

        /**
         * Check to see if the child screen's entry list has changed.
         */
        @Override
        public boolean isChanged() {
            if (childScreen instanceof GuiConfig) {
                GuiConfig child = (GuiConfig) childScreen;
                return child.entryList.listEntries.size() != child.initEntries.size() || child.entryList.hasChangedEntry(true);
            }
            return false;
        }

        /**
         * Since adding a new entry to the child screen is what constitutes a change here, reset the child screen
         * listEntries to the saved list.
         */
        @Override
        public void undoChanges() {
            if (childScreen instanceof GuiConfig) {
                GuiConfig child = (GuiConfig) childScreen;
                for (IConfigEntry ice : child.entryList.listEntries)
                    if (!child.initEntries.contains(ice) && ForgeChunkManager.getConfig().hasCategory(ice.getName()))
                        ForgeChunkManager.getConfig().removeCategory(ForgeChunkManager.getConfig().getCategory(ice.getName()));

                child.entryList.listEntries = new ArrayList<IConfigEntry>(child.initEntries);
            }
        }
    }

    /**
     * This custom list entry provides a button that will open to a screen that will allow a user to define a new mod
     * override.
     */
    public static class AddOreEntry extends CategoryEntry {
        public AddOreEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            List<IConfigElement> list = new ArrayList<IConfigElement>();

            list.add(new DummyConfigElement("oreName", "", ConfigGuiType.STRING, "configgui.oreName").setCustomListEntryClass(OreEntry.class));

            list.add(new ConfigElement<String>(new Property("typeNameVolume", new String[] { "type:name:volume" }, Property.Type.STRING,
                    "configgui.dictAndVolume")));
            list.add(new ConfigElement<Integer>(new Property("malleability", "0", Property.Type.DOUBLE, "name.malleability")));
            list.add(new ConfigElement<Integer>(new Property("density", "0", Property.Type.DOUBLE, "name.density")));
            list.add(new ConfigElement<Integer>(new Property("hardness", "0", Property.Type.DOUBLE, "name.hardness")));
            list.add(new ConfigElement<Integer>(new Property("luster", "0", Property.Type.DOUBLE, "name.luster")));
            list.add(new ConfigElement<Integer>(new Property("color", "0x999999", Property.Type.STRING, "name.color")));
            list.add(new DummyCategoryElement("remove", "forge.configgui.ctgy.forgeChunkLoadingAddModConfig", RemoveOreEntry.class));

            return new GuiConfig(this.owningScreen, list, this.owningScreen.modID, "ores", this.configElement.requiresWorldRestart()
                    || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                    this.owningScreen.title, I18n.format("forge.configgui.ctgy.forgeChunkLoadingAddModConfig"));
        }

        @Override
        public boolean isChanged() {
            return false;
        }
    }

    public static class RemoveOreEntry extends ButtonEntry {

        public RemoveOreEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement<?> configElement, GuiButtonExt button) {
            super(owningScreen, owningEntryList, configElement, button);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void updateValueButtonText() {
            // TODO Auto-generated method stub

        }

        @Override
        public void valueButtonPressed(int slotIndex) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean isDefault() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void setToDefault() {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean isChanged() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void undoChanges() {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean saveConfigElement() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Object getCurrentValue() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object[] getCurrentValues() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    /**
     * This custom list entry provides a Mod ID selector. The control is a button that opens a list of values to select
     * from. This entry also overrides onGuiClosed() to run code to save the data to a new ConfigCategory when the user
     * is done.
     */
    public static class OreEntry extends StringEntry {
        public OreEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        /**
         * By overriding onGuiClosed() for this entry we can perform additional actions when the user is done such as
         * saving a new ConfigCategory object to the Configuration object.
         */
        @Override
        public void onGuiClosed() {
            String oreName = (String) this.getCurrentValue();
            String[] dictAndVolumeString = { "[type:name:volume]" };
            double density = 0, luster = 0, malleability = 0, hardness = 0;
            String color = "0x999999";
            if (this.getCurrentValue() != null) {
                this.owningEntryList.saveConfigElements();
                for (IConfigElement ice : this.owningScreen.configElements) {
                    if ("density".equals(ice.getName()))
                        density = Double.valueOf(ice.get().toString());
                    else if ("malleability".equals(ice.getName()))
                        malleability = Double.valueOf(ice.get().toString());
                    else if ("hardness".equals(ice.getName()))
                        hardness = Double.valueOf(ice.get().toString());
                    else if ("luster".equals(ice.getName()))
                        luster = Double.valueOf(ice.get().toString());
                    else if ("color".equals(ice.getName()))
                        color = ice.get().toString();
                    else if ("typeNameVolume".equals(ice.getName())) {
                        dictAndVolumeString = (String[]) ice.getList();
                    }
                }

                Config.LOG.info(this.getCurrentValue() + " " + density + " " + malleability + " " + luster + " " + hardness);

                AmalgamPropertyManager.addConfigProperty(oreName, "density", String.valueOf(density), Property.Type.DOUBLE);
                AmalgamPropertyManager.addConfigProperty(oreName, "malleability", String.valueOf(malleability), Property.Type.DOUBLE);
                AmalgamPropertyManager.addConfigProperty(oreName, "luster", String.valueOf(luster), Property.Type.DOUBLE);
                AmalgamPropertyManager.addConfigProperty(oreName, "hardness", String.valueOf(hardness), Property.Type.DOUBLE);
                AmalgamPropertyManager.addConfigProperty(oreName, "color", color, Property.Type.STRING);
                AmalgamPropertyManager.addConfigProperty(oreName, "typeNameVolume", dictAndVolumeString, Property.Type.STRING);

                if (this.owningScreen.parentScreen instanceof GuiConfig) {
                    GuiConfig superParent = (GuiConfig) this.owningScreen.parentScreen;
                    ConfigCategory modCtgy = AmalgamPropertyManager.getConfigFor(oreName);
                    ConfigElement modConfig = new ConfigElement(modCtgy);

                    boolean found = false;
                    for (IConfigElement ice : superParent.configElements)
                        if (ice.getName().equals(oreName))
                            found = true;

                    if (!found)
                        superParent.configElements.add(modConfig);

                    superParent.needsRefresh = true;
                    superParent.initGui();
                }
            }
        }
    }
}
