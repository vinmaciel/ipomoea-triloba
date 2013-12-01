package br.usp.pcs.securetcg.client.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import br.usp.pcs.securetcg.library.ecash.model.Coin;

public class Card extends Coin implements Serializable {

	private static final long serialVersionUID = 8781132184531968399L;


	public Card() { };
	
	
	private long id;
	private CardClass cardClass;
	
	private List<CardProperty> properties;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public List<CardProperty> getProperties() {
		return properties;
	}
	public CardProperty getProperty(int index) {
		return properties.get(index);
	}
	@Override
	@SuppressWarnings("unchecked")
	public void setProperties(List<?> properties) {
		this.properties = (List<CardProperty>) properties;
	}
	public void addProperty(CardProperty property) {
		if(this.properties == null)
			this.properties = new LinkedList<CardProperty>();
		this.properties.add(property);
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
