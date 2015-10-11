package com.bspindler.clickgame;

import java.util.Map;

/**
 * An arbitrary source of data, in this case, a Click 
 * @author bspindler
 *
 */
public class Click {

	// id: generated on server and so should not be defined
	//     any value set will be ignored
	String id;
	// source: some arbitrary source identifier, required by client.
	String source;
	// contents: key/value pairs of data
	Map<String, Object> data;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
