package br.usp.pcs.securetcg.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.usp.pcs.securetcg.server.model.Player;
import br.usp.pcs.securetcg.server.model.PlayerDAO;

public class LoginAction extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoginActionForm laForm = (LoginActionForm) form;
		
		if(request.getMethod().equals("POST")) {
			String name = laForm.getName();
			byte[] pku = laForm.getPku();
			if(name == null || pku == null) {
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
				return null;
			}
			
			PlayerDAO playerDAO = new PlayerDAO();
			
			Player player = new Player();
			player.setName(name);
			player.setPku(pku);
			playerDAO.add(player);
			
			response.setStatus(HttpServletResponse.SC_OK);
			return null;
		}
		else {
			System.out.println("method: " + request.getMethod());
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		
		return mapping.findForward("failure");
	}
}
