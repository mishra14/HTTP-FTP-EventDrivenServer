package edu.upenn.cis.cis455.http;

import java.util.Map;

public class HttpResponse 
{
	private String protocol;
	private String version;
	private String responseCode;
	private String responseCodeString;
	private Map<String, String> headers;
	private String data;
	
	public HttpResponse(String protocol, String version, String responseCode,
			String responseCodeString, Map<String, String> headers, String data) {
		super();
		this.protocol = protocol;
		this.version = version;
		this.responseCode = responseCode;
		this.responseCodeString = responseCodeString;
		this.headers = headers;
		this.data = data;
	}
	
	public HttpResponse(String protocol, String version, String responseCode,
			String responseCodeString, Map<String, String> headers) {
		super();
		this.protocol = protocol;
		this.version = version;
		this.responseCode = responseCode;
		this.responseCodeString = responseCodeString;
		this.headers = headers;
	}
	
	public String getResponseString()
	{
		StringBuilder response = new StringBuilder();
		response.append(protocol+"/"+version+" "+responseCode+" "+responseCodeString+"\r\n");
		if(headers!=null)
		{
			for(Map.Entry<String, String> header : headers.entrySet())
			{
				response.append(header.getKey()+":"+header.getValue()+"\r\n");
			}
		}
		response.append("\r\n");
		if(data!=null)
		{
			response.append(data);
		}
		return response.toString();
	}
	
	public String getResponseStringHeadersOnly()
	{
		StringBuilder response = new StringBuilder();
		response.append(protocol+"/"+version+" "+responseCode+" "+responseCodeString+"\r\n");
		if(headers!=null)
		{
			for(Map.Entry<String, String> header : headers.entrySet())
			{
				response.append(header.getKey()+":"+header.getValue()+"\r\n");
			}
		}
		response.append("\r\n");
		return response.toString();
	}
	
	@Override
	public String toString() {
		return "HttpResponse [protocol=" + protocol + ", version=" + version
				+ ", responseCode=" + responseCode + ", responseCodeString="
				+ responseCodeString + ", headers=" + headers + "]";
	}
}
