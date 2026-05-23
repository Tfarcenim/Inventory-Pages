package tfar.inventorypages;

import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;

public class RegistryObjects {
    public static final MenuType<InventoryPageMenuV2> INVENTORY_PAGE_MENU_V2 = IForgeMenuType.create(InventoryPageMenuV2::new);
}
