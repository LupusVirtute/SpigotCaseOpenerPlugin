package com.lupus.opener.messages;

import java.util.HashMap;
import java.util.Map;

public class MessageReplaceQuery {
	private final Map<String,String> queryReplacement = new HashMap<>();
	public MessageReplaceQuery addQuery(String key,String replacement) {
		queryReplacement.put(key, replacement);
		return this;
	}
	public Map<String,String> toMap(){
		return queryReplacement;
	}
}
