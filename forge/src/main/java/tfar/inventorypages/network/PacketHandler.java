package tfar.inventorypages.network;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tfar.inventorypages.InventoryPages;
import tfar.inventorypages.network.client.S2CCarriedItemPacket;
import tfar.inventorypages.network.client.S2CModPacket;
import tfar.inventorypages.network.client.S2CToastPacket;
import tfar.inventorypages.network.server.C2SBackPacket;
import tfar.inventorypages.network.server.C2SChangePagePacket;
import tfar.inventorypages.network.server.C2SModPacket;


import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {

    public static void registerPackets() {
        registerClientPackets();
        registerServerPacket(C2SChangePagePacket.class, C2SChangePagePacket::new);
        registerServerPacket(C2SPacketRequestDropoff.class, C2SPacketRequestDropoff::new);
        registerServerPacket(C2SBackPacket.class, C2SBackPacket::fromPacket);
    }

    public static void registerClientPackets() {
        registerClientPacket(S2CCarriedItemPacket.class, S2CCarriedItemPacket::new);
        registerClientPacket(S2CToastPacket.class, S2CToastPacket::new);
        registerClientPacket(S2CReportPacket.class, S2CReportPacket::new);
    }

    static int i;

    static  <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandler.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, wrapS2C());
    }

    static  <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandler.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, wrapC2S());
    }

    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(InventoryPages.id("packets"), () -> "1.0", s -> true, s -> true);


    public static <MSG extends S2CModPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> wrapS2C() {
        return ((msg, contextSupplier) -> {
            contextSupplier.get().enqueueWork(msg::handleClient);
            contextSupplier.get().setPacketHandled(true);
        });
    }

    public static <MSG extends C2SModPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> wrapC2S() {
        return ((msg, contextSupplier) -> {
            ServerPlayer player = contextSupplier.get().getSender();
            contextSupplier.get().enqueueWork(() -> msg.handleServer(player));
            contextSupplier.get().setPacketHandled(true);
        });
    }

    public static <MSG> void sendToClient(MSG packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <MSG> void sendToServer(MSG packet) {
        INSTANCE.sendToServer(packet);
    }


}
