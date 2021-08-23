package net.doodcraft.dooder07.telepads;

import java.util.HashMap;

public class TelepadNetworks {

    private HashMap<String, TelepadNetwork> networks;

    public TelepadNetworks() {
        this.networks = new HashMap<>();
    }

    public TelepadNetwork addNetwork(TelepadNetwork network) {
        return this.networks.put(network.getNetworkId(), network);
    }

    public TelepadNetwork getNetwork(String id) {
        return this.networks.get(id);
    }

    public Boolean networkExists(String id) {
        return this.networks.containsKey(id);
    }
}
