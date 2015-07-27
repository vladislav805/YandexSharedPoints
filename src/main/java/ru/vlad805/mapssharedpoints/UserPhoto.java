package ru.vlad805.mapssharedpoints;

import mjson.Json;

public class UserPhoto {
	
	public int photoId;
	public int ownerId;
	public long date;
	
	public String photo50;
	public String photo200;
	public String photoOriginal;
	
	UserPhoto (Json p) {
		this.ownerId = p.at(Const.USER_ID).asInteger();
		this.photoId = p.at(Const.PHOTO_ID).asInteger();
		this.date = p.at(Const.DATE).asLong();
		this.photo50 = p.at(Const.PHOTO_50).asString();
		this.photo200 = p.at(Const.PHOTO_200).asString();
		this.photoOriginal = p.at(Const.PHOTO_ORIGINAL).asString();
	}
}
