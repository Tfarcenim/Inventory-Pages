package tfar.inventorypages;



import tfar.inventorypages.client.InventoryPagesClientForge;

import java.util.List;

public record ReportTask(int itemsCounter, int affectedContainers, int totalContainers,
                         List<tfar.inventorypages.RendererCubeTarget> rendererCubeTargets) implements Runnable {

    @Override
    public void run() {
        if (DropOffConfig.Client.highlightContainers.get()) {
            InventoryPagesClientForge.RendererCube.draw(rendererCubeTargets);
        }

       /* if (tfar.inventorypages.DropOffConfig.Client.displayMessage.get()) {
            String message = red(String.valueOf(itemsCounter)) +
                " items moved to " + red(String.valueOf(affectedContainers)) +
                " containers of " + red(String.valueOf(totalContainers)) +
                " checked in total.";

            ClientUtils.printToChat(message);
        }*/
    }
}
