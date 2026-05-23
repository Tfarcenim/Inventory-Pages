package tfar.inventorypages.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import tfar.inventorypages.InventoryPagesClient;


public record S2CPopupPacket(int page, ItemStack pickedUp) implements S2CModPacket {


    public S2CPopupPacket(FriendlyByteBuf buf) {
        this(buf.readInt(),buf.readItem());
    }

    @Override
    public void handleClient() {
        InventoryPagesClient.handle(this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeInt(page);
        to.writeItem(pickedUp);
    }
}
