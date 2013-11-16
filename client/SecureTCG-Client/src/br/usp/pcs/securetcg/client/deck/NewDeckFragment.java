package br.usp.pcs.securetcg.client.deck;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import br.usp.pcs.securetcg.client.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NewDeckFragment extends Fragment {

	/* UI Objects */
	private Button createButton;
	private Button cancelButton;
	private EditText nameInput;
	private EditText descriptionInput;
	
	/* Callback */
	private NewDeckInterface callback;
	
	/* Life-cycle methods */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.deck_manager_create, container, false);
		
		getObjects(fragmentView);
		setObjects();
		
		return fragmentView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callback = ((DeckManagerActivity) activity).newDeckListener;
	}
	
	/* UI methods */
	private void getObjects(View view) {
		createButton = (Button) view.findViewById(R.id.deck_manager_create_submit);
		cancelButton = (Button) view.findViewById(R.id.deck_manager_create_cancel);
		nameInput = (EditText) view.findViewById(R.id.deck_manager_create_name);
		descriptionInput = (EditText) view.findViewById(R.id.deck_manager_create_description);
	}
	
	private void setObjects() {
		createButton.setOnClickListener(onClickCreate());
		cancelButton.setOnClickListener(onClickCancel());
	}
	
	/* OnClick Listeners */
	private View.OnClickListener onClickCreate() {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = nameInput.getText().toString();
				String description = descriptionInput.getText().toString();
				callback.submitCreation(name, description);
			}
		};
	}
	
	private View.OnClickListener onClickCancel() {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				callback.cancelCreation();
			}
		};
	}
	
	/* Callback interface */
	protected interface NewDeckInterface {
		void cancelCreation();
		void submitCreation(String name, String description);
	}
}
