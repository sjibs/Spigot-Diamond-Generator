package net.survivalcore.diamondgenerator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.google.gson.annotations.Expose;

public class SerialisableBlockLocation {
    @Expose
	private int x;
    @Expose
    private int y;
    @Expose
    private int z;
    @Expose
    private String world;
    SerialisableBlockLocation(){}
	SerialisableBlockLocation(int x, int y, int z, String world){
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	public boolean isBukkitLocation(Location l){
		if(x == l.getBlockX()&& y == l.getBlockY()&& z == l.getBlockZ() && l.getWorld().getName().equals(this.world)){
			return true;
		}
		return false;
	}
	public boolean equals(SerialisableBlockLocation sb){
		if(sb.x != this.x) return false;
		if(sb.y != this.y) return false;
		if(sb.z != this.z) return false;
		if(!sb.world.equals(this.world)) return false;
		return true;
	}
	
	public Location getBukkitLocation(){
		return new Location(Bukkit.getWorld(world),x,y,z);
	}
}
