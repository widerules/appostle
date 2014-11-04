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
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends ArrayAdapter<App> {
	private final static String TAG = "APP_ADAPTER";
	
	Context context;
	int layoutResourceId;
	App data[] = null;
	
	public AppAdapter(Context context, int layoutResourceId, App[] data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
	}
	
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AppHolder holder = null;
        App app = data[position];

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new AppHolder();
            holder.image = (ImageView)row.findViewById(R.id.app_image);
            holder.info = (TextView)row.findViewById(R.id.app_name);
            row.setTag(holder);
        }
        else
        {
        	holder = (AppHolder) row.getTag();
        }

        // App name, update date and version.
        holder.info.setText(Html.fromHtml("<h3>" + app.name + "</h3>" +
        		  "<h7>" + app.date + "</h7>"));
        		  //"<h7>" + app.date + "\u00A0(" + app.version +")</h7>"));
        try {
			app.icon = context.getPackageManager().getApplicationIcon(app.packageName);
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
			app.icon = null;
		}
        holder.image.setImageDrawable(app.icon);
       
        return row;
    }
    
    static class AppHolder
    {
        TextView info;
        ImageView image;
    }	
}
