package com.bspindler.clickgame;


/**
 * Simple demonstrative response wrapper class, we'll build on this class as our 
 * response becomes more involved. 
 * 
 * @author bspindler
 *
 */
public class Response {
	
	String response;

	/**
	 * Object value as String
	 *  such as: <code>objectMapper.writeValueAsString(Object);</code>
	 * @param valueAsString
	 */
	public Response(String valueAsString) {
		this.response = valueAsString;
	}

	public String getResponse() {
		return this.response;
	}
}
