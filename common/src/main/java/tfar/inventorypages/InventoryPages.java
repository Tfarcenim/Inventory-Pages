package tfar.inventorypages;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.inventorypages.platform.Services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Predicate;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class InventoryPages {

    public static final String MOD_ID = "inventorypages";
    public static final String MOD_NAME = "Inventory Pages";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(MOD_ID)
                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("clear")
                        .then(Commands.argument("page", IntegerArgumentType.integer(0))
                                .executes(InventoryPages::clearPage)
                        )
                )
        );
    }

    private static int clearPage(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();
        int page = IntegerArgumentType.getInteger(ctx, "page");
        InventoryPageList inventoryPageList = ((PlayerDuck)player).inventoryPageList();
        if (page>= inventoryPageList.size())
            return 0;
        InventoryPage inventoryPage = inventoryPageList.get(page);
        inventoryPage.clearItems();
        return 1;
    }

    public static final Path PATH = Services.PLATFORM.getConfigDirectory().resolve("inventorypages.json");

    public static JsonObject read(Gson gson) {
        if (!PATH.toFile().exists()) {
            writeDefaultConfig();
        }
        try (Reader reader = new FileReader(PATH.toFile())) {
            JsonReader jsonReader = new JsonReader(reader);
            LOG.info("Loading existing config");
            return gson.fromJson(jsonReader, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static final List<InventoryPage.Config> LIST = new ArrayList<>();
    public static boolean SHOW_POPUPS;
    public static boolean KEEP_INV_PAGES;

    public static void load(JsonObject jsonObject) {
        LIST.clear();

        JsonArray jsonArray = jsonObject.getAsJsonArray("pages");

        for (JsonElement jsonElement : jsonArray) {
            JsonObject o = jsonElement.getAsJsonObject();
            ItemStack itemStack = new ItemStack(GsonHelper.getAsItem(o, "icon", Items.BARRIER));
            @Nullable TagKey<Item> tagKey = o.has("tag") ? TagKey.create(Registry.ITEM_REGISTRY,
                    new ResourceLocation(GsonHelper.getAsString(o, "tag"))) : null;
            Component title = Component.Serializer.fromJson(GsonHelper.getAsString(o, "title", "{\"text\":\"Untitled Page\"}"));
            int pageColor = Integer.decode(GsonHelper.getAsString(o, "page_color", "0xffffffff"));
            LIST.add(new InventoryPage.Config(itemStack, tagKey, title, pageColor));
        }

        SHOW_POPUPS = GsonHelper.getAsBoolean(jsonObject, "show_popups", true);
        KEEP_INV_PAGES = GsonHelper.getAsBoolean(jsonObject, "keep_page_inventory_on_death", false);

        //MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        //if (server != null) server.getPlayerList().getPlayers().forEach(player -> PacketHandler.sendToClient(new S2CConfigPacket(MAP),player));
    }

    public static void writeDefaultConfig() {
        try (InputStream resource = InventoryPages.class.getClassLoader()
                .getResourceAsStream("inventorypages.json")) {
            Files.copy(resource, PATH, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static void handle(Player player, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        InventoryPageList inventoryPageList = ((PlayerDuck) player).inventoryPageList();
        for (int page = 0; page < inventoryPageList.size(); page++) {
            InventoryPage inventoryPage = inventoryPageList.get(page);
            boolean fullyHandled = inventoryPage.tryAdd(player, itemStack, page);
            if (fullyHandled) {
                cir.setReturnValue(true);
                break;
            }
        }
    }

    public static void tick(Player player) {
        ((PlayerDuck) player).inventoryPageList().tick();
    }
}