package br.usp.pcs.securetcg.library.communication.json;

public class MarketCardJson {

	private long id;
	private String name;
	private String description;
	
	public MarketCardJson() { }
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
