package ru.vlad805.mapssharedpoints;


import mjson.Json;

public class APIError {
	private int errorId;
	private String errorInfo;
	
	public APIError (Json j) {
		if (j.has(Const.RESPONSE))
			j = j.at(Const.RESPONSE);
		errorId = j.at(Const.ERROR_ID).asInteger();
		errorInfo = j.at("errorInfo").asString();
	}
	public String getString () {
		return "Ошибка API: " + errorId + ": " + errorInfo;
	}

	public int getErrorId() {
		return errorId;
	}

	public String getErrorInfo() {
		return errorInfo;
	}
}
