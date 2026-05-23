package tfar.inventorypages.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import tfar.inventorypages.InventoryPageMenuV2;
import tfar.inventorypages.network.PacketHandler;
import tfar.inventorypages.network.client.S2CCarriedItemPacket;

public record C2SChangePagePacket(int index) implements C2SModPacket{

    public C2SChangePagePacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    @Override
    public void handleServer(ServerPlayer player) {
        if (!(player.containerMenu instanceof  InventoryPageMenuV2 inventoryPageMenu)) {
            ItemStack carried = player.inventoryMenu.getCarried();
            player.inventoryMenu.setCarried(ItemStack.EMPTY);
            NetworkHooks.openScreen(player,new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.empty();
                }

                @Override
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                    return new InventoryPageMenuV2(pContainerId, pPlayerInventory, pPlayer, index);
                }
            },buf -> buf.writeInt(index));
            player.containerMenu.setCarried(carried);
            PacketHandler.sendToClient(new S2CCarriedItemPacket(carried),player);
        } else {
            inventoryPageMenu.setPage(index);
        }
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(index);
    }
}
