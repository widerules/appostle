package nl.jalava.appostle;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class DetailFragment extends SherlockFragment {
	private View view = null;
	private String package_name = null;
	private String curLC;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.app_details, container, false);

		// Get current language code.
		curLC = "en";
        if (savedInstanceState != null) {
        	curLC = savedInstanceState.getString("curLC"); // Use API level 1
        }  		
		
		final Spinner languages = (Spinner) view.findViewById(R.id.languagesSpinner); 
		final String langs[] = getResources().getStringArray(R.array.ln); // Language codes.

		// Set chosen language in selector.
		int p = 0;
		for (String s : langs) {
		    if (s.equalsIgnoreCase(curLC)) {
		        break;
		    }
		    p++;
		}
		languages.setSelection(p);
		
		// Button clicked: open Play Store or browser.
		Button button = (Button) view.findViewById(R.id.ViewInPlayStoreButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + package_name)));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + package_name)));
				}
			}
		});

		// Launch the app.
		ImageView open = (ImageView) view.findViewById(R.id.detail_image);
		open.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				Toast.makeText(context, "Opening: " + package_name, Toast.LENGTH_SHORT).show();
				Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(package_name);
				startActivity(LaunchIntent);
			}
		});

		// Open app details in browser with chosen language.
		button = (Button) view.findViewById(R.id.ViewInBrowser);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int pos = languages.getSelectedItemPosition();
				curLC = langs[pos];
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?" +
						"id=" + package_name +
						"&hl=" + curLC)));
			}
		});
		
	    return view;
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("curLC", curLC);
    }	
	
	public void fillDetail(String app_package) {
		package_name = app_package;
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
