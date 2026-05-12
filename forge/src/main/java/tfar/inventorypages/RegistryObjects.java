package tfar.inventorypages;

import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;

public class RegistryObjects {
    public static final MenuType<InventoryPageMenu> INVENTORY_PAGE_MENU = new MenuType<>(InventoryPageMenu::new);
}
