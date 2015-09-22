package edu.upenn.cis.cis455.http;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class HTTP {
	
	private static final String protocol = "HTTP";
	private static final String version1 = "1";
	private static final String version11 = "1.1";
	private static final String KEY_100 = "100";
	private static final String KEY_200 = "200";
	private static final String KEY_300 = "300";
	private static final String KEY_304 = "304";
	private static final String KEY_400 = "400";
	private static final String KEY_403 = "403";
	private static final String KEY_404 = "404";
	private static final String KEY_405 = "405";
	private static final String KEY_412 = "412";
	private static final String KEY_500 = "500";
	private static final String KEY_POST = "POST";
	private static final String CONTENT_TYPE_KEY="Content-type";
	private static final String CONTENT_LENGTH_KEY="Content-Length";
	private static final String LAST_MODIFIED_KEY="Last-Modified";
	private static final String DATE_KEY="Date";
	private static final String CONNECTION_KEY="Connection";
	private static final String ACCEPT_KEY = "Accept";
	private static SimpleDateFormat httpDateFormat;
	private static Map<String, String> errorHeaders;
	private static Map<String, String> responseCodes;
	private static HttpResponse error100;
	private static HttpResponse error404;
	private static HttpResponse error400;
	private static HttpResponse error500;
	private static HttpResponse error300;
	private static HttpResponse error304;
	private static HttpResponse error403;
	private static HttpResponse error405;
	private static HttpResponse error412;
	private static HttpResponse errorPOST;
	
	static
	{
		httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		responseCodes = new HashMap<String, String>();
		errorHeaders = new HashMap<String, String>();
		responseCodes.put(KEY_200,"OK");
		responseCodes.put(KEY_404,"NOT FOUND");
		responseCodes.put(KEY_403,"FORBIDDEN");
		responseCodes.put(KEY_500, "SERVER ERROR");
		responseCodes.put(KEY_400, "BAD REQUEST");
		responseCodes.put(KEY_300, "REDIRECTED");
		responseCodes.put(KEY_304, "NOT MODIFIED");
		responseCodes.put(KEY_405, "METHOD NOT ALLOWED");
		responseCodes.put(KEY_412, "PRECONDITION FAILED");
		responseCodes.put(KEY_100, "CONTINUE");
		responseCodes.put(KEY_POST, "CALL NOT SUPPORTED");
		String dataPre = "<html><body>";
		String dataPost = "<br/><br/>Ankit Mishra<br/>mankit<br/></body></html>\r\n";
		//error response for 404
		String data = dataPre+KEY_404+" : "+responseCodes.get(KEY_404)+dataPost;
		errorHeaders.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
		errorHeaders.put(CONTENT_TYPE_KEY,"text/html; charset=utf-8");
		errorHeaders.put(CONTENT_LENGTH_KEY,""+data.length());
		errorHeaders.put(CONNECTION_KEY,"Close");
		error404 = new HttpResponse(protocol,version11,KEY_404,responseCodes.get(KEY_404),errorHeaders,data);
		//error response for 400
		data = dataPre+KEY_400+" : "+responseCodes.get(KEY_400)+dataPost;
		errorHeaders.put(CONTENT_LENGTH_KEY,""+data.length());
		error400 = new HttpResponse(protocol,version11,KEY_400,responseCodes.get(KEY_400),errorHeaders,data);
		//error response for 500
		data = dataPre+KEY_500+" : "+responseCodes.get(KEY_500)+dataPost;
		errorHeaders.put(CONTENT_LENGTH_KEY,""+data.length());
		error500 = new HttpResponse(protocol,version11,KEY_500,responseCodes.get(KEY_500),errorHeaders,data);
		//error response for 300
		data = dataPre+KEY_300+" : "+responseCodes.get(KEY_300)+dataPost;
		errorHeaders.put(CONTENT_LENGTH_KEY,""+data.length());
		error300 = new HttpResponse(protocol,version11,KEY_300,responseCodes.get(KEY_300),errorHeaders,data);
		//error response for POST
		data = dataPre+KEY_POST+" : "+responseCodes.get(KEY_POST)+dataPost;
		errorHeaders.put(CONTENT_LENGTH_KEY,""+data.length());
		errorPOST = new HttpResponse(protocol,version11,KEY_400,responseCodes.get(KEY_400),errorHeaders,data);
		//error response for POST
		data = dataPre+KEY_403+" : "+responseCodes.get(KEY_403)+dataPost;
		errorHeaders.put(CONTENT_LENGTH_KEY,""+data.length());
		error403 = new HttpResponse(protocol,version11,KEY_403,responseCodes.get(KEY_403),errorHeaders,data);
		//error response for 405
		data = dataPre+KEY_405+" : "+responseCodes.get(KEY_405)+dataPost;
		errorHeaders.put(CONTENT_LENGTH_KEY,""+data.length());
		errorHeaders.put(ACCEPT_KEY, "GET, HEAD");
		error405 = new HttpResponse(protocol,version11,KEY_405,responseCodes.get(KEY_405),errorHeaders,data);
		//error response for 100
		data = dataPre+KEY_100+" : "+responseCodes.get(KEY_100)+dataPost;
		errorHeaders.put(CONTENT_LENGTH_KEY,""+data.length());
		error100 = new HttpResponse(protocol,version11,KEY_100,responseCodes.get(KEY_100),errorHeaders,data);	
		//error response for 304
		data = dataPre+KEY_304+" : "+responseCodes.get(KEY_304)+dataPost;
		errorHeaders.put(CONTENT_LENGTH_KEY,""+data.length());
		error304 = new HttpResponse(protocol,version11,KEY_304,responseCodes.get(KEY_304),errorHeaders,data);	
		//error response for 412
		data = dataPre+KEY_412+" : "+responseCodes.get(KEY_412)+dataPost;
		errorHeaders.put(CONTENT_LENGTH_KEY,""+data.length());
		error412 = new HttpResponse(protocol,version11,KEY_412,responseCodes.get(KEY_412),errorHeaders,data);			
		
	}

	public static String getProtocol() {
		return protocol;
	}

	public static String getVersion1() {
		return version1;
	}

	public static String getVersion11() {
		return version11;
	}

	public static String getKey200() {
		return KEY_200;
	}

	public static String getKey300() {
		return KEY_300;
	}

	public static String getKey400() {
		return KEY_400;
	}

	public static String getKey404() {
		return KEY_404;
	}

	public static String getKey500() {
		return KEY_500;
	}

	public static String getContentTypeKey() {
		return CONTENT_TYPE_KEY;
	}

	public static String getContentLengthKey() {
		return CONTENT_LENGTH_KEY;
	}

	public static String getLastModifiedKey() {
		return LAST_MODIFIED_KEY;
	}

	public static String getDateKey() {
		return DATE_KEY;
	}

	public static String getConnectionKey() {
		return CONNECTION_KEY;
	}

	public static Map<String, String> getErrorHeaders() {
		return errorHeaders;
	}

	public static Map<String, String> getResponseCodes() {
		return responseCodes;
	}

	public static HttpResponse getError404() {
		errorHeaders.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
		return error404;
	}

	public static HttpResponse getError400() {
		errorHeaders.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
		return error400;
	}

	public static HttpResponse getError500() {
		errorHeaders.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
		return error500;
	}

	public static HttpResponse getError300() {
		errorHeaders.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
		return error300;
	}

	public static String getKeyPost() {
		return KEY_POST;
	}

	public static HttpResponse getErrorPOST() {
		errorHeaders.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
		return errorPOST;
	}

	public static String getKey403() {
		return KEY_403;
	}

	public static HttpResponse getError403() {
		errorHeaders.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
		return error403;
	}

	public static String getKey405() {
		return KEY_405;
	}

	public static String getAcceptKey() {
		return ACCEPT_KEY;
	}

	public static HttpResponse getError405() {
		errorHeaders.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
		return error405;
	}

	public static String getKey100() {
		return KEY_100;
	}
	
	public static HttpResponse getError100() {
		errorHeaders.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
		return error100;
	}

	public static String getKey304() {
		return KEY_304;
	}

	public static HttpResponse getError304() {
		errorHeaders.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
		return error304;
	}

	public static SimpleDateFormat getHttpDateFormat() {
		httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return httpDateFormat;
	}

	public static String getKey412() {
		return KEY_412;
	}

	public static HttpResponse getError412() {
		errorHeaders.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
		return error412;
	}
	
	
}
