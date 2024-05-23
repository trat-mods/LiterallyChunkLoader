package net.literally.chunk.loader.utils;

import net.literally.chunk.loader.loaders.LCLLoader;

public final class ModLogger {
    public final static ModLogger DEFAULT_CHANNEL = new ModLogger(LCLLoader.MOD_ID);
    private final String domain;

    public ModLogger(String domain) {
        this.domain = domain;
    }

    public void logError(String message) {
        System.out.println("[" + domain + "]:(ERROR) => " + message);
    }

    public void logInfo(String message) {
        System.out.println("[" + domain + "]:(INFO) => " + message);
    }

    public void logWarning(String message) {
        System.out.println("[" + domain + "]:(WARNING) => " + message);
    }
}
