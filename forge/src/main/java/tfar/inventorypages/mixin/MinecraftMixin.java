package tfar.inventorypages.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.inventorypages.client.InventoryPagesClientForge;

@Mixin(Minecraft.class)
@Debug(export = true)
public class MinecraftMixin {
    @Inject(method = "pickBlock",at = @At(
            value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"),locals = LocalCapture.CAPTURE_FAILHARD)
    private void onPickBlock(CallbackInfo ci, boolean creative, BlockEntity blockentity, HitResult.Type hitresult$type, ItemStack itemstack, Inventory inventory, int i) {
        if (i == -1 && !creative) {
            InventoryPagesClientForge.tryPickBlock(itemstack);
        }
    }
}
