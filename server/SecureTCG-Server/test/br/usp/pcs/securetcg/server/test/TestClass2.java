package br.usp.pcs.securetcg.server.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Class to test hibernate utility
 * 
 * @author vinmaciel
 */
@Entity
@Table(name="test_two")
public class TestClass2 {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private long id;
	@ManyToOne
	@JoinColumn(name="test_one_id")
	private TestClass1 testClassOne;
	@Column(name="description")
	private String description;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public TestClass1 getTestClassOne() {
		return testClassOne;
	}
	public void setTestClassOne(TestClass1 testClassOne) {
		this.testClassOne = testClassOne;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
