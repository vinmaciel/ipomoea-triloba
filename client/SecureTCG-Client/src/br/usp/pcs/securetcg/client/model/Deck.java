package br.usp.pcs.securetcg.client.model;

import java.io.Serializable;
import java.util.List;

public class Deck implements Serializable {

	public Deck() { };
	
	
	private long id;
	private String name;
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
	
	public List<Card> getCards() {
		return cards;
	}
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	
	@Override
	public String toString() {
		String s =	"Deck with id=" + this.getId() + " name=" + this.getName() +
					" cards=[\n";
		
		for(Card card : this.getCards()) {
			s = s + card.getId() + "(" + card.getName() + ")\n";
		}
		
		s = s + "]";
		
		return s;
	}
	
}
