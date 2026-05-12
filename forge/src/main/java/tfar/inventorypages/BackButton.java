package tfar.inventorypages;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class BackButton extends Button {
    public BackButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, OnTooltip pOnTooltip) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pOnTooltip);
    }
}
