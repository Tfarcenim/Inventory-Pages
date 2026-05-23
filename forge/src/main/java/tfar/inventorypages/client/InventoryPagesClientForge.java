package tfar.inventorypages.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.inventorypages.*;
import tfar.inventorypages.network.C2SPacketRequestDropoff;
import tfar.inventorypages.network.PacketHandler;
import tfar.inventorypages.network.client.S2CCarriedItemPacket;
import tfar.inventorypages.network.server.C2SBackPacket;
import tfar.inventorypages.network.server.C2SChangePagePacket;

import java.util.ArrayList;
import java.util.List;

public class InventoryPagesClientForge {

    public static void init(IEventBus bus) {
        bus.addListener(InventoryPagesClientForge::setup);
        bus.addListener(InventoryPagesClientForge::registerOverlays);
    }

    public static final IGuiOverlay OVERLAY = (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        int i = 0;
        for (ClientPopup clientPopup : InventoryPagesClient.popups) {
            clientPopup.render(poseStack,partialTick,screenWidth,screenHeight);
        }
    };

    static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (!Minecraft.getInstance().isPaused()) {
                InventoryPagesClient.tickPopups();
            }
        }
    }

    static void setup(FMLClientSetupEvent event) {
        MenuScreens.register(RegistryObjects.INVENTORY_PAGE_MENU_V2, InventoryPageScreenV2::new);
        MinecraftForge.EVENT_BUS.addListener(InventoryPagesClientForge::addButtons);
        MinecraftForge.EVENT_BUS.addListener(InventoryPagesClientForge::onRenderWorldLastEvent);
        MinecraftForge.EVENT_BUS.addListener(InventoryPagesClientForge::clientTick);
    }

    static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("popup",OVERLAY);
    }

    static void addButtons(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (screen instanceof AbstractContainerScreen<?> abstractContainerScreen) {

            int leftPos = abstractContainerScreen.getGuiLeft();
            int topPos = abstractContainerScreen.getGuiTop();

            if (abstractContainerScreen instanceof InventoryScreen || abstractContainerScreen instanceof CreativeModeInventoryScreen) {
                int buttons = InventoryPages.LIST.size();
                for (int i = 0; i < buttons; i++) {
                    int finalI = i;
                    event.addListener(new InventoryPageButton(abstractContainerScreen,leftPos - 20, topPos + 20 * i +
                            InventoryPageScreenV2.TOP + 20,
                            20, 20, Component.empty(), finalI));
                }
            }

            if (!canDisplay(event.getScreen()) || !DropOffConfig.Client.showInventoryButton.get()) {
                return;
            }


            boolean isCreative = Minecraft.getInstance().player.getAbilities().instabuild;

            int xPos = leftPos + 80 +
                    (isCreative ? DropOffConfig.Client.creativeInventoryButtonXOffset.get()
                            : DropOffConfig.Client.survivalInventoryButtonXOffset.get());
            int yPos = topPos + 80
                    + (isCreative ? DropOffConfig.Client.creativeInventoryButtonYOffset.get()
                    : DropOffConfig.Client.survivalInventoryButtonYOffset.get());
            if (DropOffConfig.Client.enableDump.get()) {
                DropoffButton dump = new DropoffButton(xPos, yPos, 10, 14, Component.literal("^"), b -> actionPerformed(true),
                        (pButton, pPoseStack, pMouseX, pMouseY) -> {
                            screen.renderTooltip(pPoseStack, Component.translatable("dropoff.dump_nearby"), pMouseX, pMouseY);
                        });
                event.addListener(dump);
            }

            if (screen instanceof InventoryScreen) {
                event.addListener(new MoveToPagesButton(xPos + 24, yPos, 10, 14, Component.literal("^"), b -> C2SBackPacket.INSTANCE2.send(),
                        (pButton, pPoseStack, pMouseX, pMouseY) -> {
                            screen.renderTooltip(pPoseStack, Component.literal("Move To Pages"), pMouseX, pMouseY);
                        }));
            }

            DropoffButton deposit = new DropoffButton(xPos + 12, yPos, 10, 14, Component.literal("^"), b -> actionPerformed(false),
                    (pButton, pPoseStack, pMouseX, pMouseY) -> {
                        screen.renderTooltip(pPoseStack, Component.translatable("dropoff.quick_stack"), pMouseX, pMouseY);
                    });
            event.addListener(deposit);
        }
    }

    public static void handle(S2CCarriedItemPacket packet) {
        Minecraft.getInstance().player.containerMenu.setCarried(packet.carried());
    }

    public static void onRenderWorldLastEvent(RenderLevelStageEvent event) {
        RendererCube.tryToRender(event);
    }

    public static class RendererCube {

        private static List<RendererCubeTarget> rendererCubeTargets = new ArrayList<>();
        private static long lastDrawTime;

        public static void draw(List<RendererCubeTarget> rendererCubeTargets) {
            RendererCube.rendererCubeTargets = rendererCubeTargets;
            lastDrawTime = System.currentTimeMillis();
        }

        /**
         * This method called by RenderWorldLastEvent handler.
         * It does nothing until the draw() method assign the necessary delay to the
         * global field named currentTime.
         */
        static void tryToRender(RenderLevelStageEvent event) {
            long timeWhenDissapear = lastDrawTime + DropOffConfig.Client.highlightDelay.get();
            if ((System.currentTimeMillis() >= timeWhenDissapear) && DropOffConfig.Client.highlightDelay.get() >= 0L) {
                return;
            }
            renderBlocks(event, rendererCubeTargets);
        }
    }


    public static void printToChat(String message) {
        message = "[" + ChatFormatting.BLUE + "DropOff" + ChatFormatting.RESET + "]: " + message;

        LocalPlayer player = Minecraft.getInstance().player;
        var textComponentString = Component.literal(message);

        player.sendSystemMessage(textComponentString);
    }

    public static void sendNoSpectator(boolean dump) {
        if (Minecraft.getInstance().player.isSpectator()) {
            printToChat("Action not allowed in spectator mode.");
        } else {
            C2SPacketRequestDropoff dropoffMessage = new C2SPacketRequestDropoff(
                    DropOffConfig.Client.ignoreHotBar.get(),
                    dump,
                    DropOffConfig.blockEntityBlacklist,
                    DropOffConfig.Client.minSlotCount.get());
            PacketHandler.INSTANCE.sendToServer(dropoffMessage);
        }
    }

    public static void renderBlocks(RenderLevelStageEvent e, List<RendererCubeTarget> rendererCubeTargets) {
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            return;
        }
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        PoseStack stack = e.getPoseStack();

        stack.pushPose();

        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        stack.translate(-cam.x, -cam.y, -cam.z);

        rendererCubeTargets.forEach(rendererCubeTarget -> {
            VertexConsumer builder = buffer.getBuffer(RenderType.LINES);
            AABB bb = Shapes.block().bounds().move(rendererCubeTarget.blockPos().getX(),
                    rendererCubeTarget.blockPos().getY(), rendererCubeTarget.blockPos().getZ());
            float red = (rendererCubeTarget.color() >> 16 & 0xff) / 255f;
            float green = (rendererCubeTarget.color() >> 8 & 0xff) / 255f;
            float blue = (rendererCubeTarget.color() & 0xff) / 255f;

            LevelRenderer.renderLineBox(stack, builder, bb, red, green, blue, 1);
            buffer.endBatch(RenderType.LINES);
        });

        stack.popPose();
    }

    public static boolean canDisplay(Screen screen) {
        if (screen instanceof InventoryScreen) {return true;}
        //else if (screen instanceof InventoryPageScreenV2) {return true;}
        return false;
    }

    static void actionPerformed(boolean dump) {
        InventoryPagesClientForge.sendNoSpectator(dump);
    }

}
