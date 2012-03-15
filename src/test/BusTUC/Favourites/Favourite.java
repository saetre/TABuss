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

package test.BusTUC.Favourites;

public class Favourite
{
	private String query;
	private String identifier;
	
	public Favourite(String query, String identifier)
	{
		this.query = query;
		this.identifier = identifier;
	}
	
	public Favourite()
	{
		
	}
	
	public String getQuery()
	{
		return query;
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
	
	public void setQuery(String newQuery)
	{
		query = newQuery;
	}
	
	public void setIdentifier(String newIdentifier)
	{
		identifier = newIdentifier;
	}

}
