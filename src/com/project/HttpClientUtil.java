package com.project;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
	
	/**
	 * Execute the login POST request
	 * @param loginURL
	 * @return
	 * @throws Exception 
	 */
	public String doLogin(final String loginURL) throws Exception {
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(loginURL);
		String responseStr = null;
		
		try {
			HttpResponse response = httpclient.execute(httpPost);
			// verify response is HTTP OK
			final int statusCode = response.getStatusLine().getStatusCode();
			System.out.print(response);
			if (statusCode != HttpStatus.SC_OK) {
				throw new Exception("Blackpurl Login failed with statusCode : " + statusCode);
			} else {
				responseStr = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			// release connection
			httpPost.releaseConnection();
		}
		return responseStr;
	}

}

