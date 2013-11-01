package br.usp.pcs.securetcg.client.model;

import java.io.Serializable;

import br.usp.pcs.securetcg.library.interfaces.ICard;

public class Card extends ICard implements Serializable {

	public Card() { };
	
	
	private String name;
	private String description;
	
	
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
	
	
	@Override
	public String toString() {
		return	"Card with id=" + this.getId() + " name=" + this.getName() +
				" description=" + this.getDescription() + " signature=" + this.getSignature();
	}
	
	
	@Override
	public Card clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Forbidden to clone cards");
	}
	
}
