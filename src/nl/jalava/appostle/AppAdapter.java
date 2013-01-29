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
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new AppHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.app_image);
            holder.txtTitle = (TextView)row.findViewById(R.id.app_text);
            row.setTag(holder);
        }
        else
        {
            holder = (AppHolder)row.getTag();
        }
        
        App app = data[position];
        holder.txtTitle.setText(app.text);
        holder.imgIcon.setImageDrawable(app.icon);

        return row;
    }
    
    static class AppHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }	
}
