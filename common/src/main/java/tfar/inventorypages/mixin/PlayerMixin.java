package tfar.inventorypages.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.inventorypages.InventoryPageList;
import tfar.inventorypages.PlayerDuck;

@Mixin(Player.class)
public class PlayerMixin implements PlayerDuck {

    @Unique
    final InventoryPageList inventoryPageList = new InventoryPageList((Player) (Object)this);

    @Override
    public InventoryPageList inventoryPageList() {
        return inventoryPageList;
    }

    @Inject(method = "readAdditionalSaveData",at = @At("HEAD"))
    private void readExtra(CompoundTag $$0, CallbackInfo ci) {
        ListTag inventorypages = $$0.getList("inventorypages", CompoundTag.TAG_LIST);
        inventoryPageList.load(inventorypages);
    }

    @Inject(method = "addAdditionalSaveData",at = @At("HEAD"))
    private void addExtra(CompoundTag $$0, CallbackInfo ci) {
        $$0.put("inventorypages",inventoryPageList.save());
    }
}
