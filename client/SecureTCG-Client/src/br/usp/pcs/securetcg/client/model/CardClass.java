package br.usp.pcs.securetcg.client.model;

import java.io.Serializable;

public class CardClass implements Serializable {

	private long id;
	private String name;
	private String description;
	private String bitmapPath;

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

	public String getBitmapPath() {
		return bitmapPath;
	}
	public void setBitmapPath(String bitmapPath) {
		this.bitmapPath = bitmapPath;
	}
	
	
	public String toString() {
		return "CardClass with id=" + this.id + " name=" + this.name +
			" description=" + this.description + " bitmap path=" + this.bitmapPath; 
	}
}
