package br.usp.pcs.securetcg.server.test;

import org.apache.struts.action.ActionForm;

public class TestActionForm extends ActionForm {

	private static final long serialVersionUID = 7029173289136228087L;
	
	private String message;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
