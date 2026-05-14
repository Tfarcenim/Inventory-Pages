package tfar.inventorypages.network;

import net.minecraft.network.FriendlyByteBuf;
import tfar.inventorypages.RendererCubeTarget;
import tfar.inventorypages.ReportTask;
import tfar.inventorypages.network.client.S2CModPacket;

import java.util.List;

public record S2CReportPacket(int itemsCounter, int affectedContainers, int totalContainers,
                              List<RendererCubeTarget> rendererCubeTargets) implements S2CModPacket {

    public S2CReportPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readInt(), buf.readList(RendererCubeTarget::read));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(itemsCounter);
        buf.writeInt(affectedContainers);
        buf.writeInt(totalContainers);

        buf.writeCollection(rendererCubeTargets, (buf1, rendererCubeTarget) ->
            rendererCubeTarget.write(buf1));
    }

    public void handleClient() {
        ReportTask reportTask = new ReportTask(itemsCounter, affectedContainers,
            totalContainers, rendererCubeTargets);
        reportTask.run();
    }
}
