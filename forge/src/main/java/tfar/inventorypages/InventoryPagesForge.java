package tfar.inventorypages;

import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import tfar.inventorypages.network.PacketHandler;

@Mod(InventoryPages.MOD_ID)
public class InventoryPagesForge {
    
    public InventoryPagesForge() {
        IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(this::reload);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerClone);
        eventbus.addListener(this::register);
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        if (FMLEnvironment.dist.isClient()) {
            InventoryPagesClient.init(eventbus);
        }

        PacketHandler.registerPackets();

        // Use Forge to bootstrap the Common mod.
        InventoryPages.init();
    }
    void onPlayerClone(PlayerEvent.Clone clone) {
        Player original = clone.getOriginal();
        Player newPlayer = clone.getEntity();
        boolean alive = !clone.isWasDeath();
        boolean shouldKeepItems = alive || newPlayer.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);

        if (shouldKeepItems) {
            ((PlayerDuck)newPlayer).inventoryPageList().replaceWith(((PlayerDuck)original).inventoryPageList());
        }
    }

    private void reload(AddReloadListenerEvent event) {
        event.addListener(new InventoryPageConfigReloadListener());
    }

    void register(RegisterEvent event) {
        event.register(Registry.MENU_REGISTRY,InventoryPages.id("menu"),() -> RegistryObjects.INVENTORY_PAGE_MENU);
    }
}