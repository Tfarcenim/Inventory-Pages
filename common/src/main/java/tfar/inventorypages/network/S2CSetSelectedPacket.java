package tfar.inventorypages.network;

import net.minecraft.network.FriendlyByteBuf;
import tfar.inventorypages.InventoryPagesClient;
import tfar.inventorypages.network.client.S2CModPacket;


public record S2CSetSelectedPacket(int selected) implements S2CModPacket {


    public S2CSetSelectedPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    @Override
    public void handleClient() {
        InventoryPagesClient.handleSelected(this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(selected);
    }
}
