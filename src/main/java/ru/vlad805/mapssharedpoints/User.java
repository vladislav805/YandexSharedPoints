package ru.vlad805.mapssharedpoints;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mjson.Json;

public class User {
	public int id;
	public String login;
	public String firstName;
	public String lastName;
	public int sex;
	public boolean isOnline;
	public long lastSeen;
	public UserPhoto photo;

	public User (Json u) {
		id = u.at(Const.USER_ID).asInteger();
		login = u.at(Const.LOGIN).asString();
		firstName = u.at(Const.FIRST_NAME).asString();
		lastName = u.at(Const.LAST_NAME).asString();
		sex = u.at(Const.SEX).asInteger();
		isOnline = u.at(Const.IS_ONLINE).asBoolean();
		lastSeen = u.at(Const.LAST_SEEN).asLong();
		photo = new UserPhoto(u.at(Const.PHOTO));
	}
	
	static ArrayList<User> parse (List<Json> u) {
		ArrayList<User> list = new ArrayList<>();
		for (int i = 0, l = u.size(); i < l; ++i) {
			list.add(new User(u.get(i)));
		}
		return list;
	}

	public void editInfo (Context context, APICallback callback, HashMap<String, String> params) {
		API.invokeAPIMethod(context, Const.API.editUser, params, callback);
	}

	public void setOnline (Context context) {
		API.invokeAPIMethod(context, Const.API.setUserAsOnline, null, null);
	}
}
