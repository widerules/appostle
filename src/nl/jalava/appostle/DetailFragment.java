/*
 * Copyright (C) 2014 Jeffrey Rusterholz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jalava.appostle;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class DetailFragment extends Fragment {
	private final static String TAG = "DETAIL_FRAGMENT";
	private final static String PREFS_LOCALE = "LC";
	
	private View view = null;
	private String package_name = null;
	private String langcodes[] = null; // List of language codes.
	private String curLC;              // Current language code.
	private SharedPreferences prefs;
	private ScrollView scroll;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences("DetailFragment", Context.MODE_PRIVATE);
        curLC = prefs.getString(PREFS_LOCALE, "en");

        langcodes = getResources().getStringArray(R.array.ln);
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.app_details, container, false);

        // Spinner for choosing language.
		final Spinner languageSpinner = (Spinner) view.findViewById(R.id.languagesSpinner);
		scroll = (ScrollView) view.findViewById(R.id.scrollView);
		scroll.setVisibility(View.INVISIBLE);

		// Set chosen language in spinner.
		int p = 0;
		for (String s : langcodes) {
		    if (s.equalsIgnoreCase(curLC)) {
		        break;
		    }
		    p++;
		}
		languageSpinner.setSelection(p);	

		
		// Handle chosen language.
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
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + package_name));
					//intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET|Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + package_name)));
				}
			}
		});

		// Button clicked: Open Amazon App Store or browser.
		button = (Button) view.findViewById(R.id.amazon);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + package_name));
					//intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET|Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android/" + package_name)));
				}
			}
		});

		// AppBrain button.
		button = (Button) view.findViewById(R.id.appBrain);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Opening in AppBrain: " + package_name);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.appbrain.com/app/" + package_name)));
			}
		});

		// Clicking the app icon opens the app.
		// TODO: show big icon. Use button to open app.
		ImageView open = (ImageView) view.findViewById(R.id.detail_image);
		open.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				Log.i(TAG, "Opening package " + package_name);
				Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(package_name);
				startActivity(LaunchIntent);
			}
		});

		// Open app details in browser with chosen language.
		button = (Button) view.findViewById(R.id.ViewInBrowser);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Opening in browser: " + package_name);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?" +
						"id=" + package_name +
						"&hl=" + curLC)));
			}
		});

		// App Info.
		button = (Button) view.findViewById(R.id.OpenAppInfo);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showInstalledAppDetails(view.getContext(), package_name);
			}
		});

		// Certificate info.
		button = (Button) view.findViewById(R.id.CertificateInfo);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showCertificateInfo(package_name);
			}
		});

		
	    return view;
	}

	@Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(PREFS_LOCALE, curLC);
        ed.commit();
    }    

    public void fillDetail(String app_package, String app_date) {
    	package_name = app_package;
		PackageManager pm = getView().getContext().getPackageManager();
		ApplicationInfo ai = null;
		try {
			ai = pm.getApplicationInfo(app_package, 0);
		} catch (final NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
			return;
		}

		// Get version from PackageInfo.
		PackageInfo pi;
		String version = "-";
		try {
			pi = pm.getPackageInfo(app_package, 0);
			version = pi.versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}

		// Get the app icon. Try to get the hires one.
		Drawable d;
		Intent intent = pm.getLaunchIntentForPackage(app_package);
		if ((Build.VERSION.SDK_INT > 11) && (intent != null)) {
			ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
			d = getAppIcon(resolveInfo);
		} else {
			d = pm.getApplicationIcon(ai);
		}

		ImageView image = (ImageView) view.findViewById(R.id.detail_image);
		image.setImageDrawable(d);

		// Get the app info.
		TextView name = (TextView) view.findViewById(R.id.detail_app_name);
	    name.setText(Html.fromHtml("<h3>" + pm.getApplicationLabel(ai) + "</h3>" + 
				"<h7>" + ai.packageName + "<br/>" + version + "<br/>" + app_date + "</h7>"));
		scroll.setVisibility(View.VISIBLE);
	}

    // This code is from: http://stackoverflow.com/questions/4600740/getting-app-icon-in-android
	public Drawable getFullResDefaultActivityIcon() {
		return getFullResIcon(Resources.getSystem(), android.R.mipmap.sym_def_app_icon);
    }

    public Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            ActivityManager activityManager = (ActivityManager) view.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            int iconDpi = activityManager.getLauncherLargeIconDensity();
            d = resources.getDrawableForDensity(iconId, iconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }

        return (d != null) ? d : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = view.getContext().getPackageManager().getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(ResolveInfo info) {
        return getFullResIcon(info.activityInfo);
    }

    public Drawable getFullResIcon(ActivityInfo info) {
        Resources resources;
        try {
            resources = view.getContext().getPackageManager().getResourcesForApplication(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    private Drawable getAppIcon(ResolveInfo info) {
        return getFullResIcon(info.activityInfo);
    }
    
    // This code is from: http://stackoverflow.com/questions/4421527/start-android-application-info-screen
    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

	private static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }
    
    // Code from: http://thomascannon.net/misc/android_apk_certificate/
    private void showCertificateInfo(String packageName) {
    	PackageManager pm = view.getContext().getPackageManager();
    	PackageInfo packageInfo = null;
    	
    	try {
    		packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
			return;
		}

    	Signature[] signatures = packageInfo.signatures;

    	// cert = DER encoded X.509 certificate:
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);

        CertificateFactory cf = null;
        try {
        	cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
        	Log.e(TAG, e.getMessage());
            return;
        }
        
        X509Certificate c = null;
        String certinf = null;        
        try {
        	c = (X509Certificate) cf.generateCertificate(input);
      	
            certinf = String.format(getResources().getString(R.string.certificate_info,
                      c.getSubjectDN(), 
                      c.getIssuerDN(), 
                      c.getNotBefore(), 
                      c.getNotAfter(), 
                      c.getSerialNumber().toString(),
                      c.getSigAlgName())); 
        	
        } catch (CertificateException e) {
        	// Display the error.
        	certinf = getResources().getString(R.string.certificate_error) + e.getMessage(); 
        }
        
        // Show certificate info.
        // TODO: Use dialog layout.
        Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage(certinf);
        builder.setCancelable(true);
        builder.setTitle(getResources().getString(R.string.certificate_title));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {
        		dialog.dismiss();
        	}
    	});
        AlertDialog dialog = builder.create();
        dialog.show();   	
	}
}