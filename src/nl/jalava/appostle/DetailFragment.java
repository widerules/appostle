package nl.jalava.appostle;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class DetailFragment extends SherlockFragment {
	private View view = null;

	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    view = inflater.inflate(R.layout.app_details, container, false);
	    return view;
	  }

	  public void fillDetail(String app_package) {
		  PackageManager pm = view.getContext().getPackageManager();
		  ApplicationInfo ai = null;
		  try {
			  ai = pm.getApplicationInfo(app_package, 0);
		  } catch (final NameNotFoundException e) {
			  ai = null;
		  }

		  // Get version from PackageInfo.
		  PackageInfo pi;
		  String version = "-";
		  try {
			  pi = pm.getPackageInfo(app_package, 0);
			  version = pi.versionName;
		  } catch (NameNotFoundException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
			
		  TextView name = (TextView) view.findViewById(R.id.detail_app_name);
		  ImageView image = (ImageView) view.findViewById(R.id.detail_image);
		  image.setImageDrawable(pm.getApplicationIcon(ai));
		  name.setText(pm.getApplicationLabel(ai) + "\n" + version + "\n" + ai.packageName); 
	  }
}
