package br.usp.pcs.securetcg.client.model;

import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;

public class Deck implements Serializable {

	private static final long serialVersionUID = 3619538977583658174L;


	public Deck() { };
	
	
	private long id;
	private String name;
	private String description;
	private List<Card> cards;
	
	
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
	
	public List<Card> getCards() {
		return cards;
	}
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	public int getCardsSize() {
		return cards.size();
	}
	
	
	@Override
	public String toString() {
		String s =	"Deck with id=" + this.getId() + " name=" + this.getName() + 
					" description=" + this.getDescription() + " with cards=[";
		
		for(ListIterator<Card> iterator = (ListIterator<Card>) cards.iterator(); iterator.hasNext(); ) {
			s += iterator.next().getName();
			if(iterator.hasNext()) s += ", ";
		}
		s += "]\n";
		
		return s;
	}
	
}
