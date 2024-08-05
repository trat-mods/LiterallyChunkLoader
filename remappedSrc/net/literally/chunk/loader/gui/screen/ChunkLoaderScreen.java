package net.literally.chunk.loader.gui.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.literally.chunk.loader.gui.handler.ChunkLoaderGUIHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ChunkLoaderScreen extends CottonInventoryScreen<ChunkLoaderGUIHandler> {
    public ChunkLoaderScreen(ChunkLoaderGUIHandler container, PlayerEntity player, Text title) {
        super(container, player, title);
    }
}