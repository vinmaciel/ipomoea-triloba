package br.usp.pcs.securetcg.client.model;

import java.io.Serializable;

public class Card implements Serializable {

	private static final long serialVersionUID = 8781132184531968399L;


	public Card() { };
	
	
	private long id;
	private String name;
	private String description;
	private String bitmapPath;
	
	private byte[] serial;
	private byte[] properties;
	private long classID;
	
	
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
		return classID;
	}
	public void setClassID(long classID) {
		this.classID = classID;
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
