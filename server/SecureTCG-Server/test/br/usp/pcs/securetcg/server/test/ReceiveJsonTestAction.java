package br.usp.pcs.securetcg.server.test;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.usp.pcs.securetcg.library.communication.json.WithdrawRequestJson;

import com.google.gson.Gson;

public class ReceiveJsonTestAction extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ReceiveJsonTestActionForm jsonForm = (ReceiveJsonTestActionForm) form;
		System.out.println("form: " + jsonForm.getJson());
		String inputString = null;
		try {
			InputStream in = request.getInputStream();
			byte[] buffer = new byte[1000];
			int bytes = in.read(buffer);
			inputString = new String(buffer, 0, bytes);
			System.out.println(inputString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		WithdrawRequestJson json = new Gson().fromJson(inputString, WithdrawRequestJson.class);
		if(	json.getCommitment() != null &&
			json.getPku() != null &&
			json.getTr() != null)
			response.setStatus(HttpServletResponse.SC_OK);
		
		System.out.println(json.getCardID());
		System.out.println(json.getJ());
		System.out.println(json.getPku());
		System.out.println(json.getTr());
		System.out.println(json.getCommitment());
		
		return null;
	}
}
