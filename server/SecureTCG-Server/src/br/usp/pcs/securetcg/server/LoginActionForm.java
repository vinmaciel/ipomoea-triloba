package br.usp.pcs.securetcg.server;

import org.apache.struts.action.ActionForm;

public class LoginActionForm extends ActionForm {

	private String name;
	private String pku;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPku() {
		return pku;
	}
	public void setPku(String pku) {
		this.pku = pku;
	}
}
