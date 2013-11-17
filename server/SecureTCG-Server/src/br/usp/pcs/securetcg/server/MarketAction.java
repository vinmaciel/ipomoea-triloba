package br.usp.pcs.securetcg.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.usp.pcs.securetcg.library.communication.json.MarketCardJson;
import br.usp.pcs.securetcg.library.communication.json.MarketCardSetJson;
import br.usp.pcs.securetcg.server.model.CardClass;
import br.usp.pcs.securetcg.server.model.CardClassDAO;

import com.google.gson.Gson;

public class MarketAction extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		//TODO
		MarketActionForm maForm = (MarketActionForm) form;
		
		if(request.getMethod().equals("GET")) {
			boolean isDownload = maForm.isDownloadImage();
			long cardID = maForm.getCardId();
			
			CardClassDAO classDAO = new CardClassDAO();
			
			if(isDownload) {
				//Cannot download all
				if(cardID < 0) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
				//Download image
				else {
					try {
						CardClass cardClass = classDAO.get(cardID);
						
						if(cardClass == null) {
							response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							return null;
						}
						
						String imagePath = getServlet().getServletContext().getRealPath("/") + cardClass.getBitmapPath();
						InputStream in = new FileInputStream(imagePath);
						byte[] buffer = new byte[10000000];
						int bytes = in.read(buffer);
						in.close();
						
						response.setContentType("image/jpeg");
						response.setContentLength(bytes);
						response.setStatus(HttpServletResponse.SC_OK);
						OutputStream out = response.getOutputStream();
						out.write(buffer, 0, bytes);
						out.close();
					} catch (IOException e) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					}
				}
			}
			else {
				String json = "";
				
				//Get all cards
				if(cardID < 0) {
					List<CardClass> cardClasses = classDAO.getAll();
					
					if(cardClasses == null) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						return null;
					}
					
					MarketCardJson[] cardJsons = new MarketCardJson[cardClasses.size()];
					for(int i = 0; i < cardClasses.size(); i++) {
						cardJsons[i] = new MarketCardJson();
						cardJsons[i].setId(cardClasses.get(i).getId());
						cardJsons[i].setName(cardClasses.get(i).getName());
						cardJsons[i].setDescription(cardClasses.get(i).getDescription());
					}
					
					MarketCardSetJson cardSetJson = new MarketCardSetJson();
					cardSetJson.setCardSet(cardJsons);
					json = new Gson().toJson(cardSetJson, MarketCardSetJson.class);
				}
				//Get one card
				else {
					CardClass cardClass = classDAO.get(cardID);
					
					if(cardClass == null) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						return null;
					}
					
					MarketCardJson cardJson = new MarketCardJson();
					cardJson.setId(cardClass.getId());
					cardJson.setName(cardClass.getName());
					cardJson.setDescription(cardClass.getDescription());
					json = new Gson().toJson(cardJson, MarketCardJson.class);
				}
				
				try {
					response.setContentType("application/json");
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().write(json);
				} catch (IOException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
			
			return null;
		}
		
		else {
			System.out.println("method: " + request.getMethod());
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		
		return mapping.findForward("failure");
	}
}
