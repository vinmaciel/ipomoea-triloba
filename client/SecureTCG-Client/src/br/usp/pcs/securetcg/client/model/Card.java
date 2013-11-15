package br.usp.pcs.securetcg.client.model;

import java.io.Serializable;

import br.usp.pcs.securetcg.library.interfaces.ICard;

public class Card extends ICard implements Serializable {

	public Card() { };
	
	
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


	@Override
	public String toString() {
		return	"Card with id=" + this.getId() + " name=" + this.getName() +
				" description=" + this.getDescription() + " cardID" + 
				" serial=" + this.getSerial() + " properties=" + this.getProperties();
	}
	
	
	@Override
	public Card clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Forbidden to clone cards");
	}
	
}
