package net.literally.chunk.loader.initializer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.literally.chunk.loader.data.LclData;
import net.literally.chunk.loader.data.SerializableChunkPos;
import net.literally.chunk.loader.saves.ChunksSerializeManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public final class LCLCommands {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            LCLLocate.define(dispatcher);
        });
    }

    private final static class LCLLocate {
        public static void define(CommandDispatcher<ServerCommandSource> dispatcher) {
            ArrayList<String> launchersList = new ArrayList<>();
            dispatcher.register(CommandManager.literal("lclocate").then(CommandManager.argument("dimension", word()).suggests(suggestedStrings()).executes(ctx -> {
                String dimension = getString(ctx, "dimension");
                ArrayList<SerializableChunkPos> loaders = getLoadersInDimension(ctx, dimension);
                if (loaders.size() == 0) {
                    ctx.getSource().sendFeedback((Text.of("No loaders found in dimension: " + dimension + "\n")), false);
                    return 1;
                }
                int size = loaders.size();
                StringBuilder response = new StringBuilder("Found " + size + " placed loaders ");
                if (!dimension.equals("all")) {
                    response.append("in dimension: ").append(dimension).append("\n");

                }
                else {
                    response.append(": \n");
                }
                for (int i = 0; i < size; i++) {
                    SerializableChunkPos current = loaders.get(i);
                    response.append("[").append(current.getX()).append(", ").append(current.getZ()).append("]");
                    if (dimension.equals("all")) {
                        response.append(" in ").append(current.getDimension());
                    }
                    if (i < size - 1) {
                        response.append(", ");
                    }
                }
                ctx.getSource().sendFeedback(Text.of(response.toString()), false);
                return 1;
            })));
        }

        public static SuggestionProvider<ServerCommandSource> suggestedStrings() {
            ArrayList<String> suggestions = new ArrayList<>();
            suggestions.add(World.OVERWORLD.getValue().getPath());
            suggestions.add(World.NETHER.getValue().getPath());
            suggestions.add(World.END.getValue().getPath());
            suggestions.add("all");
            return (ctx, builder) -> getSuggestionsBuilder(builder, suggestions);
        }

        private static CompletableFuture<Suggestions> getSuggestionsBuilder(SuggestionsBuilder builder, List<String> list) {
            String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

            if (list.isEmpty()) { // If the list is empty then return no suggestions
                return Suggestions.empty(); // No suggestions
            }

            for (String str : list) { // Iterate through the supplied list
                if (str.toLowerCase(Locale.ROOT).startsWith(remaining)) {
                    builder.suggest(str); // Add every single entry to suggestions list.
                }
            }
            return builder.buildFuture(); // Create the CompletableFuture containing all the suggestions
        }

        private static ArrayList<SerializableChunkPos> getLoadersInDimension(CommandContext<ServerCommandSource> ctx, String dimension) {
            ArrayList<SerializableChunkPos> res = new ArrayList<>();
            LclData areasData = ChunksSerializeManager.deserialize(ctx.getSource().getWorld().getServer().getSaveProperties().getLevelName());
            if (areasData == null) {
                return res;
            }
            ArrayList<SerializableChunkPos> all = areasData.getLoadersChunks();
            if (dimension.equals("all")) {
                return all;
            }

            for (SerializableChunkPos chunk : all) {
                if (chunk.getDimension().equals(dimension)) {
                    res.add(chunk);
                }
            }
            return res;
        }
    }
}
