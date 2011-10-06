package test.BusTUC;

public class GPS
{
	
	public static String[][] formatCoordinates(String[] unformattedCoordinates){	
		String[][] formatedGPSCords = new String[unformattedCoordinates.length][4];
		for(int i=0;i<unformattedCoordinates.length;i++)
		 {
			 String startString = unformattedCoordinates[i];
			 String[] line = startString.split("\\,");
			 for(int y=0;y<line.length;y++)
			 {
				 formatedGPSCords[i][y] = line[y].trim();
			 }
		 }
	     return formatedGPSCords;
	}
}