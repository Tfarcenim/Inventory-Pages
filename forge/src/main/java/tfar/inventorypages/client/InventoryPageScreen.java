package tfar.inventorypages.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import tfar.inventorypages.InventoryPage;
import tfar.inventorypages.InventoryPageMenu;
import tfar.inventorypages.InventoryPages;
import tfar.inventorypages.network.PacketHandler;
import tfar.inventorypages.network.server.C2SBackPacket;
import tfar.inventorypages.network.server.C2SChangePagePacket;

import java.util.ArrayList;
import java.util.List;

public class InventoryPageScreen extends EffectRenderingInventoryScreen<InventoryPageMenu> implements RecipeUpdateListener {
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private float xMouse;
    private float yMouse;
    private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
    private boolean recipeBookComponentInitialized;
    private boolean widthTooNarrow;
    private boolean buttonClicked;

    private BackButton backButton;
    private List<InventoryPageButton> pageButtons = new ArrayList<>();

    public InventoryPageScreen(InventoryPageMenu inventoryPageMenu, Inventory $$0, Component $$1) {
        super(inventoryPageMenu, $$0, $$1);
        this.passEvents = true;
        this.titleLabelX = 97;
    }

    @Override
    public void containerTick() {
        this.recipeBookComponent.tick();
    }

    @Override
    protected void init() {
        super.init();
        pageButtons.clear();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.recipeBookComponentInitialized = true;
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        this.addRenderableWidget(new ImageButton(this.leftPos + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, ($$0) -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            ((ImageButton) $$0).setPosition(this.leftPos + 104, this.height / 2 - 22);
            updateButtonPositions();
            this.buttonClicked = true;
        }));
        this.addWidget(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
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

    void updateButtonPositions() {
        for (InventoryPageButton button : this.pageButtons) {
            button.setPosition(this.leftPos - 20, topPos + 20 * button.page + TOP + 20);
        }
    }

    void addPages() {
        int buttons = InventoryPages.LIST.size();
        for (int i = 0; i < buttons; i++) {
            int finalI = i;
            InventoryPageButton pageButton = new InventoryPageButton(leftPos - 20, topPos + 20 * i + TOP + 20,
                    20, 20, Component.empty(),
                    b -> PacketHandler.sendToServer(new C2SChangePagePacket(finalI)),
                    (pButton, pPoseStack, pMouseX, pMouseY) -> {

                    }, finalI, this);
            pageButtons.add(pageButton);
            addRenderableWidget(pageButton);
        }
    }

    @Override
    protected void renderLabels(PoseStack $$0, int pMouseX, int pMouseY) {
        this.font.draw($$0, this.title.copy().append(" Page " + menu.page), (float) this.titleLabelX - 19, (float) this.titleLabelY, 0x404040);
    }

    @Override
    public void render(PoseStack $$0, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground($$0);
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg($$0, pPartialTick, pMouseX, pMouseY);
            this.recipeBookComponent.render($$0, pMouseX, pMouseY, pPartialTick);
        } else {
            this.recipeBookComponent.render($$0, pMouseX, pMouseY, pPartialTick);
            super.render($$0, pMouseX, pMouseY, pPartialTick);
            this.recipeBookComponent.renderGhostRecipe($$0, this.leftPos, this.topPos, false, pPartialTick);
        }

        this.renderTooltip($$0, pMouseX, pMouseY);
        this.recipeBookComponent.renderTooltip($$0, this.leftPos, this.topPos, pMouseX, pMouseY);
        this.xMouse = (float) pMouseX;
        this.yMouse = (float) pMouseY;
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, INVENTORY_LOCATION);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        InventoryScreen.renderEntityInInventory($$4 + 51, $$5 + 75, 30, (float) ($$4 + 51) - this.xMouse, (float) ($$5 + 75 - 50) - this.yMouse, this.minecraft.player);
    }

    @Override
    protected boolean isHovering(int $$0, int $$1, int $$2, int $$3, double $$4, double $$5) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.recipeBookComponent.mouseClicked($$0, $$1, $$2)) {
            this.setFocused(this.recipeBookComponent);
            return true;
        } else {
            return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.mouseClicked($$0, $$1, $$2);
        }
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if (this.buttonClicked) {
            this.buttonClicked = false;
            return true;
        } else {
            return super.mouseReleased($$0, $$1, $$2);
        }
    }

    @Override
    protected boolean hasClickedOutside(double $$0, double $$1, int $$2, int $$3, int $$4) {
        boolean $$5 = $$0 < (double) $$2 || $$1 < (double) $$3 || $$0 >= (double) ($$2 + this.imageWidth) || $$1 >= (double) ($$3 + this.imageHeight);
        return this.recipeBookComponent.hasClickedOutside($$0, $$1, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, $$4) && $$5;
    }

    @Override
    protected void slotClicked(Slot slot, int $$1, int $$2, ClickType $$3) {
        super.slotClicked(slot, $$1, $$2, $$3);
        this.recipeBookComponent.slotClicked(slot);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    @Override
    public void removed() {
        if (this.recipeBookComponentInitialized) {
            this.recipeBookComponent.removed();
        }

        super.removed();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }

    public static class InventoryPageButton extends Button {
        private final int page;
        private final EffectRenderingInventoryScreen<?> screen;


        public InventoryPageButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, OnTooltip pOnTooltip, int page,
                                   EffectRenderingInventoryScreen<?> screen) {
            super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pOnTooltip);
            this.page = page;
            this.screen = screen;
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
            InventoryPage.Config config = InventoryPages.LIST.get(this.page);
            Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(config.icon(), x + 2, y + 2);
        }

        public void setPosition(int pX, int pY) {
            this.x = pX;
            this.y = pY;
        }
    }
}
