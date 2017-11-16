package net.omniblock.skywars.patch.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;

import net.omniblock.skywars.Skywars;
import net.omniblock.skywars.patch.managers.MapManager.MapType;
import net.omniblock.skywars.util.DebugUtil;
import net.omniblock.skywars.util.Scan;

@SuppressWarnings("deprecation")
public class LobbySchematic {

	private MapType maptype;

	public static final String path = "SWWL.schematic";
	public static final String pathZ = "SWZWL.schematic";

	public static EditSession SELECTED_SESSION;
	public static Location SELECTED_LOCATION;

	public static Map<EditSession, CuboidClipboard> LOBBYSCHEMATIC_NORMAL;
	public static Map<EditSession, CuboidClipboard> LOBBYSCHEMATIC_Z;

	public static Location LOCATION_LOBBYSCHEMATIC_NORMAL;
	public static Location LOCATION_LOBBYSCHEMATIC_Z;

	public LobbySchematic() {
		return;
	}

	public void scanAndPasteLobbySchematic(World world, MapType mt) {

		List<Location> matchedBlocks = Scan.oneMaterial(world, Material.BEDROCK);
		
		if (matchedBlocks != null) {

			for(Location loc : matchedBlocks) {
				
				Block bl = loc.getBlock();

				if (bl.getRelative(0, 1, 0).getType() == Material.WOOD_PLATE) {

					if (mt == MapType.NORMAL) {

						bl.setType(Material.AIR);
						bl.getRelative(0, 1, 0).setType(Material.AIR);

						LOCATION_LOBBYSCHEMATIC_NORMAL = loc;
						LOBBYSCHEMATIC_NORMAL = paste(LOCATION_LOBBYSCHEMATIC_NORMAL.toVector(), world, mt);
						
						
					} else if (mt == MapType.Z) {

						bl.setType(Material.AIR);
						bl.getRelative(0, 1, 0).setType(Material.AIR);

						LOCATION_LOBBYSCHEMATIC_Z = loc;
						LOBBYSCHEMATIC_Z = paste(LOCATION_LOBBYSCHEMATIC_Z.toVector(), world, mt);

					}

					bl.getRelative(0, 1, 0).setType(Material.AIR);
					return;

				}
				
			}

		} else {

			DebugUtil.debugSevere("ERROR - El mapa: " + world.getName()
					+ " no tiene la (bedrock + placa de madera) para definir el lobby!");
			return;

		}

	}

	public void selectMapType(MapType mt) {
		switch (mt) {

		case NORMAL:

			SELECTED_LOCATION = LOCATION_LOBBYSCHEMATIC_NORMAL;

			for (Map.Entry<EditSession, CuboidClipboard> k : LOBBYSCHEMATIC_NORMAL.entrySet()) {

				SELECTED_SESSION = k.getKey();
				break;

			}

			break;

		case Z:

			SELECTED_LOCATION = LOCATION_LOBBYSCHEMATIC_Z;

			for (Map.Entry<EditSession, CuboidClipboard> k : LOBBYSCHEMATIC_Z.entrySet()) {

				SELECTED_SESSION = k.getKey();
				break;

			}

			break;

		case UNKNOWN:
			break;

		}
	}

	public void removePasted() {

		if (SELECTED_SESSION != null) {

			SELECTED_SESSION.undo(SELECTED_SESSION);

		}

	}

	public Map<EditSession, CuboidClipboard> paste(Vector loc, World world, MapType mt) {

		Map<EditSession, CuboidClipboard> map = new HashMap<EditSession, CuboidClipboard>();
		File schematic = new File(Skywars.getInstance().getDataFolder(), "/data/schems/" + getPath(mt));

		if (schematic != null) {

			try {
				WorldEditPlugin we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

				EditSession session = we.getWorldEdit().getEditSessionFactory()
						.getEditSession((LocalWorld) new BukkitWorld(world), 10000000);
				CuboidClipboard cc = MCEditSchematicFormat.getFormat(schematic).load(schematic);

				cc.paste(session, new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ()), false);

				map.put(session, cc);
				return map;
			} catch (MaxChangedBlocksException | DataException | IOException e) {
				e.printStackTrace();
			}

		}

		return null;

	}

	public Location getLocation() {

		return SELECTED_LOCATION;

	}

	public String getPath(MapType mt) {

		if (mt == MapType.NORMAL) {
			return path;
		}

		return pathZ;

	}

	public MapType getMaptype() {
		return maptype;
	}

	public void setMaptype(MapType maptype) {
		this.maptype = maptype;
	}

}
