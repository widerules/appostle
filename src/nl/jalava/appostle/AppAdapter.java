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

import nl.jalava.appostle.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AppAdapter extends ArrayAdapter<App> {
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
        final String pkg;        

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new AppHolder();
            holder.image = (ImageView)row.findViewById(R.id.app_image);
            holder.name = (TextView)row.findViewById(R.id.app_name);
            holder.date = (TextView)row.findViewById(R.id.app_date);
            row.setTag(holder);
        }
        else
        {
        	holder = (AppHolder)row.getTag();
        }

        pkg = app.packageName;
        
        // Play button clicked; launch Play Store.
        row.findViewById(R.id.playButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Google Play package: " + pkg, Toast.LENGTH_SHORT).show();
				try {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg)));
				} catch (android.content.ActivityNotFoundException anfe) {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + pkg)));
				}
			}
    	});

        holder.name.setText(app.name);
        holder.date.setText(app.date + " (" + app.version + ")");
        try {
			app.icon = context.getPackageManager().getApplicationIcon(app.packageName);
		} catch (NameNotFoundException e) {
			app.icon = null;
		}
        holder.image.setImageDrawable(app.icon);
       
        return row;
    }
    
    static class AppHolder
    {
        TextView name;
        TextView date;
        ImageView image;
    }	
}
