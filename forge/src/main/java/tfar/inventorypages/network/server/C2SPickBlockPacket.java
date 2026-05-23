package tfar.inventorypages.network.server;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import tfar.inventorypages.InventoryPageList;
import tfar.inventorypages.PlayerDuck;

public record C2SPickBlockPacket(ItemStack picked) implements C2SModPacket {

    public C2SPickBlockPacket(FriendlyByteBuf buf) {
        this(buf.readItem());
    }

    @Override
    public void handleServer(ServerPlayer player) {
        if (player != null) {
            InventoryPageList inventoryPageList = ((PlayerDuck) player).inventoryPageList();
            Pair<Integer, Integer> pageSlot = inventoryPageList.findMatchingItem(picked);
            if (pageSlot != null) {
                inventoryPageList.pickSlotAndPage(pageSlot);
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeItem(picked);
    }
}
