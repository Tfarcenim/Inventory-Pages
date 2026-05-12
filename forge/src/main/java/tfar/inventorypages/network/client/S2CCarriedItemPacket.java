package tfar.inventorypages.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import tfar.inventorypages.InventoryPagesClientForge;


public record S2CCarriedItemPacket(ItemStack carried) implements S2CModPacket {


    public S2CCarriedItemPacket(FriendlyByteBuf buf) {
        this(buf.readItem());
    }

    @Override
    public void handleClient() {
        InventoryPagesClientForge.handle(this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeItem(carried);
    }
}
