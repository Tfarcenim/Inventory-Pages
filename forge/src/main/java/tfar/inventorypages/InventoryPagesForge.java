package tfar.inventorypages;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.registries.RegisterEvent;
import tfar.inventorypages.client.InventoryPagesClientForge;
import tfar.inventorypages.datagen.ModDatagen;
import tfar.inventorypages.network.PacketHandler;

@Mod(InventoryPages.MOD_ID)
public class InventoryPagesForge {
    
    public InventoryPagesForge() {
        IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(this::reload);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerClone);
        MinecraftForge.EVENT_BUS.addListener(this::tick);
        MinecraftForge.EVENT_BUS.addListener(ModDatagen::gather);
        eventbus.addListener(this::register);
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DropOffConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, DropOffConfig.SERVER_SPEC);
        eventbus.addListener(DropOffConfig::onConfigChanged);

        if (FMLEnvironment.dist.isClient()) {
            InventoryPagesClientForge.init(eventbus);
        }

        PacketHandler.registerPackets();

        // Use Forge to bootstrap the Common mod.
        InventoryPages.init();
    }

    void tick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase == TickEvent.Phase.START) {
            InventoryPages.tick(player);
        }
    }

    void onPlayerClone(PlayerEvent.Clone clone) {
        Player original = clone.getOriginal();
        Player newPlayer = clone.getEntity();
        boolean alive = !clone.isWasDeath();
        boolean shouldKeepItems = alive || InventoryPages.KEEP_INV_PAGES;

        if (shouldKeepItems) {
            ((PlayerDuck)newPlayer).inventoryPageList().replaceWith(((PlayerDuck)original).inventoryPageList());
        }
    }

    private void reload(AddReloadListenerEvent event) {
        event.addListener(new InventoryPageConfigReloadListener());
    }

    void register(RegisterEvent event) {
        event.register(Registry.MENU_REGISTRY,InventoryPages.id("menu_v2"),() -> RegistryObjects.INVENTORY_PAGE_MENU_V2);
    }

    public static CombinedInvWrapper makeWrapper(InventoryPageList list) {
        IItemHandlerInventoryPage[] pages = list.stream().map(IItemHandlerInventoryPage::new).toArray(IItemHandlerInventoryPage[]::new);
        return new CombinedInvWrapper(pages);
    }

    public static int moveToPages(Player player) {
        IItemHandlerModifiable playerstacks = new PlayerMainInvWrapper(player.getInventory());

        IItemHandlerModifiable target = makeWrapper(((PlayerDuck)player).inventoryPageList());

        int itemsCounter = 0;
        for (int i = 0; i < playerstacks.getSlots(); ++i) {
            if (i < 9)
                continue;
            ItemStack playerstack = playerstacks.getStackInSlot(i);

            if (playerstack.isEmpty())
                continue;
            itemsCounter += playerstack.getCount();
            ItemStack rem = playerstacks.extractItem(i, Integer.MAX_VALUE, false);
            for (int j = 0; j < target.getSlots(); ++j) {
                rem = target.insertItem(j, rem, false);
                if (rem.isEmpty())
                    break;
            }
            if (!rem.isEmpty()) {
                itemsCounter -= rem.getCount();
                playerstacks.insertItem(i, rem, false);
            }
        }
        return itemsCounter;
    }
}