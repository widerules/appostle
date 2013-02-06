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

import com.actionbarsherlock.app.SherlockActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends SherlockActivity {
	public static final String APP_NAME = "app_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	// Need to check if Activity has been switched to landscape mode
	// If yes, finish and go back to the start Activity
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
    	finish();
    	return;
    }

    //setContentView(R.layout.activity_detail);
    setContentView(R.layout.app_details);
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
        String s = extras.getString(APP_NAME);
        TextView view = (TextView) findViewById(R.id.detail_app_name);
        view.setText(s);
    }
  }
} 