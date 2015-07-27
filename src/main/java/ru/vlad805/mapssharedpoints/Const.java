package ru.vlad805.mapssharedpoints;

final public class Const {
	final public static String SHARED_PREFERENCES = "vlad805symp";
	final public static String SHARED_DATA = "data";
	final public static String SHARED_USER = "_userInfo";
	final public static String V = "v";
	final public static String USER_ACCESS_TOKEN = "userAccessToken";
	final public static String USER_ID = "userId";
	final public static String USER_IDS = "userIds";
	final public static String LOGIN = "login";
	final public static String PASSWORD = "password";
	final public static String OWNER_ID = "ownerId";
	final public static String POINT_ID = "pointId";
	final public static String PHOTO_ID = "photoId";
	final public static String ZOOM = "zoom";
	final public static String LATITUDE = "latitude";
	final public static String LONGITUDE = "longitude";
	final public static String TITLE = "title";
	final public static String DESCRIPTION = "description";
	final public static String DATE_CREATED = "dateCreated";
	final public static String DATE_UPDATED = "dateUpdated";
	final public static String DATE_VISIT = "dateVisit";
	final public static String IS_PUBLIC = "isPublic";
	final public static String IS_VISIT = "isVisit";
	final public static String IS_VISITED = "isVisited";
	final public static String ACCESS_CODE = "accessCode";
	final public static String COLOR_ID = "colorId";
	final public static String ERROR_ID = "errorId";
	final public static String ITEMS = "items";
	final public static String FIRST_NAME = "firstName";
	final public static String LAST_NAME = "lastName";
	final public static String SEX = "sex";
	final public static String IS_ONLINE = "isOnline";
	final public static String LAST_SEEN = "lastSeen";
	final public static String PHOTO = "photo";
	final public static String DATE = "date";
	final public static String PHOTO_50 = "photo_50";
	final public static String PHOTO_200 = "photo_200";
	final public static String PHOTO_ORIGINAL = "photo_original";
	final public static String APPLICATION_ID = "applicationId";
	final public static String BUILD_ID = "buildId";
	final public static String IS_OLD = "isOld";
	final public static String VERSION = "version";
	final public static String DOWNLOAD = "download";
	final public static String LINK = "link";
	final public static String SIZE = "size";
	final public static String RESPONSE = "response";
	final public static String RESULT = "result";
	final public static String LIST_OPEN_POINT_ID = "_openPointFromList";
	final public static String IS_NOT_OWNER = "_notOwner";

	final class API {
		final public static String login = "online.login";
		final public static String logout = "online.logout";
		final public static String registerUser = "online.registerUser";
		final public static String editUser = "online.editInfo";
		final public static String getUsers = "online.getUsers";
		final public static String setUserAsOnline = "online.setUserAsOnline";
		final public static String getPoints = "online.getPoints";
		final public static String addPoint = "online.addPoint";
		final public static String editPoint = "online.editPoint";
		final public static String setVisitPoint = "online.setVisitPoint";
		final public static String copyPoint = "online.copyPoint";
		final public static String sharePoint = "online.sharePoint";
		final public static String deletePoint = "online.deletePoint";
		final public static String checkUpdates = "apps.checkUpdates";
	}

	final class Settings {
		final public static String IS_NIGHT = "s_night";
		final public static String IS_TRAFFIC = "s_traffic";
		final public static String IS_LOCATION = "s_location";
		final public static String IS_HD = "s_hd";
		final public static String TAB_PROFILE = "_tabProfile";
	}

	final public static String IS_VK = "_isVK";
}
