package com.ptrprograms.daydream.models;

/**
 * Created by PaulTR on 1/29/14.
 */
public class Quote {
	private String json_class;
	private String quote;
	private String link;
	private String source;

	public void setJson_class( String json_class ) {
		this.json_class = json_class;
	}

	public void setQuote( String quote ) {
		this.quote = quote;
	}

	public void setLink( String link ) {
		this.link = link;
	}

	public void setSource( String source ) {
		this.source = source;
	}

	public String getJson_class() {
		return json_class;
	}

	public String getQuote() {
		return quote;
	}

	public String getLink() {
		return link;
	}

	public String getSource() {
		return source;
	}
}
