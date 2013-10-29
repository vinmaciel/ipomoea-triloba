package br.usp.pcs.securetcg.server.test;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Class to test hibernate utility
 * 
 * @author vinmaciel
 */
@Entity()
@Table(name="test_one")
public class TestClass1 {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private long id;
	@Column(name="name")
	private String name;
	@Column(name="data")
	private byte[] data;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="testClassOne", fetch=FetchType.LAZY)
	private List<TestClass2> twos;
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
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public List<TestClass2> getTwos() {
		return twos;
	}
	public void setTwos(List<TestClass2> twos) {
		this.twos = twos;
	}
	
	@Override
	public String toString() {
		return "" + this.getId() + this.getName();
	}
	
}
