package tfar.inventorypages.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import tfar.inventorypages.InventoryPageMenu;

public enum C2SBackPacket implements C2SModPacket{
    INSTANCE;

    public static C2SBackPacket fromPacket(FriendlyByteBuf buf) {
        return INSTANCE;
    }

    @Override
    public void handleServer(ServerPlayer player) {
        if ((player.containerMenu instanceof  InventoryPageMenu inventoryPageMenu)) {
            player.containerMenu = player.inventoryMenu;
        }
    }

    @Override
    public void write(FriendlyByteBuf to) {
    }
}
