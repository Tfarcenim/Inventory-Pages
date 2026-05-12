package tfar.inventorypages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.inventorypages.network.PacketHandler;
import tfar.inventorypages.network.client.S2CCarriedItemPacket;
import tfar.inventorypages.network.server.C2SChangePagePacket;

public class InventoryPagesClient {

    public static void init(IEventBus bus) {
        bus.addListener(InventoryPagesClient::setup);
    }

    static void setup(FMLClientSetupEvent event) {
        MenuScreens.register(RegistryObjects.INVENTORY_PAGE_MENU, InventoryPageScreen::new);
        MinecraftForge.EVENT_BUS.addListener(InventoryPagesClient::addButtons);
    }

    static void addButtons(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (screen instanceof InventoryScreen inventoryScreen) {
            int buttons = InventoryPages.LIST.size();
            for (int i = 0; i < buttons; i++) {
                int finalI = i;
                event.addListener(new InventoryPageScreen.InventoryPageButton(inventoryScreen.getGuiLeft()-20, inventoryScreen.getGuiTop()+ 20 * i + 80,
                        20,20,Component.empty(),
                        b -> {
                            PacketHandler.sendToServer(new C2SChangePagePacket(finalI));
                        },
                        (pButton, pPoseStack, pMouseX, pMouseY) -> {
                    InventoryPage inventoryPage = ((PlayerDuck)screen.getMinecraft().player).inventoryPageList().get(finalI);
                    inventoryScreen.renderTooltip(pPoseStack,Component.literal("tag: "+inventoryPage.config.tag().location()),pMouseX,pMouseY);
                },finalI,inventoryScreen));
            }
        }
    }

    public static void handle(S2CCarriedItemPacket packet) {
        Minecraft.getInstance().player.containerMenu.setCarried(packet.carried());
    }
}
