package br.usp.pcs.securetcg.client.model;

import java.io.Serializable;

public class Card implements Serializable {

	private static final long serialVersionUID = 8781132184531968399L;


	public Card() { };
	
	
	private long id;
	private CardClass cardClass;
	
	private byte[] serial;
	private byte[] properties;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return this.cardClass.getName();
	}
	public void setName(String name) {
		this.cardClass.setName(name);
	}

	public String getDescription() {
		return this.cardClass.getDescription();
	}
	public void setDescription(String description) {
		this.cardClass.setDescription(description);
	}

	public String getBitmapPath() {
		return this.cardClass.getBitmapPath();
	}
	public void setBitmapPath(String bitmapPath) {
		this.cardClass.setBitmapPath(bitmapPath);
	}
	
	
	public byte[] getSerial() {
		return serial;
	}
	public void setSerial(byte[] serial) {
		this.serial = serial;
	}
	
	public byte[] getProperties() {
		return properties;
	}
	public void setProperties(byte[] properties) {
		this.properties = properties;
	}
	
	public long getClassID() {
		return cardClass.getId();
	}
	public void setClassID(long classID) {
		this.cardClass.setId(classID);
	}
	
	public CardClass getCardClass() {
		return this.cardClass;
	}
	public void setCardClass(CardClass cardClass) {
		this.cardClass = cardClass;
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
