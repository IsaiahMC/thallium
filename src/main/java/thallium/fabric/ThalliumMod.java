package thallium.fabric;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import thallium.fabric.gui.ThalliumOptions;

public class ThalliumMod implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger("Thallium");
    public static File saveFile;

    public static int RENDER_DISTANCE;

	@SuppressWarnings("deprecation")
    @Override
	public void onInitialize() {
        ThalliumOptions.init();

        saveFile = new File(FabricLoader.getInstance().getConfigDirectory(), "thallium-options.dat");
        if (saveFile.exists())
            ThalliumOptions.load();
        ThalliumOptions.save();
    }

}