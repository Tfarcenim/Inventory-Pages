package tfar.inventorypages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class InventoryPageConfigReloadListener extends SimplePreparableReloadListener<JsonObject> {
    final Gson gson = new GsonBuilder().create();

    @Override
    protected JsonObject prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return InventoryPages.read(gson);
    }

    @Override
    protected void apply(JsonObject pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        InventoryPages.load(pObject);
    }
}
