package br.usp.pcs.securetcg.server;

import org.apache.struts.action.ActionForm;

public class LoginActionForm extends ActionForm {

	private String name;
	private byte[] pku;
	
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
}
