package com.etprovince.dontconnecttrapdoor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class DontConnectTrapdoor implements ModInitializer {
    public static final String  MOD_ID = "dontconnecttrapdoor";
    public static String VERSION = "unknown";

    @Override
    public void onInitialize() {
        VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
    }
}
