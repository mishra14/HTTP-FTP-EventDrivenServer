package edu.upenn.cis.cis455.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.upenn.cis.cis455.webserver.HttpServer;

public class HttpHandler 
{
	public static final Logger logger = Logger.getLogger(HttpHandler.class);
	private static final String CONTENT_TYPE_KEY="Content-type";
	private static final String CONTENT_LENGTH_KEY="Content-Length";
	private static final String DATE_KEY="Date";
	private static final String CONNECTION_KEY="Connection";
	
	public static byte[] handleRequest(HttpRequest httpRequest) throws IOException
	{

		byte[] result = null;
		HttpResponse httpResponse;
		if(httpRequest==null)
		{
			logger.info("null Http Request ");
			// respond with error code 400
			result = (HTTP.getError400().getResponseString().getBytes());
		}
		else if(!httpRequest.isValidRequest())
		{
			logger.info("Invalid http request - "+httpRequest.getResource());
			// respond with error code 400
			result = (HTTP.getError400().getResponseString().getBytes());
		}
		else
		{
			//deal with an Expect header
			if(httpRequest.getHeaders().containsKey("expect"))
			{
				//respond with a 100 error code
				result = (HTTP.getError100().getResponseString().getBytes());
			}
			//dead with absolute urls
			if(httpRequest.getResource().contains("http://localhost:"+HttpServer.getPort()))
			{
				logger.info("Absolute url in GET request - "+httpRequest.getResource());
				httpRequest.setResource(httpRequest.getResource().substring(("http://localhost:"+HttpServer.getPort()).length()));
			}
			Map<String, String> headers=new HashMap<String, String>();
			String data = "";
			String protocol = HTTP.getProtocol();
			String version = HTTP.getVersion11();
			String responseCode = "200";
			String responseCodeString = HTTP.getResponseCodes().get(responseCode);
			logger.info(httpRequest);
			logger.info("Requesting - "+HttpServer.getHomeDirectory().concat(httpRequest.getResource()));
			File resourceFile=new File(HttpServer.getHomeDirectory().concat(httpRequest.getResource()));
			if(resourceFile.exists())
			{
				if(!resourceFile.canRead()) //403 if the file is not readable
				{
					logger.warn("User accessing non readable file - "+resourceFile.getAbsolutePath());
					result = (HTTP.getError403().getResponseString().getBytes());
				}
				//valid file request
				else if(resourceFile.isDirectory())
				{
					//send the list of all the files inside
					File[] filesInDirectory = resourceFile.listFiles();
					StringBuilder dataBuilder=new StringBuilder();
					dataBuilder.append("<html><body>"+httpRequest.getResource()+"<br/>");
					logger.info(HttpServer.getHomeDirectory());
					for(File file : filesInDirectory)
					{
						if(!file.getName().endsWith("~"))
							dataBuilder.append("<a href=\"http://localhost:"+HttpServer.getPort()+httpRequest.getResource()+"/"+file.getName()+"\">"+file.getName()+"</a><br/>");
					}
					dataBuilder.append("</body></html>");
					data=dataBuilder.toString();
					headers.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));																						
					headers.put(CONTENT_TYPE_KEY,"text/html; charset=utf-8");
					headers.put(CONTENT_LENGTH_KEY,""+data.length());
					headers.put(CONNECTION_KEY,"Close");
					httpResponse = new HttpResponse(protocol, version, responseCode, responseCodeString, headers, data);
					if(httpRequest.getOperation().equalsIgnoreCase("GET"))
					{
						result = (httpResponse.getResponseString().getBytes());
					}
					else if(httpRequest.getOperation().equalsIgnoreCase("HEAD"))
					{
						result = (httpResponse.getResponseStringHeadersOnly().getBytes());
					}
					else if(httpRequest.getOperation().equalsIgnoreCase("POST"))
					{
						result = (HTTP.getError405().getResponseString().getBytes());
					}
					else
					{
						logger.info("Unknown operation in request - "+httpRequest.getResource());
						result = (HTTP.getError400().getResponseString().getBytes());
					}
					logger.info(httpResponse.toString());
					logger.info(httpResponse.getResponseString());
				}
				else if(resourceFile.isFile())
				{

					//send the file
					FileInputStream fis = new FileInputStream(resourceFile);
					byte[] bytes = new byte[(int) resourceFile.length()];
					if(fis.read(bytes, 0, bytes.length)!=resourceFile.length())
					{
						//did not read the file completely; send back an error code 500
						logger.warn("Length error while reading file - "+resourceFile.getAbsolutePath());
						result = (HTTP.getError500().getResponseString().getBytes());
						
					}
					else
					{
						//read the file correctly; send the file over
						if(Files.probeContentType(resourceFile.toPath())==null)
						{
							//no content type; send an error code 500
							logger.warn("Error in detecting file type on file- "+resourceFile.getAbsolutePath());
							result = (HTTP.getError500().getResponseString().getBytes());
						}
						else
						{
							if(httpRequest.getHeaders().containsKey("if-modified-since") || httpRequest.getHeaders().containsKey("if-unmodified-since"))
							{
								logger.info("if-modified or if-unmodified header detected");
								Calendar ifModifiedDate = new GregorianCalendar();
								try {
									ifModifiedDate.setTime(httpRequest.getHeaders().containsKey("if-modified-since")?HTTP.getHttpDateFormat().parse(httpRequest.getHeaders().get("if-modified-since")):HTTP.getHttpDateFormat().parse(httpRequest.getHeaders().get("if-unmodified-since")));
								} catch (ParseException e) {
									logger.error("ParseException while parsing if-modified-date  ",e);
									result = (HTTP.getError500().getResponseString().getBytes());
								}
								Calendar fileModifiedDate=new GregorianCalendar();
								fileModifiedDate.setTimeInMillis(resourceFile.lastModified());
								logger.info("File modified Date - "+HTTP.getHttpDateFormat().format(fileModifiedDate.getTime()));
								logger.info("If modified Date - "+HTTP.getHttpDateFormat().format(ifModifiedDate.getTime()));
								if(fileModifiedDate.after(ifModifiedDate))
								{
									logger.info("File modified since if modified date");
								}
								else
								{
									logger.info("File not modified since if modified date");
								}
								if(!fileModifiedDate.after(ifModifiedDate) && httpRequest.getHeaders().containsKey("if-modified-since"))
								{
									logger.info("Requesting a non modified file through if-modified - "+resourceFile.getAbsolutePath());
									//send a 304 error code
									result = (HTTP.getError304().getResponseString().getBytes());
								}
								else if(fileModifiedDate.after(ifModifiedDate) && httpRequest.getHeaders().containsKey("if-unmodified-since"))
								{
									logger.info("Requesting a modified file through if-unmodified - "+resourceFile.getAbsolutePath());
									//send a 412 error code
									result = (HTTP.getError412().getResponseString().getBytes());
								}
								else
								{
									data=new String(bytes);
									logger.info(bytes);
									headers.put(CONTENT_TYPE_KEY,Files.probeContentType(resourceFile.toPath())+"; charset=utf-8");
									headers.put(CONTENT_LENGTH_KEY,""+data.length());
									headers.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));													
									httpResponse = new HttpResponse(protocol, version, responseCode, responseCodeString, headers, data);
									
									//logger.info(httpResponse.toString());
									//logger.info(httpResponse.getResponseString());
									if(httpRequest.getOperation().equalsIgnoreCase("GET"))
									{
										result = new byte[httpResponse.getResponseStringHeadersOnly().getBytes().length + bytes.length];
										System.arraycopy(httpResponse.getResponseStringHeadersOnly().getBytes(), 0, result, 0, httpResponse.getResponseStringHeadersOnly().getBytes().length);
										System.arraycopy(bytes, 0, result, httpResponse.getResponseStringHeadersOnly().getBytes().length, bytes.length);
									}
									else if(httpRequest.getOperation().equalsIgnoreCase("HEAD"))
									{
										result = (httpResponse.getResponseStringHeadersOnly().getBytes());
									}
									else if(httpRequest.getOperation().equalsIgnoreCase("POST"))
									{
										result = (HTTP.getError405().getResponseString().getBytes());
									}
									else
									{
										logger.info("Unknown operation in request - "+httpRequest.getResource());
										result = (HTTP.getError400().getResponseString().getBytes());
									}
									logger.info(bytes.toString());
								}
							}
							else
							{
								data=new String(bytes);
								logger.info(bytes);
								headers.put(CONTENT_TYPE_KEY,Files.probeContentType(resourceFile.toPath())+"; charset=utf-8");
								headers.put(CONTENT_LENGTH_KEY,""+data.length());
								headers.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));													
								httpResponse = new HttpResponse(protocol, version, responseCode, responseCodeString, headers, data);
								//logger.info(httpResponse.toString());
								//logger.info(httpResponse.getResponseString());
								if(httpRequest.getOperation().equalsIgnoreCase("GET"))
								{
									result = new byte[httpResponse.getResponseStringHeadersOnly().getBytes().length + bytes.length];
									System.arraycopy(httpResponse.getResponseStringHeadersOnly().getBytes(), 0, result, 0, httpResponse.getResponseStringHeadersOnly().getBytes().length);
									System.arraycopy(bytes, 0, result, httpResponse.getResponseStringHeadersOnly().getBytes().length, bytes.length);
								}
								else if(httpRequest.getOperation().equalsIgnoreCase("HEAD"))
								{
									result = (httpResponse.getResponseStringHeadersOnly().getBytes());
								}
								else if(httpRequest.getOperation().equalsIgnoreCase("POST"))
								{
									result = (HTTP.getError405().getResponseString().getBytes());
								}
								else
								{
									logger.info("Unknown operation in request - "+httpRequest.getResource());
									result = (HTTP.getError400().getResponseString().getBytes());
								}
								logger.info(bytes.toString());
							}

							
						}
					}
					fis.close();
				}
				else
				{
					logger.warn("requested file is neither a file nor a directory - "+resourceFile.getAbsolutePath());
					//requested data is neither file nor directory; send 400 error code
					return (HTTP.getError400().getResponseString().getBytes());
				}
			}
			else
			{
				
				if(httpRequest.getResource().equalsIgnoreCase("/control"))
				{
					StringBuilder dataBuilder=new StringBuilder();
					dataBuilder.append("<html><body>"+httpRequest.getResource()+"<br/>Ankit Mishra<br/>mankit<br/><br/>");
					dataBuilder.append("</body></html>");
					data=dataBuilder.toString();
					headers.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));													
					headers.put(CONTENT_TYPE_KEY,"text/html; charset=utf-8");
					headers.put(CONTENT_LENGTH_KEY,""+data.length());
					headers.put(CONNECTION_KEY,"Close");
					httpResponse = new HttpResponse(protocol, version, responseCode, responseCodeString, headers, data);
					if(httpRequest.getOperation().equalsIgnoreCase("GET"))
					{
						result = (httpResponse.getResponseString().getBytes());
					}
					else if(httpRequest.getOperation().equalsIgnoreCase("HEAD"))
					{
						result = (httpResponse.getResponseStringHeadersOnly().getBytes());
					}
					else if(httpRequest.getOperation().equalsIgnoreCase("POST"))
					{
						result = (HTTP.getError405().getResponseString().getBytes());
					}
					else
					{
						logger.info("Unknown operation in request - "+httpRequest.getResource());
						result = (HTTP.getError400().getResponseString().getBytes());
					}
					logger.info(httpResponse.toString());
					logger.info(httpResponse.getResponseString());

				}
				else if(httpRequest.getResource().equalsIgnoreCase("/shutdown"))
				{
					HttpServer.setRun(false);
					StringBuilder dataBuilder=new StringBuilder();
					dataBuilder.append("<html><body>"+httpRequest.getResource()+"<br/>Ankit Mishra<br/>mankit<br/><br/>");
					dataBuilder.append("This page has started the server shutdown <br/>");
					dataBuilder.append("</body></html>");
					data=dataBuilder.toString();
					headers.put(DATE_KEY, HTTP.getHttpDateFormat().format(new GregorianCalendar().getTime()));
					headers.put(CONTENT_TYPE_KEY,"text/html; charset=utf-8");
					headers.put(CONTENT_LENGTH_KEY,""+data.length());
					headers.put(CONNECTION_KEY,"Close");
					httpResponse = new HttpResponse(protocol, version, responseCode, responseCodeString, headers, data);
					if(httpRequest.getOperation().equalsIgnoreCase("GET"))
					{
						result = (httpResponse.getResponseString().getBytes());
					}
					else if(httpRequest.getOperation().equalsIgnoreCase("HEAD"))
					{
						result = (httpResponse.getResponseStringHeadersOnly().getBytes());
					}
					else if(httpRequest.getOperation().equalsIgnoreCase("POST"))
					{
						result = (HTTP.getError405().getResponseString().getBytes());
					}
					else
					{
						logger.info("Unknown operation in request - "+httpRequest.getResource());
						result = (HTTP.getError400().getResponseString().getBytes());
					}
					logger.info(httpResponse.toString());
					logger.info(httpResponse.getResponseString());
				}
				else
				{
					logger.warn("requested file does not exist - "+resourceFile.getAbsolutePath());
					//invalid request; respond with 404
					result = (HTTP.getError404().getResponseString().getBytes());
				}
			}
		}
		return result;
	}
}
