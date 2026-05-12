package tfar.inventorypages;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import tfar.inventorypages.network.PacketHandler;
import tfar.inventorypages.network.server.C2SBackPacket;
import tfar.inventorypages.network.server.C2SChangePagePacket;

import java.util.ArrayList;
import java.util.List;

public class InventoryPageScreenV2 extends AbstractContainerScreen<InventoryPageMenuV2> {
    private float xMouse;
    private float yMouse;


    private BackButton backButton;
    private List<InventoryPageButton> pageButtons = new ArrayList<>();

    public InventoryPageScreenV2(InventoryPageMenuV2 inventoryPageMenu, Inventory $$0, Component $$1) {
        super(inventoryPageMenu, $$0, $$1);
    }

    @Override
    protected void init() {
        super.init();
        pageButtons.clear();
        backButton = new BackButton(leftPos - 20, topPos + TOP,
                20, 20, Component.literal("B"),
                b -> {
            //open vanilla inventory
                    PacketHandler.sendToServer(C2SBackPacket.INSTANCE);
                    ItemStack stack = minecraft.player.containerMenu.getCarried();
                    minecraft.player.containerMenu.setCarried(ItemStack.EMPTY);
                    if (minecraft.gameMode.isServerControlledInventory()) {//opens vehicle inventory
                        minecraft.player.sendOpenInventory();
                    } else {
                        //this.tutorial.onOpenInventory();
                        minecraft.setScreen(new InventoryScreen(minecraft.player));
                        minecraft.player.containerMenu = minecraft.player.inventoryMenu;
                        minecraft.player.containerMenu.setCarried(stack);
                    }
                },
                (pButton, pPoseStack, pMouseX, pMouseY) -> {

                });
        addRenderableWidget(backButton);
        addPages();
    }

    public static final int TOP = 60;

    void addPages() {
        int buttons = InventoryPages.LIST.size();
        for (int i = 0; i < buttons; i++) {
            int finalI = i;
            InventoryPageButton pageButton = new InventoryPageButton(leftPos - 20, topPos + 20 * i + TOP + 20,
                    20, 20, Component.empty(),
                    b -> PacketHandler.sendToServer(new C2SChangePagePacket(finalI)),
                    (pButton, pPoseStack, pMouseX, pMouseY) -> {

                    }, finalI);
            pageButtons.add(pageButton);
            addRenderableWidget(pageButton);
        }
    }

    @Override
    protected void renderLabels(PoseStack $$0, int pMouseX, int pMouseY) {
        this.font.draw($$0, Component.literal("Page " + menu.page), this.titleLabelX, this.titleLabelY, 0x404040);
        this.font.draw($$0, this.title, inventoryLabelX, inventoryLabelY, 0x404040);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);

    }

    public static final ResourceLocation INVENTORY_PAGE_LOCATION = InventoryPages.id("textures/gui/inventory_page.png");


    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, INVENTORY_PAGE_LOCATION);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
    }

    public static class InventoryPageButton extends Button {
        private final int page;


        public InventoryPageButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, OnTooltip pOnTooltip, int page) {
            super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pOnTooltip);
            this.page = page;
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
            InventoryPage.Config config = InventoryPages.LIST.get(this.page);
            Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(config.icon(), x + 2, y + 2);
        }

    }
}
