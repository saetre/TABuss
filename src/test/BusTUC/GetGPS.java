package test.BusTUC;

import test.BusTUC.R;

public class GetGPS
{
	String[] unFormatedGPSCords;
	String[][] formatedGPSCords;
	public GetGPS(String[] p_UnFormatedGPSCords)
	{
		unFormatedGPSCords = p_UnFormatedGPSCords;
        formatedGPSCords = new String[unFormatedGPSCords.length][4];
	}
	public String[][] fCords(){
	
	 for(int i=0;i<unFormatedGPSCords.length;i++)
	 {
		 String startString = unFormatedGPSCords[i];
		 String[] line = startString.split("\\,");
		 for(int y=0;y<line.length;y++)
		 {
		 formatedGPSCords[i][y] = line[y].trim();
		 
		// Log.v(TAG, line[y]);
		 }
	 }
     return formatedGPSCords;
	}
}