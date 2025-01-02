<p align="center"><b>📢 Get 25% off of your Minecraft Server! 📢</b></p>
<p align="center">
  <img src="https://solar-digital.com/images/portfolio/120/thumb/bisect-hosting.gif" width="400">
</p>

<p align="center">
    <a href="https://url-shortener.curseforge.com/RVxje" target="_blank">  
        <img src="https://github.com/user-attachments/assets/55cd0dd6-968f-4b4b-bb39-85fc05547f37" width="200"/>
    </a>
</p>


<h2 align="center">Welcome to the Wiki page of Literally Chunk Loader</h2>
<p align="center">
<img src="https://user-images.githubusercontent.com/31132987/80125195-a13b6d80-8591-11ea-828f-1ac07a1ac498.png" width="256">
</p>

## Recipe

![recipeLoader](https://user-images.githubusercontent.com/31132987/80126114-e8762e00-8592-11ea-97ae-04ca2284bba6.png)

## Overview

This mod is implemented by extending and tweaking the Vanilla functionalities of force loading,
making it light and fast.  
The Loader will keep an area of **5x5** chunks force loaded, that means that game mechanics such as
random ticks and entities will remain active in that area. Keep in mind however that mob spawning
occurs only when a player is in the loaded chunks, hence mobs do not spawn in loaded chunks if there
is no player in range.
It is not possible to place multiple Loaders in areas that overlap themselves.
A Loader can be active or inactive.

Data are stored on the physical server side, that means that in a multiplayer environment only the
physical server will have stored the data of the chunks that need to be force loaded.  
The path of the data is:  `mods/literally_chunk_loader/<WorldName>/chunks.data`


> [!IMPORTANT]
> Loaders can be picked up only with Silk Touch enchantment

## Troubleshooting

### Hard reset

In case of weird behaviours and malfunctioning, go to the server folder, into the `mods`
folder and delete the folder `literally_chunk_loader`.
> [!WARNING]
> This will erase all the data and perform a hard reset** of the world chunks data at server next
> startup.

The server must be off when performing this step.

## The Loader GUI

<p align="center">
  <img width="300" alt="loaderrGUI" src="https://user-images.githubusercontent.com/31132987/106477129-1c7a6a00-64a8-11eb-9319-d51379206107.png">
</p>

The new Loader GUI displays the 25 chunks inside the Loader influenced area. By clicking the
corresponding buttons, it is possible to select the chunks to load. It is important to remember that
the GUI is oriented always in the same direction, that is North on top, indipendently of the facing
direction of the player that opens the GUI.   
The GUI is synced with other Loaders, and it updates every time any of the chunks in its range are
force loaded.    
When a button is green, the corresponding chunk is force loaded.    
The Loader is considered placed in the central chunk.

## `lclocate` command

The `lclocate` command can be used to locate placed Loaders.

The synthax is: `/lclocate *dimension*` where dimension: `overworld, the_end,
the_nether, all`.

Keep in mind that the positions returned are *chunk positions*, thus identify the chunk position and
not the actual block position of the Loader.

> [!IMPORTANT]
> As explained in the migration guide, this command will identify only the loaders that have been
> placed in this version, and not on older versions.

## Tips

* To retrieve the currently active force loaded chunks, use the Minecraft command:   
  `/forceload query`
* Remember that its always possible to *hard reset* the force loaded chunks with the Minecraft
  command:  
  `/forceload remove all`
  
> [!WARNING]
> The `/forceload remove all` command will remove all the force loaded chunks regardless of the state of all
> the Loaders in the whole map.

---
