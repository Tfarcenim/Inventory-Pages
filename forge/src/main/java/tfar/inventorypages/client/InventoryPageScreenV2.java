package tfar.inventorypages.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import tfar.inventorypages.DropOffConfig;
import tfar.inventorypages.InventoryPageMenuV2;
import tfar.inventorypages.InventoryPages;
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
                    C2SBackPacket.INSTANCE.send();
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
        addMoveButtons();
    }

    public static final int TOP = 0;

    void addPages() {
        int buttons = InventoryPages.LIST.size();
        for (int i = 0; i < buttons; i++) {
            int finalI = i;
            InventoryPageButton pageButton = new InventoryPageButton(leftPos - 20, topPos + 20 * i + TOP + 20,
                    20, 20, Component.empty(),
                    b -> {
                        menu.setPage(finalI);
                        PacketHandler.sendToServer(new C2SChangePagePacket(finalI));
                    }, finalI);
            pageButtons.add(pageButton);
            addRenderableWidget(pageButton);
        }
    }

    void addMoveButtons() {
        if (DropOffConfig.Client.enableDump.get()) {
            DropoffButton dump = new DropoffButton(leftPos + 100, topPos + 3, 10, 12, Component.literal("^"), b ->
                    InventoryPagesClientForge.actionPerformed(true),
                    (pButton, pPoseStack, pMouseX, pMouseY) -> {
                        renderTooltip(pPoseStack, Component.translatable("dropoff.dump_nearby"), pMouseX, pMouseY);
                    });
            addRenderableWidget(dump);
        }

        DropoffButton deposit = new DropoffButton(leftPos + 110, topPos + 3, 10, 12, Component.literal("^"), b ->
                InventoryPagesClientForge.actionPerformed(false),
                (pButton, pPoseStack, pMouseX, pMouseY) -> {
                    renderTooltip(pPoseStack, Component.translatable("dropoff.quick_stack"), pMouseX, pMouseY);
                });
        addRenderableWidget(deposit);
    }

    @Override
    protected void renderLabels(PoseStack $$0, int pMouseX, int pMouseY) {
        this.font.draw($$0, menu.getActivePage().config.title(), this.titleLabelX, this.titleLabelY, 0x404040);
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
        int integer = menu.getActivePage().config.pageColor();

        float f = (float) (integer >> 16 & 255) / 255.0F;
        float f1 = (float) (integer >> 8 & 255) / 255.0F;
        float f2 = (float) (integer & 255) / 255.0F;

        RenderSystem.setShaderColor(f, f1, f2, 1.0F);
        RenderSystem.setShaderTexture(0, INVENTORY_PAGE_LOCATION);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
    }

}
