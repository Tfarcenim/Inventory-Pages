package tfar.inventorypages.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.inventorypages.InventoryPageMenu;

public record C2SChangePagePacket(int index) implements C2SModPacket{

    public C2SChangePagePacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    @Override
    public void handleServer(ServerPlayer player) {
        if (!(player.containerMenu instanceof  InventoryPageMenu inventoryPageMenu)) {
            player.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.inventory");
                }

                @Override
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                    return new InventoryPageMenu(pContainerId, pPlayerInventory, pPlayer, index);
                }
            });
        } else {
            inventoryPageMenu.setPage(index);
        }
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(index);
    }
}
