package tfar.inventorypages.network.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;


public record S2CConfigPacket(
        Map<Item, Map<Attribute, Map<EquipmentSlot, AttributeModifier>>> map) implements S2CModPacket {


    public S2CConfigPacket(FriendlyByteBuf buf) {
        this(new HashMap<>());
    }

    @Override
    public void handleClient() {
    }

    @Override
    public void write(FriendlyByteBuf to) {
    }
}
