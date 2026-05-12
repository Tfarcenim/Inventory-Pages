package tfar.inventorypages.network.client;


import tfar.inventorypages.network.ModPacket;

public interface S2CModPacket extends ModPacket {

    void handleClient();

}
