package net.literally.chunk.loader.gui.handler;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.literally.chunk.loader.data.LCLData;
import net.literally.chunk.loader.data.SerializableChunkPos;
import net.literally.chunk.loader.initializer.LCLBlocks;
import net.literally.chunk.loader.initializer.LCLGUIHandlers;
import net.literally.chunk.loader.network.packets.packet.ForcedChunksUpdatePacketPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChunkLoaderGUIHandler extends SyncedGuiDescription {
    //ChunkLoaderBlockEntity loaderEntity;
    BlockPos pos;
    WToggleButton[][] buttonsMatrix;
    SerializableChunkPos centre;
    ScreenHandlerContext context;

    public ChunkLoaderGUIHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, BlockPos pos) {
        super(LCLGUIHandlers.CHUNK_LOADER_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context), null);
        this.context = context;

        this.pos = pos;

        centre = new SerializableChunkPos(pos, world.getRegistryKey().getValue().getPath());
        WPlainPanel root = new WPlainPanel();
        root.setSize(128, 140);
        root.setInsets(Insets.ROOT_PANEL);
        setupButtonsGrid(root);

        WButton selectAll = new WButton(Text.of("All"));
        selectAll.setOnClick(() -> {
            ArrayList<SerializableChunkPos> data = new ArrayList<>();
            for (int i = 0; i < LCLData.SIZE; i++) {
                for (int j = 0; j < LCLData.SIZE; j++) {
                    data.add(centre.getChunkAtRelativeOffset(i, j));
                }
            }
            ForcedChunksUpdatePacketPayload pack = new ForcedChunksUpdatePacketPayload(pos.getX(), pos.getZ(), true, data);
            ClientPlayNetworking.send(pack);
        });

        WButton selectNone = new WButton(Text.of("None"));
        selectNone.setOnClick(() -> {
            ArrayList<SerializableChunkPos> data = new ArrayList<>();
            for (int i = 0; i < LCLData.SIZE; i++) {
                for (int j = 0; j < LCLData.SIZE; j++) {
                    data.add(centre.getChunkAtRelativeOffset(i, j));
                }
            }
            ForcedChunksUpdatePacketPayload pack = new ForcedChunksUpdatePacketPayload(pos.getX(), pos.getZ(), false, data);
            ClientPlayNetworking.send(pack);
        });

        root.add(selectAll, 16, 128, 40, 24);
        root.add(selectNone, 71, 128, 40, 24);
        setRootPanel(root);
    }

    @Override
    public boolean canUse(PlayerEntity entity) {
        return canUse(context, entity, LCLBlocks.CHUNK_LOADER_BLOCK);
    }

    public void refreshGUI(ForcedChunksUpdatePacketPayload payload) {
        List<SerializableChunkPos> chunks = payload.getChunksPos();
        if (!chunks.isEmpty()) {
            if (!chunks.getFirst().getDimension().equals(centre.getDimension())) {
                System.out.println("not calculating, other dim");
                return;
            }
            SerializableChunkPos updateCentre = new SerializableChunkPos(payload.getX(), payload.getZ(), centre.getDimension());
            if (centre.distanceFrom(updateCentre) > LCLData.SIZE * 1.5D) {
                System.out.println("not calculating too far");
                return;
            }
            for (int i = 0; i < LCLData.SIZE; i++) {
                for (int j = 0; j < LCLData.SIZE; j++) {
                    if (chunks.contains(centre.getChunkAtRelativeOffset(i, j))) {
                        buttonsMatrix[i][j].setToggle(payload.isState());
                    }
                }
            }
        }
    }

    private void setupButtonsGrid(WPlainPanel root) {
        WLabel north = new WLabel(Text.of("N"));
        north.setHorizontalAlignment(HorizontalAlignment.CENTER);
        north.setVerticalAlignment(VerticalAlignment.CENTER);
        WLabel south = new WLabel(Text.of("S"));
        south.setHorizontalAlignment(HorizontalAlignment.CENTER);
        south.setVerticalAlignment(VerticalAlignment.CENTER);
        WLabel east = new WLabel(Text.of("E"));
        east.setHorizontalAlignment(HorizontalAlignment.CENTER);
        east.setVerticalAlignment(VerticalAlignment.CENTER);
        WLabel west = new WLabel(Text.of("W"));
        west.setHorizontalAlignment(HorizontalAlignment.CENTER);
        west.setVerticalAlignment(VerticalAlignment.CENTER);

        buttonsMatrix = new WToggleButton[LCLData.SIZE][LCLData.SIZE];
        for (int i = 0; i < LCLData.SIZE; i++) {
            int posX = (19 * i + 16);
            for (int j = 0; j < LCLData.SIZE; j++) {
                WToggleButton curr = new WToggleButton(new Identifier("lchunkloader:textures/gui/loaded.png"), new Identifier("lchunkloader:textures/gui/not_loaded.png"));
                int finalI = i;
                int finalJ = j;
                //System.out.println(i+", "+j);
                curr.setOnToggle((state) -> {
                    ForcedChunksUpdatePacketPayload pack = new ForcedChunksUpdatePacketPayload(pos.getX(), pos.getZ(), state, Collections.singletonList(centre.getChunkAtRelativeOffset(finalI, finalJ)));
                    ClientPlayNetworking.send(pack);
                });
                int posY = 19 * j + 16;
                root.add(curr, posX, posY, 16, 16);
                buttonsMatrix[i][j] = curr;
            }
        }
        root.add(north, 54, 2, 16, 16);
        root.add(south, 54, 109, 16, 16);
        root.add(west, 2, 54, 16, 16);
        root.add(east, 109, 54, 16, 16);
    }
}