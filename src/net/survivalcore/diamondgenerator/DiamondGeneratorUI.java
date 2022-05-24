package net.survivalcore.diamondgenerator;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DiamondGeneratorUI implements InventoryHolder{
	static ItemStack infobook;
	static ItemStack loadingbar1;
	static ItemStack loadingbar2;
	static ItemStack loadingbar3;
	static ItemStack loadingbar4;
	static ItemStack loadingbar5;
	static ItemStack loadingbar6;
	static ItemStack loadingbar7;
	static ItemStack rightbar;
	static ItemStack blank;
	private DiamondGenerator dg;
	Inventory i;
	static {
		infobook = new ItemStack(Material.BOOK);
		ItemMeta bookMeta = getCustomModelMeta(infobook,2);
		bookMeta.setDisplayName("§b§lDiamond Generator");
		bookMeta.setLore(Arrays.asList(
				"§7Generates a diamond every 1000 seconds.",
				"§7Right click with an upgrade shard to",
				"§7upgrade the generator.",
				"§7More Information can be found on the wiki:",
				"§b§nsuicide-ws.fandom.com"));
		infobook.setItemMeta(bookMeta);
		loadingbar1 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		loadingbar1.setItemMeta(getCustomModelMeta(loadingbar1,1));
		loadingbar2 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		loadingbar2.setItemMeta(getCustomModelMeta(loadingbar2,2));
		loadingbar3 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		loadingbar3.setItemMeta(getCustomModelMeta(loadingbar1,3));
		loadingbar4 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		loadingbar4.setItemMeta(getCustomModelMeta(loadingbar4,4));
		loadingbar5 = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		loadingbar5.setItemMeta(getCustomModelMeta(loadingbar5,5));
		loadingbar6 = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		loadingbar6.setItemMeta(getCustomModelMeta(loadingbar6,6));
		loadingbar7 = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		loadingbar7.setItemMeta(getCustomModelMeta(loadingbar7,7));
		rightbar = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
		rightbar.setItemMeta(getCustomModelMeta(rightbar,2));
		blank = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
		blank.setItemMeta(getCustomModelMeta(blank,1));	
	}
	public DiamondGeneratorUI(DiamondGenerator dg){
		this.dg = dg;
		i = Bukkit.createInventory(this, 27,"§8§nLevel "+dg.level+" Diamond Generator");
		for(int j = 9 ;j<27;j++){
			i.setItem(j, blank);
		}
		i.setItem(0, infobook);
		i.setItem(13,null);
		setLoadingBar((int) ((dg.inventory/(double) dg.level)*100)+((dg.dgtimer%100)/dg.level));
		i.setItem(8, rightbar);

	}
	
	public void setLoadingBar(int percentage){
		int divs = (int) (percentage*0.07d);
		
		for(int j = 0;j<7;j++){
			if(j<divs){
				i.setItem(j+1, loadingbar1);
			}else if(j>divs){
				i.setItem(j+1, loadingbar7);
			}else{
				int rem = 6-(int)(((percentage*0.07d)-j)*7)%7;
				switch(rem){
				case 0: i.setItem(j+1, loadingbar1); break;
				case 1: i.setItem(j+1, loadingbar2); break;
				case 2: i.setItem(j+1, loadingbar3); break;
				case 3: i.setItem(j+1, loadingbar4); break;
				case 4: i.setItem(j+1, loadingbar5); break;
				case 5: i.setItem(j+1, loadingbar6); break;
				case 6: i.setItem(j+1, loadingbar7); break;
				}
			}
		}
		if(i.getItem(13)==null||i.getItem(13).getType().equals(Material.AIR)){
			if(dg.inventory>0){
				i.setItem(13,new ItemStack(Material.DIAMOND,dg.inventory));
			}else{
				i.setItem(13,null);
			}
		}
	}
	private static ItemMeta getCustomModelMeta(ItemStack is, int customModelData){
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("§r");
		im.setCustomModelData(customModelData);
		return im;
	}
	@Override
	public Inventory getInventory() {
		return i;
	}
	public void close(){
		if(i.getViewers().size()<=1){
			dg.uiInventory=null;
			if(i.getItem(13)!=null &&!i.getItem(13).getType().equals(Material.AIR)){
				if(!i.getItem(13).getType().equals(Material.DIAMOND)){
						i.getViewers().get(0).getInventory().addItem(i.getItem(13));
				}else{
						dg.inventory = i.getItem(13).getAmount();
						dg.updateLoadingBarArmorStand((int) ((dg.inventory/(double) dg.level)*100)+((dg.dgtimer%100)/dg.level));
				}
				
			}
		}
	}
	public void onClick(InventoryClickEvent e){
		if(e.getClickedInventory().getHolder().equals(this) &&e.getSlot()!=13) {e.setCancelled(true);return;}
		if(e.getSlot()==13 && !e.getClick().isLeftClick()){e.setCancelled(true);return;}
		if(dg.inventory==0 && e.getClick().isShiftClick()){e.setCancelled(true);return;}
		if(e.getSlot()==13 && dg.inventory==0) {e.setCancelled(true);return;}
		if(e.getSlot()==13 && (e.getCurrentItem()==null ||e.getCurrentItem().getType().equals(Material.AIR))) {e.setCancelled(true);return;}
		if(e.getSlot()==13&&dg.inventory>0){
				dg.dgtimer = 0;
				dg.inventory =0;
		}
		
	}
	

}
