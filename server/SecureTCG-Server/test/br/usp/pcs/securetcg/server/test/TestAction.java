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
			if(message != null && !message.equals("")) {
				TestClass1 one = new TestClass1();
				one.setName(message);
				one.setData(BigInteger.valueOf(Calendar.getInstance().getTimeInMillis()).toByteArray());
				one.setId(oneDAO.add(one));
				System.out.println("added one: " + one.getId());
				
				one = oneDAO.get(taForm.getSearchId());
				
				if(id > 0) {
					TestClass2 two = new TestClass2();
					two.setDescription(message);
					two.setTestClassOne(one);
					two.setId(twoDAO.add(two));
					
					System.out.println("added two: " + two.getId());
					
					one = oneDAO.get(one.getId());
					one.setName(one.getName() + " " + two.getDescription());
					oneDAO.update(one);
					
					System.out.println("updated one: " + one.getId());
				}
			}
			else {
				TestClass1 one = oneDAO.get(id);
				if(one != null) {
					oneDAO.delete(one);
					System.out.println("deleted one: " + one.getId());
				}
			}
			
			taForm.setMessage(oneDAO.getAll().toString()); 
		}
		catch(Throwable t) {
			taForm.setMessage(t.getMessage());
		}
		
		return mapping.findForward("success");
	}
}
