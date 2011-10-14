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
