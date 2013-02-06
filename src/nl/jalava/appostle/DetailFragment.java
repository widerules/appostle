package nl.jalava.appostle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class DetailFragment extends SherlockFragment {
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.app_details, container, false);
	    return view;
	  }

	  public void setText(String name) {
		  TextView view = (TextView) getView().findViewById(R.id.detail_app_name);
		    view.setText(name);
	  }
}
