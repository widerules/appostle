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
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class MainActivity extends SherlockFragmentActivity {
	private ListView listView1; 
	private Context context;
	PackageManager pm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		listView1 = (ListView)findViewById(R.id.listView1);
			
		pm = getPackageManager();
		context = getBaseContext();
		
		updateApps();
	}
	
	private void updateApps() {
		setProgressBarIndeterminateVisibility(true);

	    Vector<App> app_data = new Vector<App>();

		List<PackageInfo> apps = pm.getInstalledPackages(0);

		int max = apps.size();
		int i = 0;
		for (PackageInfo pi: apps) {
			ApplicationInfo ai = null;
			try {
				ai = pm.getApplicationInfo(pi.packageName, 0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}

			// Only installed apps. TODO: Setting in options.
			if (ai.sourceDir.startsWith("/data/app/")) {
				String name = (String) pm.getApplicationLabel(ai);
				
				// Date from app directory.
				String appFile = ai.sourceDir;
				//long updated = pi.lastUpdateTime; // Requires level 9.
				long updated = new File(appFile).lastModified();				

				//String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(updated));
				//TODO: localize date.
				String dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(updated));
				App app = new App(0, name + " (" + pi.versionName + ")\n" + pi.packageName + "\n" + dateString);
				app.name = name;
				app.version = pi.versionName;
				app.icon = pm.getApplicationIcon(ai);
				app.packageName = pi.packageName;
				app.lastUpdateTime = updated;
				app_data.add(app);
				i++;
				if (i >= max) break;
			}
		}

		// Copy the Vector into the array.
		App[] app_data2 = new App[app_data.size()];
		app_data.copyInto(app_data2);
	    
        AppAdapter adapter = new AppAdapter(this, R.layout.app_row, app_data2);
        
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
        
        // OnClick
        listView1.setClickable(true);
        listView1.setFastScrollEnabled(true);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					App o = (App) listView1.getItemAtPosition(position);
					String pkg = o.packageName;
				    Toast.makeText(context, "Opening: " + pkg, Toast.LENGTH_SHORT).show();
					try {
					    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg)));
					} catch (android.content.ActivityNotFoundException anfe) {
					    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + pkg)));
					}
				}
        });
        
	    setProgressBarIndeterminateVisibility(false); 	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.options, menu);

		menu.add("Refresh")
        .setIcon(R.drawable.ic_refresh)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return (super.onCreateOptionsMenu(menu));
	}

}
