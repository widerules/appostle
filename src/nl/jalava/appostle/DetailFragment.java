package nl.jalava.appostle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class DetailFragment extends SherlockFragment {
	private View view = null;
	private String package_name = null;
	private String langcodes[] = null; // List of language codes.
	private String curLC;              // Current language code.
	private SharedPreferences prefs;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences("DetailFragment", Context.MODE_PRIVATE);
        curLC = prefs.getString("lc", "en");
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.app_details, container, false);

        // Spinner for choosing language.
		final Spinner languageSpinner = (Spinner) view.findViewById(R.id.languagesSpinner);
		langcodes = getResources().getStringArray(R.array.ln);

		// Set chosen language in spinner.
		int p = 0;
		for (String s : langcodes) {
		    if (s.equalsIgnoreCase(curLC)) {
		        break;
		    }
		    p++;
		}
		languageSpinner.setSelection(p);	
		
		// Handle choosen language.
		languageSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
				int pos = parent.getSelectedItemPosition();
				curLC = langcodes[pos];				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		

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
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?" +
						"id=" + package_name +
						"&hl=" + curLC)));
			}
		});
		
	    return view;
	}

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("lc", curLC);
        ed.commit();
    }    

    public void fillDetail(String app_package) {
		package_name = app_package;
		PackageManager pm = getView().getContext().getPackageManager();
		ApplicationInfo ai = null;
		try {
			ai = pm.getApplicationInfo(app_package, 0);
		} catch (final NameNotFoundException e) {
			return;
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
