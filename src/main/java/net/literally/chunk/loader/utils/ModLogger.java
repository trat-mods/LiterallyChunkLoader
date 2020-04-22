package net.literally.chunk.loader.utils;

public final class ModLogger
{
    private String domain;
    
    public ModLogger(String domain)
    {
        this.domain = domain;
    }
    
    public void logError(String message)
    {
        System.out.println("[" + domain + "]:(ERROR) => " + message);
    }
    
    public void logInfo(String message)
    {
        System.out.println("[" + domain + "]:(INFO) => " + message);
    }
    
    public void logWarning(String message)
    {
        System.out.println("[" + domain + "]:(WARNING) => " + message);
    }
}
