package tfar.inventorypages.platform;

import net.minecraft.server.level.ServerPlayer;
import tfar.inventorypages.network.client.S2CModPacket;
import tfar.inventorypages.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return null;
    }

    @Override
    public void sendToClient(S2CModPacket packet, ServerPlayer player) {

    }

    @Override
    public int getMaxStackSizeBiggerStacks() {
        return 0;
    }
}
