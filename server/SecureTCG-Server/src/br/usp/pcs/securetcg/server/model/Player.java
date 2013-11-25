package br.usp.pcs.securetcg.server.model;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="player")
public class Player {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private long id;
	@Column(name="name", nullable=false)
	private String name;
	@Column(name="pku", nullable=false)
	private byte[] pku;
	
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
	
	public byte[] getPku() {
		return pku;
	}
	public void setPku(byte[] pku) {
		this.pku = pku;
	}
	
	
	@Override
	public String toString() {
		return "Player with id=" + this.id + " user=" + this.name +
				" pku=" + new BigInteger(this.pku).toString();
	}
}
