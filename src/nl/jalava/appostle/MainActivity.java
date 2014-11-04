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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity implements AppListFragment.OnItemSelectedListener {
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_applist);
		context = getBaseContext();
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

	@Override
	public void onAppSelected(App app) {
	    DetailFragment fragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.app_detail);
        if (fragment != null && fragment.isInLayout()) {
        	fragment.fillDetail(app.packageName, app.date);
        } else {
        	Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        	Bundle extra = new Bundle();
        	extra.putString(DetailActivity.PACKAGE_NAME, app.packageName);
        	extra.putString(DetailActivity.PACKAGE_DATE, app.date);     	
        	intent.putExtras(extra);
        	startActivity(intent);
        }
	}
}


