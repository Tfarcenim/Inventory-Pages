package tfar.inventorypages.mixin;

import tfar.inventorypages.InventoryPages;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    
    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(CallbackInfo info) {
        
        InventoryPages.LOG.info("This line is printed by an example mod common mixin!");
        InventoryPages.LOG.info("MC Version: {}", Minecraft.getInstance().getVersionType());
    }
}