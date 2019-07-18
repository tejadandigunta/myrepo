package com.track.triaging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class Properties {
	private HashMap<String,String> props = new HashMap<>();
	
	public Properties(String file){
		String filePath = getClass().getClassLoader().getResource(file).getFile();
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			while((line = br.readLine()) != null) {
				String[] str = line.split("=");
				props.put(str[0], str[1]);
			}
			addQeName();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addQeName() {
		String userName = props.get("user_name"); 
		props.put("QE", userName.substring(0, 1).toUpperCase() + userName.substring(1, userName.indexOf(".")));
	}
	
	public String getProperty(String propertyName) {
		return this.props.get(propertyName);
	}
	
	public boolean hasProperty(String propertyName) {
		return props.containsKey(propertyName);
	}
}