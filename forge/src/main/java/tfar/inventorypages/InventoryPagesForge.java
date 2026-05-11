package tfar.inventorypages;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(InventoryPages.MOD_ID)
public class InventoryPagesForge {
    
    public InventoryPagesForge() {
        IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
        eventbus.addListener(this::reload);
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        InventoryPages.init();
        
    }

    private void reload(AddReloadListenerEvent event) {
        event.addListener(new InventoryPageConfigReloadListener());
    }
}