package tfar.inventorypages.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import tfar.inventorypages.InventoryPages;

public class IPLang extends LanguageProvider {
    public IPLang(DataGenerator gen) {
        super(gen, InventoryPages.MOD_ID,"en_us");
    }

    @Override
    protected void addTranslations() {
        add("dropoff.dump_nearby", "Dump Nearby");
        add("dropoff.quick_stack", "Quick Stack");
        add("dropoff.movetopages", "Move To Pages");
    }
}
