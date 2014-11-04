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

import android.graphics.drawable.Drawable;

public class App {
	public Drawable icon;
	public String name;
	public String date;
	public String version;
	public String packageName;
	long lastUpdateTime;
	
	public App(){
		super();
		this.name = "";
		this.version = "";
		this.packageName = "";
		this.date = "";
		this.lastUpdateTime = 0;
	}
}
