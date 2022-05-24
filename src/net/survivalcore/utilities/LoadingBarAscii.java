package net.survivalcore.utilities;
/**
 * generates an ascii loading bar with a specified number of characters
 * @author wolfm
 */
public class LoadingBarAscii {
	public static String getLoadingBar(int percentage, int length){
		percentage = Math.min(percentage,100);
		String retString = getColor(percentage);
		if(percentage<100){
			for(int i =0 ;i <length;i++){
				if(percentage>(100/length)*i){
					retString+="█";
				}else{
					retString+="§7▒";
				}
			}
		}else{
			retString+="Completed";
		}
		return retString + getColor(percentage)+" "+ percentage + "%";
	}
	private static String getColor(int percentage){
		if(percentage==100){
			return "§2";
		}
		if(percentage>=75){
			return "§a";
		}
		if(percentage>=50){
			return "§e";
		}
		if(percentage>=25){
			return "§6";
		}
		return "§c";
	}

}
