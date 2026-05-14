package tfar.inventorypages;

import net.minecraft.network.chat.Component;

public class test {
    public static void main(String[] args) {
        String untitledPage = Component.Serializer.toJson(Component.literal("Untitled Page"));
        System.out.println(untitledPage);
    }
}
