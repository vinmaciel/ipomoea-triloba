package br.usp.pcs.securetcg.server.test;

import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class TestAction extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		TestActionForm taForm = (TestActionForm) form;
		taForm.setMessage("Testing action form");
		
		TestClass1 obj = new TestClass1();
		obj.setName("one_name");
		obj.setData(new BigInteger("123456789").toByteArray());
		
		try{
			TestClass1DAO.add(obj);
		}
		catch(Throwable t) {
			taForm.setMessage(t.getMessage());
		}
		
		return mapping.findForward("success");
	}
}
