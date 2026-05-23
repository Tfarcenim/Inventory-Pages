package tfar.inventorypages.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tfar.inventorypages.InventoryPages;

public class ClientPopup {

    private final int page;
    private final ItemStack pickedUp;
    private int ticksRendered;
    private final int initialIndex;

    public static final int TIME = 50;

    public ClientPopup(int page, ItemStack pickedUp, int initialIndex) {
        this.page = page;
        this.pickedUp = pickedUp;
        this.initialIndex = initialIndex;
    }

    public boolean tick() {
        ticksRendered++;
        return ticksRendered > (TIME + 20 + 15 * initialIndex);
    }

    public void render(PoseStack pPoseStack, float pPartialTick,int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        float scale =2/3f;
        int scaledWidth = (int)(screenWidth/ scale);
        int scaledHeight = (int)(screenHeight / scale);
        float x = (int) (8 /scale);
        float y = (int) ((screenHeight-21) / scale) - 16 * initialIndex / scale;

        Component text = Component.literal(pickedUp.getCount()+"x").append(pickedUp.getHoverName());

        float alpha = 1;

        float fullTicks = ticksRendered+pPartialTick;

        if (fullTicks > TIME) {
            y += (2.5f*(fullTicks-TIME));
        }

        pPoseStack.pushPose();
        pPoseStack.scale(scale, scale, scale);
        //GuiComponent.fill(pPoseStack,x-1,y-1,x+mc.font.width(text)+20,y+mc.font.lineHeight+8,0xff000000| InventoryPages.LIST.get(page).pageColor());
        mc.font.drawShadow(pPoseStack,text,x + 18/scale,y + 3.5f/scale, 0xffffffff);
        mc.getItemRenderer().renderAndDecorateFakeItem(InventoryPages.LIST.get(page).icon(), (int) ((int) x*scale), (int) (y*scale));
        pPoseStack.popPose();
    }
}
