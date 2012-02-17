package org.me.five_stones_project.internet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 *
 * @author Tangl Andras
 */

public class WebService {
	private static final String URL = "http://tanglandras.appspot.com/fivestones";
	
	public static String executeRequest(String path, Map<String, String> params) throws Exception {
		HttpGet request = new HttpGet(URL + path + buildQueryParams(params));
		request.addHeader("Accept", "text/plain");
		
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpResponse response = client.execute(request);
		
		if(response.getStatusLine().getStatusCode() != 200)
			throw new Exception("Server unavailable!");
		
		HttpEntity entity = response.getEntity();
		InputStream instream = entity.getContent();
		StringBuilder sb =  new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(instream));
		
		String line = "";
		while((line = br.readLine()) != null)
            sb.append(line);
		
		instream.close();
		
		return sb.toString();
	}
	
	private static String buildQueryParams(Map<String, String> params) {
		if(params == null)
			return "";
		String ret = "?";
		Iterator<String> it = params.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			ret += key + "=" + params.get(key) + "&";
		}
		return ret.substring(0, ret.length() - 1);
	}
}
