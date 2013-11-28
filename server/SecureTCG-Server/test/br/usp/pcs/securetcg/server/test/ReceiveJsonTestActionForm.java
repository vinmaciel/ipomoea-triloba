package br.usp.pcs.securetcg.server.test;

import org.apache.struts.action.ActionForm;

public class ReceiveJsonTestActionForm extends ActionForm {

	private String json;

	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
}
