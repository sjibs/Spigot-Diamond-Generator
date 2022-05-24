package net.survivalcore.diamondgenerator;

import org.bukkit.entity.Player;

public class DGMessage {
	final static String normalMessage = "§b§lDiamond Generator §7» ";
	final static String errorMessage = "§c§lError §7» ";
	public static void sendMessage(Player p, String message){
		p.sendMessage(normalMessage+message);
	}
	public static void sendError(Player p, String message){
		p.sendMessage(errorMessage+message);
	}
}
