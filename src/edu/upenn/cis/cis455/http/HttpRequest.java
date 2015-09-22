package edu.upenn.cis.cis455.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/*
 GET /temp HTTP/1.1 
 Host: localhost:8080
 User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:28.0) Gecko/20100101 Firefox/28.0
 Accept: text/html,application/xhtml+xml,application/xml;q=0.9;q=0.8
 Accept-Language: en-US,en;q=0.5
 Accept-Encoding: gzip, deflate
 Connection: keep-alive 
 */
public class HttpRequest 
{
	private static final Logger logger = Logger.getLogger(HttpRequest.class);
	private String operation;
	private String protocol;
	private String version;
	private String resource;
	private Map<String,String> headers;
	private boolean validRequest;
	
	public HttpRequest(String httpRequest)
	{
		headers=new HashMap<String, String>();
		validRequest=true;
		String[] requestSplit=httpRequest.split("\n");
		String[] firstLineSplit=requestSplit[0].trim().split(" ");
		if(firstLineSplit.length<3)
		{
			logger.warn("invalid request - "+httpRequest);
			validRequest=false;
			return;
		}
		else
		{
			logger.info(requestSplit[0].trim());
			if(firstLineSplit.length<3 || firstLineSplit[2].split("/").length<2 || !firstLineSplit[2].split("/")[0].trim().equalsIgnoreCase("HTTP") || (!firstLineSplit[2].split("/")[1].trim().equalsIgnoreCase("1.0") && !firstLineSplit[2].split("/")[1].trim().equalsIgnoreCase("1.1")))
			{
				logger.warn("invalid first line in request - "+httpRequest);
				validRequest=false;
				return;
			}
			
			operation=firstLineSplit[0].trim();
			resource=firstLineSplit[1].trim();
			protocol=firstLineSplit[2].split("/")[0].trim();
			version=firstLineSplit[2].split("/")[1].trim();
		}
		for(int i=1;i<requestSplit.length;i++)
		{
			if(requestSplit[i].split(":").length<2)
			{
				logger.warn("invalid header in request - "+httpRequest);
			}
			else
			{
				logger.info(requestSplit[i]);
				String key = requestSplit[i].split(":")[0].trim().toLowerCase();
				String value = requestSplit[i].trim().split(":")[1].trim();
				if(requestSplit[i].split(":").length>2)
				{
					value=requestSplit[i].substring(requestSplit[i].indexOf(":")+1).trim();
				}
				headers.put(key, value);
			}
		}
		if(!headers.containsKey("host") && version.equalsIgnoreCase("1.1"))
		{
			//invalid http 1.1 request; respond with 400 error;
			logger.warn("HTTP 1.1 request without Host header - "+httpRequest);
			validRequest=false;
		}
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public boolean isValidRequest() {
		return validRequest;
	}

	public void setValidRequest(boolean validRequest) {
		this.validRequest = validRequest;
	}

	@Override
	public String toString() {
		return "HttpRequest [operation=" + operation + ", protocol=" + protocol
				+ ", version=" + version + ", resource=" + resource
				+ ", headers=" + headers + ", validRequest=" + validRequest
				+ "]";
	}
	
	

}
