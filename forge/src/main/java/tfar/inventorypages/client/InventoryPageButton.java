package tfar.inventorypages.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import tfar.inventorypages.InventoryPage;
import tfar.inventorypages.InventoryPages;
import tfar.inventorypages.PlayerDuck;
import tfar.inventorypages.network.PacketHandler;
import tfar.inventorypages.network.server.C2SCarriedItemPacket;
import tfar.inventorypages.network.server.C2SChangePagePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryPageButton extends Button {
    private final AbstractContainerScreen<?> parent;
    private final int page;


    public InventoryPageButton(AbstractContainerScreen<?> parent,int pX, int pY, int pWidth, int pHeight, Component pMessage, int page) {
        super(pX, pY, pWidth, pHeight, pMessage, b -> {
                    Minecraft mc = Minecraft.getInstance();
                    Player player = mc.player;
                    if (player != null) {
                        if (parent instanceof InventoryPageScreenV2 inventoryPageScreen) {
                            inventoryPageScreen.getMenu().setPage(page);
                        }
                        PacketHandler.sendToServer(new C2SChangePagePacket(page));
                        if (player.isCreative()) {
                           PacketHandler.sendToServer(new C2SCarriedItemPacket(player.inventoryMenu.getCarried()));
                        }
                    }
                },
                (pButton, pPoseStack, pMouseX, pMouseY) -> {
                    InventoryPage inventoryPage = ((PlayerDuck)Minecraft.getInstance().player).inventoryPageList().get(page);

                    List<Component> tooltip = new ArrayList<>();

                    tooltip.add(inventoryPage.config.title());
                    if (Minecraft.getInstance().options.advancedItemTooltips) {
                        if (inventoryPage.config.tag() != null) {
                            tooltip.add(Component.literal("Tag: " + inventoryPage.config.tag().location()));
                        } else{
                            tooltip.add(Component.literal("No filter"));
                        }
                    }
                    Minecraft.getInstance().screen.renderTooltip(pPoseStack,tooltip, Optional.empty(), pMouseX, pMouseY);
                });
        this.parent = parent;
        this.page = page;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Screen screen = Minecraft.getInstance().screen;
        boolean selected = false;
        if (screen instanceof InventoryPageScreenV2 inventoryPageScreenV2) {
            selected = inventoryPageScreenV2.getMenu().getPage() == this.page;
        }

        if (selected){
            RenderSystem.setShaderTexture(0, InventoryPages.id("textures/gui/tab_selected.png"));
        } else {
            RenderSystem.setShaderTexture(0, InventoryPages.id("textures/gui/tab.png"));
        }

        int integer = InventoryPages.LIST.get(page).pageColor();

        float f = (float) (integer >> 16 & 255) / 255.0F;
        float f1 = (float) (integer >> 8 & 255) / 255.0F;
        float f2 = (float) (integer & 255) / 255.0F;

        RenderSystem.setShaderColor(f, f1, f2, 1.0F);

        RenderSystem.enableDepthTest();
        int width = selected ? 24 : 20;

        blit(pPoseStack, this.x, this.y, 0,0, width, this.height, width, 22);

        RenderSystem.setShaderColor(1,1,1,1);

        InventoryPage.Config config = InventoryPages.LIST.get(this.page);
        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(config.icon(), x + 3, y + 2);

        if (this.isHovered) {
            this.renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }
}
