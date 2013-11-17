package br.usp.pcs.securetcg.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="class")
public class CardClass {

	@Id
	@Column(name="id")
	private long id;
	@Column(name="name")
	private String name;
	@Column(name="description")
	private String description;
	@Column(name="bitmap_path")
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
		return "Card with id=" + this.id + " name=" + this.name +
				" description=" + this.description + " bitmap path=" + this.bitmapPath;
	}
}
