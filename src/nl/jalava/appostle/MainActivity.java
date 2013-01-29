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

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import nl.jalava.appostle.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ListView listView1; 
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		PackageManager pm = getPackageManager();
		List<PackageInfo> apps = pm.getInstalledPackages(0);
		context = getBaseContext();
		
		App[] app_data = new App[apps.size()];
		
		int i = 0;
		for (PackageInfo pi: apps) {
			ApplicationInfo ai = null;
			try {
				ai = pm.getApplicationInfo(pi.packageName, 0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String name = (String) pm.getApplicationLabel(ai);
			long updated = pi.lastUpdateTime;
			String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(updated));
			App app = new App(0, name + "\n" + pi.packageName + "\n" + dateString);
			app.name = name;
			app.icon = pm.getApplicationIcon(ai);
			app.packageName = pi.packageName;
			app.lastUpdateTime = updated;
			app.firstInstallTime = pi.firstInstallTime;
			app_data[i] = app;
			i++;
		}
		
        AppAdapter adapter = new AppAdapter(this, R.layout.app_row, app_data);
        
        // Sort array by date descending.
        adapter.sort(new Comparator<App>() {
        	public int compare(App app1, App app2) {
        		int comp = 1;
        		if (app1.lastUpdateTime > app2.lastUpdateTime) comp = -1;
        		return comp;
        	}
		});
        
        // OnClick
        listView1 = (ListView)findViewById(R.id.listView1);
        listView1.setClickable(true);
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
