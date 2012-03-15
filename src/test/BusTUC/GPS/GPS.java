/**
 * Copyright (C) 2010-2012 Magnus Raaum, Lars Moland Eliassen, Christoffer Jun Marcussen, Rune Sætre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * README:
 * 
 */

package test.BusTUC.GPS;

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
				 //System.out.println(i + formatedGPSCords[i][y]);
			 }
		 }
		
	     return formatedGPSCords;
	}
}