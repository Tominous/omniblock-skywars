package net.omniblock.skywars.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import net.minecraft.server.v1_8_R3.RegionFile;
import net.minecraft.server.v1_8_R3.RegionFileCache;

public class MCAUtil {

	public static List<Chunk> getChunksByMCAFiles(World world) {

		/**
		 * 
		 * @NuevaVersion ->
		 * 
		 */

		List<Chunk> chunks = new ArrayList<Chunk>() {

			private static final long serialVersionUID = -9118074939819844584L;

			{

				chunkLoader(world, true);

				for (Chunk c : world.getLoadedChunks()) {
					add(c);
				}

			}

		};

		return chunks;

	}

	private static void chunkLoader(World world, boolean save) {

		final Pattern regionPattern = Pattern.compile("r\\.([0-9-]+)\\.([0-9-]+)\\.mca");

		File worldDir = new File(Bukkit.getWorldContainer(), world.getName());
		File regionDir = new File(worldDir, "region");

		File[] regionFiles = regionDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return regionPattern.matcher(name).matches();

			}

		});

		for (File f : regionFiles) {

			RegionFile regionfile = null;
			Matcher matcher = regionPattern.matcher(f.getName());

			if (!matcher.matches()) {
				continue;
			}

			if (save) {
				regionfile = getRegionFile(f, regionDir);
			}

			int mcaX = Integer.parseInt(matcher.group(1));
			int mcaZ = Integer.parseInt(matcher.group(2));

			for (int cx = 0; cx < 32; cx++) {
				for (int cz = 0; cz < 32; cz++) {

					if (save) {
						if (regionfile.chunkExists((mcaX << 5) + cx, (mcaZ << 5) + cz)) {

							world.loadChunk((mcaX << 5) + cx, (mcaZ << 5) + cz, false);

						}
					} else {

						world.loadChunk((mcaX << 5) + cx, (mcaZ << 5) + cz, false);

					}

				}
			}
		}

	}

	private static RegionFile getRegionFile(File file, File regionDir) {

		RegionFile resultRegionFile = null;

		RegionFile regionfile = (RegionFile) RegionFileCache.a.get(file);

		if (regionfile != null) {
			resultRegionFile = regionfile;
		} else {
			if (!regionDir.exists()) {
				regionDir.mkdirs();
			}

			if (RegionFileCache.a.size() >= 256) {
				RegionFileCache.a();
			}

			RegionFile regionfile1 = new RegionFile(file);

			RegionFileCache.a.put(file, regionfile1);
			resultRegionFile = regionfile1;
		}

		return resultRegionFile;

	}

}
