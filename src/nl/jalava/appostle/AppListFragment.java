/*
 * Copyright (C) 2013 Jeffrey Rusterholz
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

import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

public class AppListFragment extends SherlockFragment {
	final static String TAG = "APPLIST";
	private final static String PREFS_APP_TYPE = "APP_TYPE";

	// Listener.
	private OnItemSelectedListener listener;

	// Interface for communication with listener.
	public interface OnItemSelectedListener {
		public void onAppSelected(App app);
	}

	private ListView appList;
	private ProgressBar progress;
	private TextView progress_loading;
	private Context context;
	private PackageManager pm;
	private AppAdapter adapter;
	private int curType;              // Current app type.
	private SharedPreferences prefs;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences("AppListFragment", Context.MODE_PRIVATE);
        curType = prefs.getInt(PREFS_APP_TYPE, 0);
    }	
	
    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt(PREFS_APP_TYPE, curType);
        ed.commit();
    }  
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.app_list, container, false);
		appList = (ListView) view.findViewById(R.id.listView1);
		progress = (ProgressBar) view.findViewById(R.id.progressBar);
		progress_loading = (TextView) view.findViewById(R.id.progress_loading);
	    context = view.getContext();

		// Use the action bar.
		setHasOptionsMenu(true);
		setRetainInstance(true);

		// Add drop down list with app types to the actionbar.
		ActionBar bar = getSherlockActivity().getSupportActionBar();
		ArrayAdapter<CharSequence> apptype = ArrayAdapter.createFromResource(bar.getThemedContext(), R.array.app_types, R.layout.sherlock_spinner_item);
		apptype.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		bar.setListNavigationCallbacks(apptype, new ActionBar.OnNavigationListener() {
			// Update app list with chosen app type.
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				curType = itemPosition;
			    new UpdateAppList().execute();
				return false;
			}
		});
		bar.setSelectedNavigationItem(curType);

		appList.setClickable(true);
	    appList.setFastScrollEnabled(true);

	    // OnClick
	    appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					App o = (App) appList.getItemAtPosition(position);
					listener.onAppSelected(o);
				}
	    });

	    pm = context.getPackageManager();
	
	    new UpdateAppList().execute();

	    return view;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
	    switch (id) {
	    case R.id.refresh:
		    new UpdateAppList().execute();
	    	break;
    	default:
    		return super.onOptionsItemSelected(item);
	    }
	    return true;
	}
	
	@SuppressLint("NewApi")
	private void updateApps() {

		List<PackageInfo> apps = pm.getInstalledPackages(0);
	    Vector<App> app_data = new Vector<App>();
	    
		for (PackageInfo pi: apps) {
			ApplicationInfo ai = null;
			try {
				ai = pm.getApplicationInfo(pi.packageName, 0);
			} catch (NameNotFoundException e) {
				continue;
			}

			// Which apps?
			switch (curType) {
			case 0:	if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue; // Skip system apps.
					break;
			case 1: if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0) continue; // Skip installed apps.
			        break;
			}

			String name = (String) pm.getApplicationLabel(ai);
			
			String appFile = ai.sourceDir;

			// Get last updated date.
			long updated;
			if (Build.VERSION.SDK_INT >= 9) {
				updated = pi.lastUpdateTime;
			} else {
				// Date from app directory.
				updated = new File(appFile).lastModified();
			}

			// Localized date.
			String dateString = android.text.format.DateFormat.getDateFormat(context).format(new Date(updated));
			
			// Get app info.
			App app = new App();
			app.name = name;
			app.date = dateString;
			app.version = pi.versionName;
			app.packageName = pi.packageName;
			app.lastUpdateTime = updated;
			app_data.add(app);
		}
		 
		// Copy the Vector into the array.
		App[] app_data2 = new App[app_data.size()];
		app_data.copyInto(app_data2);
	    
        adapter = new AppAdapter(context, R.layout.app_row, app_data2);

        // Sort array by date descending.
        adapter.sort(new Comparator<App>() {
        	public int compare(App app1, App app2) {
        		int comp = 0;
        		if (app1.lastUpdateTime > app2.lastUpdateTime) {
        			comp = -1;
        		} else if (app1.lastUpdateTime < app2.lastUpdateTime) {
        			comp = 1;
        		}
        		return comp;
        	}
		});
	}
	
	public class UpdateAppList extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			updateApps();
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
			progress_loading.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progress.setVisibility(View.GONE);
			progress_loading.setVisibility(View.GONE);
			appList.setAdapter(adapter);

			// Fill the detail view if available.
			FragmentManager fm = getFragmentManager();
			if (fm != null) {
				DetailFragment det = (DetailFragment) fm.findFragmentById(R.id.app_detail);
				if (det != null && det.isVisible()) {
					det.fillDetail(adapter.data[0].packageName); // TODO: check data.
				}
			}
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	    if (activity instanceof OnItemSelectedListener) {
	    	listener = (OnItemSelectedListener) activity;
	    } else {
	    	throw new ClassCastException(activity.toString()
    			+ " must implement AppListFragment.OnItemSelectedListener");
	    }
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}	
}


