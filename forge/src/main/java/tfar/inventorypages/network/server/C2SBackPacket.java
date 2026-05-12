package tfar.inventorypages.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.inventorypages.InventoryPageMenuV2;

public enum C2SBackPacket implements C2SModPacket{
    INSTANCE;

    public static C2SBackPacket fromPacket(FriendlyByteBuf buf) {
        return INSTANCE;
    }

    @Override
    public void handleServer(ServerPlayer player) {
        if (player.containerMenu instanceof InventoryPageMenuV2) {
            player.containerMenu = player.inventoryMenu;
        }
    }

    @Override
    public void write(FriendlyByteBuf to) {
    }
}
