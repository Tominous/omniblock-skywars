package net.omniblock.skywars.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Scan {

	public static Map<String, List<Chunk>> WORLD_CHUNKS = new HashMap<String, List<Chunk>>();

	@SuppressWarnings("deprecation")
	public static List<Location> oneMaterial(World world, Material m) {

		List<Location> locationblock = new ArrayList<Location>();
		List<Chunk> arrayOfChunk = Lists.newArrayList();

		if (WORLD_CHUNKS.containsKey(world.getName())) {

			arrayOfChunk = WORLD_CHUNKS.get(world.getName());

		} else {

			arrayOfChunk = MCAUtil.getChunksByMCAFiles(world);
			WORLD_CHUNKS.put(world.getName(), arrayOfChunk);

		}

		int chunk = arrayOfChunk.size();

		@SuppressWarnings("unused")
		int numberofscannedblocks = 0;
		for (int i = 0; i < chunk; i++) {

			Chunk c = arrayOfChunk.get(i);

			ChunkSnapshot csnapshot = c.getChunkSnapshot(false, false, false);

			int blockX = csnapshot.getX() << 4;
			int blockZ = csnapshot.getZ() << 4;

			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = 0; y < world.getMaxHeight(); y++) {

						if (csnapshot.getBlockTypeId(x, y, z) == m.getId()) {

							Location blockLoc = new Location(world, blockX + x, y, blockZ + z);
							locationblock.add(blockLoc);

							numberofscannedblocks = locationblock.size();
						}

					}
				}
			}
		}

		return locationblock;
	}

	@SuppressWarnings("deprecation")
	public static Location singleBlock(World world, Material m) {

		Location singleBlock = null;
		List<Chunk> arrayOfChunk = Lists.newArrayList();

		if (WORLD_CHUNKS.containsKey(world.getName())) {

			arrayOfChunk = WORLD_CHUNKS.get(world.getName());

		} else {

			arrayOfChunk = MCAUtil.getChunksByMCAFiles(world);
			WORLD_CHUNKS.put(world.getName(), arrayOfChunk);

		}

		int chunk = arrayOfChunk.size();
		for (int i = 0; i < chunk; i++) {
			Chunk c = arrayOfChunk.get(i);

			ChunkSnapshot csnapshot = c.getChunkSnapshot(false, false, false);

			int blockX = csnapshot.getX() << 4;
			int blockZ = csnapshot.getZ() << 4;

			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = 0; y < world.getMaxHeight(); y++) {
						if (csnapshot.getBlockTypeId(x, y, z) == m.getId()) {
							singleBlock = new Location(world, blockX + x, y, blockZ + z);
						}
					}
				}
			}
		}

		return singleBlock;
	}

	@SuppressWarnings("deprecation")
	public static Map<Material, List<Location>> multipleMaterials(World world, Material... materials) {

		Map<Material, List<Location>> returnMap = Maps.newHashMap();
		List<Chunk> arrayOfChunk = Lists.newArrayList();

		if (WORLD_CHUNKS.containsKey(world.getName())) {

			arrayOfChunk = WORLD_CHUNKS.get(world.getName());

		} else {

			arrayOfChunk = MCAUtil.getChunksByMCAFiles(world);
			WORLD_CHUNKS.put(world.getName(), arrayOfChunk);

		}

		for (int i = 0; i < arrayOfChunk.size(); i++) {
			ChunkSnapshot csnapshot = arrayOfChunk.get(i).getChunkSnapshot(false, false, false);

			int cLocX = csnapshot.getX() << 4;
			int cLocZ = csnapshot.getZ() << 4;

			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = 0; y < world.getMaxHeight(); y++) {

						int typeId = csnapshot.getBlockTypeId(x, y, z);

						for (Material material : materials) {
							if (typeId == material.getId()) {
								List<Location> currentList = returnMap.get(material);

								if (currentList == null) {
									currentList = Lists.newArrayList();
								}

								Location blockLoc = new Location(world, cLocX + x, y, cLocZ + z);
								currentList.add(blockLoc);

								currentList.add(new Location(world, cLocX + x, y, cLocZ + z));
								returnMap.put(material, currentList);
								break;
							}
						}
					}
				}
			}
		}

		return returnMap;
	}

}
