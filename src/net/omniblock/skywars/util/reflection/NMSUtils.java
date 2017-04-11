package net.omniblock.skywars.util.reflection;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.EntityPlayer;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.WorldBorder;

public class NMSUtils {

	public static void sendWorldBorderPacket(Player player, int warningBlocks) {
		
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        WorldBorder playerWorldBorder = nmsPlayer.world.getWorldBorder();
        PacketPlayOutWorldBorder worldBorder = new PacketPlayOutWorldBorder(playerWorldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS);
        
        try {
        	
            Field field = worldBorder.getClass().getDeclaredField("i");
            field.setAccessible(true);
            field.setInt(worldBorder, warningBlocks);
            field.setAccessible(!field.isAccessible());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        nmsPlayer.playerConnection.sendPacket(worldBorder);
        
    }
	
}
