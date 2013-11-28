package br.usp.pcs.securetcg.client.market;

import android.os.Handler;
import android.os.Message;
import br.usp.pcs.securetcg.library.ecash.model.Wallet;

public abstract class WithdrawHandler extends Handler {

	protected static final int STATE_START = 1;
	protected static final int STATE_REQUEST_FAILED = 2;
	protected static final int STATE_SOLUTION_FAILED = 3;
	protected static final int STATE_DONE = 4;
	
	public WithdrawHandler() {
		super();
	}
	
	public abstract void onStart();
	
	public abstract void onRequestFailed();
	
	public abstract void onSolutionFailed();
	
	public abstract void onWithdraw(Wallet wallet);
	
	@Override
	public void handleMessage(Message message) {
		switch(message.what) {
		case STATE_START:
			onStart();
			break;
		case STATE_REQUEST_FAILED:
			onRequestFailed();
			break;
		case STATE_SOLUTION_FAILED:
			onSolutionFailed();
			break;
		case STATE_DONE:
			onWithdraw((Wallet) message.obj);
			break;
		
		default:
			super.handleMessage(message);
		}
	}
}
