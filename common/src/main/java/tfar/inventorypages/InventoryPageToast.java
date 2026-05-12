package tfar.inventorypages;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;

public class InventoryPageToast implements Toast {

    private final ItemStack icon;
    private static long TIMER = 4000L;
    private static long SLIDE_TIMER = 1000L;
    private final ItemStack pickedUp;

    public InventoryPageToast(ItemStack icon, ItemStack pickedUp) {
        this.icon = icon;
        this.pickedUp = pickedUp;
    }

    @Override
    public Visibility render(PoseStack pPoseStack, ToastComponent pToastComponent, long pTimeSinceLastVisible){

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        pToastComponent.blit(pPoseStack, 0, 0, 0, 0, this.width(), this.height());

        if (pTimeSinceLastVisible<TIMER) {
            pToastComponent.getMinecraft().font.draw(pPoseStack,icon.getDisplayName(),20,1,0x404040);
        } else if (pTimeSinceLastVisible<(SLIDE_TIMER+TIMER)) {
            double slideFraction = (TIMER+SLIDE_TIMER - pTimeSinceLastVisible)/(double)SLIDE_TIMER;

        }

        return pTimeSinceLastVisible > (TIMER+SLIDE_TIMER) ? Visibility.HIDE : Visibility.SHOW;
    }
}
