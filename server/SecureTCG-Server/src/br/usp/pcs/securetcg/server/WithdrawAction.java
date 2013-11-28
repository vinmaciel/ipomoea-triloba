package br.usp.pcs.securetcg.server;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.usp.pcs.securetcg.library.clsign.CLPrivateKey;
import br.usp.pcs.securetcg.library.clsign.CLPublicKey;
import br.usp.pcs.securetcg.library.clsign.CLSignature;
import br.usp.pcs.securetcg.library.communication.json.WithdrawChallengeJson;
import br.usp.pcs.securetcg.library.communication.json.WithdrawRequestJson;
import br.usp.pcs.securetcg.library.communication.json.WithdrawSolveJson;
import br.usp.pcs.securetcg.library.communication.json.WithdrawWalletJson;
import br.usp.pcs.securetcg.library.ecash.CompactEcash;
import br.usp.pcs.securetcg.library.pedcom.PedPublicKey;

import com.google.gson.Gson;

public class WithdrawAction extends Action {
	
	private static final String SESSION_CHALLENGE = "challenge";
	private static final String SESSION_CARD_ID = "card_id";
	private static final String SESSION_WALLET_SIZE = "wallet_size";
	private static final String SESSION_COMMITMENT = "commitment";
	private static final String SESSION_PKU = "pku";
	private static final String SESSION_RANDOM_PROOF = "proof";
	
	/**
	 * Executes a request to /withdraw.do. <br/>
	 * Acceptable methods: GET <br/>
	 * Acceptable queries:
	 * <ul>
	 * <li> quest=request
	 * <li> quest=solve
	 * </ul>
	 * 
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		GenericJsonForm withdrawForm = (GenericJsonForm) form;
		
		if(request.getMethod().equals("POST")) {
			String option = request.getParameter("option");
			
			if(option.equals("request")) {
				HttpSession session = request.getSession();
				
				//Parse form
				String json = withdrawForm.getJson();
				WithdrawRequestJson requestJson = new Gson().fromJson(json, WithdrawRequestJson.class);
				session.setAttribute(SESSION_CARD_ID, new BigInteger(String.valueOf(requestJson.getCardID())));
				session.setAttribute(SESSION_WALLET_SIZE, new BigInteger(String.valueOf(requestJson.getJ())));
				session.setAttribute(SESSION_COMMITMENT, new BigInteger(requestJson.getCommitment()));
				session.setAttribute(SESSION_PKU, new BigInteger(requestJson.getPku()));
				byte[][] tr = requestJson.getTr();
				BigInteger[] randomProof = new BigInteger[tr.length];
				for(int i = 0; i < tr.length; i++)
					randomProof[i] = new BigInteger(tr[i]);
				session.setAttribute(SESSION_RANDOM_PROOF, randomProof);
				
				//Generate challenges
				BigInteger[] challenge = CompactEcash.withdraw_BankSide_Challenge();
				session.setAttribute(SESSION_CHALLENGE, challenge);
				
				byte[][] challengeBytes = new byte[challenge.length][];
				for(int i = 0; i < challenge.length; i++)
					challengeBytes[i] = challenge[i].toByteArray();
				
				//Return JSON
				WithdrawChallengeJson challengeJson = new WithdrawChallengeJson();
				challengeJson.setChallenges(challengeBytes);
				json = new Gson().toJson(challengeJson, WithdrawChallengeJson.class);
				
				try {
					response.setContentType("application/json");
					response.setStatus(HttpServletResponse.SC_ACCEPTED);
					response.getWriter().write(json);
				} catch (IOException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
				return null;
			}
			
			else if(option.equals("solve")) {
				HttpSession session = request.getSession(false);
				if(session == null) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return null;
				}
				
				//Parse form
				String json = withdrawForm.getJson();
				WithdrawSolveJson solveJson = new Gson().fromJson(json, WithdrawSolveJson.class);
				byte[][] solution = solveJson.getSr();
				BigInteger[] sr = new BigInteger[solution.length];
				for(int i = 0; i < solution.length; i++)
					sr[i] = new BigInteger(solution[i]);
				
				//Parse values
				BigInteger	_A = (BigInteger) session.getAttribute(SESSION_COMMITMENT),
							Gu = (BigInteger) session.getAttribute(SESSION_PKU),
							J = (BigInteger) session.getAttribute(SESSION_WALLET_SIZE),
							Q = (BigInteger) session.getAttribute(SESSION_CARD_ID);
				BigInteger[]	tr = (BigInteger[]) session.getAttribute(SESSION_RANDOM_PROOF),
								challenge = (BigInteger[]) session.getAttribute(SESSION_CHALLENGE);
				
				ServerParameter par = ServerParameter.get();
				CLPrivateKey sk = par.getSkb();
				CLPublicKey pk = par.getPkb();
				PedPublicKey ck = par.getPedkb();
				
				//Verify proof
				Object[] proof = CompactEcash.withdraw_BankSide_Proof(_A, Gu, J, Q, tr, sr, challenge, sk, pk, ck);
				if(proof.length == 1 && !(boolean) proof[0]) {
					response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
					return null;
				}
				else if(proof.length == 3 && (boolean) proof[0]) {
					BigInteger _s = (BigInteger) proof[1];
					CLSignature sig = (CLSignature) proof[2];
					
					WithdrawWalletJson walletJson = new WithdrawWalletJson();
					walletJson.setSignature(sig.getA());
					walletJson.setSignatureRandom(sig.getE());
					walletJson.setSerialComponent(_s.toByteArray());
					json = new Gson().toJson(walletJson, WithdrawWalletJson.class);
					
					try {
						response.setContentType("application/json");
						response.setStatus(HttpServletResponse.SC_OK);
						response.getWriter().write(json);
					} catch (IOException e) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					}
					return null;
				}
				else {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return null;
				}
				
			}
			
			else {
				System.out.println("option: " + option);
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
				return null;
			}
		}
		
		else {
			System.out.println("method: " + request.getMethod());
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		
		return mapping.findForward("failure");
	}
}
