package tfar.inventorypages.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModDatagen {

    public static void gather(GatherDataEvent e) {
        DataGenerator gen = e.getGenerator();
        gen.addProvider(true,new IPLang(gen));
    }
}
