package br.usp.pcs.securetcg.server.test;

import org.apache.struts.action.ActionForm;

public class TestActionForm extends ActionForm {
	
	private static final long serialVersionUID = -1391212956225863034L;
	
	private String message;
	private long searchId;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getSearchId() {
		return searchId;
	}
	public void setSearchId(long searchId) {
		this.searchId = searchId;
	}
	
}
