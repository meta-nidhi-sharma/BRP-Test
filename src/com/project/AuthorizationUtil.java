package com.project;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class AuthorizationUtil {

	// Configuration to be loaded from Environment variables
	private static final String BP_LOGIN_URL;
	private static final String BP_GRANT_SERVICE;
	
	private static final String BP_SECRET;
	private static final String BP_CLIENT_ID;
	private static final String BP_CLIENT_SECRET;

	static {
		
		BP_LOGIN_URL = "https://login.salesforce.com";
		BP_GRANT_SERVICE = "/services/oauth2/token?grant_type=password";
		
		BP_SECRET = "";
		BP_CLIENT_ID = "3MVG9zlTNB8o8BA2qsseRvLXsNymRfoBuf4E2Bs1ck4Oi.REI3P8L9N.D_x9g2TP5uu422Zu4sfvX8VFDag3_";
		BP_CLIENT_SECRET = "AF3254FA6202F7AE83A0229C91C3A02758506ACB920396836FD44BE5EFED027B";
	}

	/**
	 * Validate Salesforce credentials from Environment variables
	 * 
	 * @return
	 */
	private static boolean isValidConfiguration() {
		if (BP_SECRET != null 
				&& BP_CLIENT_ID != null 
				&& BP_CLIENT_SECRET != null) {
			System.out.println("BS Login URL : " + BP_LOGIN_URL);
			System.out.println("BS Client Id : " + BP_CLIENT_ID);
			return true;
		}
		return false;
	}

	/**
	 * Get Authorization token from Salesforce
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getAuthorizationToken(String bpUsername, String bpPassword) throws Exception {

		Map<String, String> authorizationDataMap = new HashMap<String, String>();
		try {
			if (isValidConfiguration()) {
				// Assemble the login request URL
				String loginURL = BP_LOGIN_URL + BP_GRANT_SERVICE + "&client_id=" + BP_CLIENT_ID + "&client_secret="
						+ BP_CLIENT_SECRET + "&username=" + bpUsername + "&password=" + (bpPassword + BP_SECRET);

				String responseData = HttpClientUtil.doLogin(loginURL);
				System.out.println("HttpResponse " + responseData.toString());
				if (responseData != null) {
					JSONObject jsonObject = null;
					try {
						jsonObject = (JSONObject) new JSONTokener(responseData).nextValue();
						authorizationDataMap.put("LOGIN_ACCESS_TOKEN", jsonObject.getString("access_token"));
						authorizationDataMap.put("LOGIN_INSTANCE_URL", jsonObject.getString("instance_url"));
					} catch (JSONException jsonException) {
						throw jsonException;
					}
				}
			} else {
				System.out.println("InValid credentials for Login !");
				throw new Exception("InValid credentials for Login !");
			}
		} catch (Exception e) {
			throw e;
		}
		return authorizationDataMap;
	}
}