package net.doodcraft.dooder07.telepads;

public class TelepadNetwork {

    private String netId;
    private Boolean enabled;
    private Boolean lightningEnabled;
    private Boolean createLoggingEnabled;
    private Boolean destroyLoggingEnabled;
    private Boolean playerUseLoggingEnabled;

    public TelepadNetwork(String id) {
        this.netId = id;
        this.enabled = true; // on by default
        this.lightningEnabled = StaticConfig.lightningEnabled;
        this.createLoggingEnabled = StaticConfig.createLoggingEnabled;
        this.destroyLoggingEnabled = StaticConfig.destroyLoggingEnabled;
        this.playerUseLoggingEnabled = StaticConfig.logPlayerUse;
    }

    public String getNetworkId() {
        return this.netId;
    }

    public void toggleNetwork() {
        this.enabled = !enabled;
    }

    public Boolean isEnabled() {
        return this.enabled;
    }

    public void toggleLightning() {
        this.lightningEnabled = !lightningEnabled;
    }

    public Boolean lightningEnabled() {
        return this.lightningEnabled;
    }
}
