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

import android.graphics.drawable.Drawable;

public class App {
	public Drawable icon;
	public String text;
	public String name;
	public String version;
	public String packageName;
	long lastUpdateTime;
	long firstInstallTime;
	
	public App(int image, String text){
		super();
		this.text = text;
		this.name = "Unknown Name";
		this.version = "0.0";
		this.packageName = "com.example";
		this.lastUpdateTime = 0;
		this.firstInstallTime = 0;
	}
}
