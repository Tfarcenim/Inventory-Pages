package tfar.inventorypages.network.server;

import net.minecraft.server.level.ServerPlayer;
import tfar.inventorypages.network.ModPacket;

public interface C2SModPacket extends ModPacket {

    void handleServer(ServerPlayer player);

}
