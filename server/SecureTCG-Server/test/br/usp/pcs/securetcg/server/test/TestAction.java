package br.usp.pcs.securetcg.server.test;

import java.math.BigInteger;
import java.util.Calendar;

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
		String message = taForm.getMessage();
		long id = taForm.getSearchId();
		System.out.println("id: " + id + " message: " + message);

		TestClass1DAO oneDAO = new TestClass1DAO();
		TestClass2DAO twoDAO = new TestClass2DAO();
		
		try{
			TestClass1 one = new TestClass1();
			one.setName(message);
			one.setData(BigInteger.valueOf(Calendar.getInstance().getTimeInMillis()).toByteArray());
			System.out.println("added one: " + oneDAO.add(one));
			
			one = oneDAO.get(taForm.getSearchId());
			taForm.setMessage(new BigInteger(one.getData()).toString());
			
			if(id > 0) {
				TestClass2 two = new TestClass2();
				two.setDescription(message);
				two.setTestClassOne(one);
				
				System.out.println("added two: " + twoDAO.add(two));
			}
		}
		catch(Throwable t) {
			taForm.setMessage(t.getMessage());
		}
		
		return mapping.findForward("success");
	}
}
