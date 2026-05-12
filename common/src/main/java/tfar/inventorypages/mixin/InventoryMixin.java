package tfar.inventorypages.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.inventorypages.InventoryPages;
import tfar.inventorypages.PlayerDuck;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Inject(method = "dropAll",at = @At("RETURN"))
    private void dropExtraItems(CallbackInfo ci) {
        ((PlayerDuck)player).inventoryPageList().dropAll();
    }

    @Inject(method = "add(Lnet/minecraft/world/item/ItemStack;)Z",at = @At("HEAD"),cancellable = true)
    private void interceptItem(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        InventoryPages.handle(player,itemStack,cir);
    }
}
