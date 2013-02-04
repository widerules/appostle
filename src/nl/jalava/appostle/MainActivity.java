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
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {
	private ListView listView1;
	private ProgressBar progress;
	private Context context;
	private PackageManager pm;
	private AppAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		context = getBaseContext();

		listView1 = (ListView)findViewById(R.id.listView1);
		progress = (ProgressBar) findViewById(R.id.progressBar);
		
		listView1.setClickable(true);
        listView1.setFastScrollEnabled(true);

        // OnClick
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					App o = (App) listView1.getItemAtPosition(position);
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + o.packageName + "&hl=en")));
				}
        });

        pm = getPackageManager();

        new UpdateAppList().execute();
	}

	private void updateApps() {
	    Vector<App> app_data = new Vector<App>(); //TODO: Use collection.

		List<PackageInfo> apps = pm.getInstalledPackages(0);

		for (PackageInfo pi: apps) {
			ApplicationInfo ai = null;
			try {
				ai = pm.getApplicationInfo(pi.packageName, 0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}

			// Only installed apps. TODO: Setting in options.
			if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				String name = (String) pm.getApplicationLabel(ai);
				
				// Date from app directory.
				String appFile = ai.sourceDir;
				//long updated = pi.lastUpdateTime; // Requires level 9.
				long updated = new File(appFile).lastModified();				

				// Localized date.
				String dateString = android.text.format.DateFormat.getDateFormat(context).format(new Date(updated));
				
				// Get app info.
				App app = new App();
				app.name = name;
				app.date = dateString;
				app.version = pi.versionName;
				app.icon = pm.getApplicationIcon(ai);
				app.packageName = pi.packageName;
				app.lastUpdateTime = updated;
				app_data.add(app);
			}
		}

		// Copy the Vector into the array.
		App[] app_data2 = new App[app_data.size()];
		app_data.copyInto(app_data2);
	    
        adapter = new AppAdapter(this, R.layout.app_row, app_data2);
        
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.options, menu);
 		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
	    switch (id) {
	    case R.id.refresh:
	    	new UpdateAppList().execute();
	    	break;
	    case R.id.menu_about:
	    	AboutDialog about = new AboutDialog(this);
	    	String title = context.getString(R.string.about) + " " + context.getString(R.string.app_name);
	    	about.setTitle(title);
	    	about.setCanceledOnTouchOutside(true);
	    	about.show();
	    	break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private class UpdateAppList extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			updateApps();
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progress.setVisibility(View.GONE);
			listView1.setAdapter(adapter);
		}
	}
}


