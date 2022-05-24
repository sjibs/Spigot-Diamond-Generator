package net.survivalcore.diamondgenerator;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin{
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(new DiamondGeneratorListener(this), this);
		new DiamondGeneratorCommands(this);
		DiamondGenerator.init(this);
		UpgradeShard.init(this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				DiamondGenerator.addDiamonds();
			}
		}, 0L, 200L);//set to 200
		}
}
