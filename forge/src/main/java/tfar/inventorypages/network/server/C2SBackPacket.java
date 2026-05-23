package tfar.inventorypages.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.inventorypages.InventoryPageMenuV2;
import tfar.inventorypages.InventoryPagesForge;
import tfar.inventorypages.network.PacketHandler;

public enum C2SBackPacket implements C2SModPacket{
    PICK_BLOCK,INSTANCE2;

    public static C2SBackPacket fromPacket(FriendlyByteBuf buf) {
        return buf.readEnum(C2SBackPacket.class);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        switch (this) {
            case PICK_BLOCK -> {

            }
            case INSTANCE2 -> {
                InventoryPagesForge.moveToPages(player);
            }
        }
    }

    public void send() {
        PacketHandler.sendToServer(this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeEnum(this);
    }
}
