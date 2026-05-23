package tfar.inventorypages.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import tfar.inventorypages.network.client.S2CCarriedItemPacket;
import tfar.inventorypages.platform.Services;

public record C2SCarriedItemPacket(ItemStack carried) implements C2SModPacket {

    public C2SCarriedItemPacket(FriendlyByteBuf buf) {
        this(buf.readItem());
    }

    @Override
    public void handleServer(ServerPlayer player) {
        if (player != null) {
            ItemStack stack = player.isCreative() ? carried : player.containerMenu.getCarried();
            player.containerMenu.setCarried(ItemStack.EMPTY);
            player.doCloseContainer();
            if (!stack.isEmpty()) {//the creative player already knows what they have
                if (!player.isCreative()) {
                    player.containerMenu.setCarried(stack);
                    Services.PLATFORM.sendToClient(new S2CCarriedItemPacket(stack), player);
                } else {
                    Services.PLATFORM.sendToClient(new S2CCarriedItemPacket(stack), player);
                }
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeItem(carried);
    }
}
