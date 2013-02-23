package fr.pcreations.labs.RESTDroid.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.util.Log;


/**
 * <b>Holder class to handle HTTP request</b>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.5
 *
 */
public class HttpRequestHandler {

	/**
	 * Constant use to store response status code
	 */
	public static final String STATUS_CODE_KEY = "com.pcreations.restclient.HttpRequestHandler.STATUS_CODE";
	
	/**
	 * Constant use to store the response
	 */
	public static final String RESPONSE_KEY = "com.pcreations.restclient.HttpRequestHandler.RESPONSE";
	private static final int URI_SYNTAX_EXCEPTION = 1;
	private static final int CLIENT_PROTOCOL_EXCEPTION = 2;
	private static final int IO_EXCEPTION = 3;
	private static final int UNKNOWN_HOST_EXCEPTION = 4;
	private static final int MALFORMED_URL_EXCEPTION = 5;
	private static final int UNKNOWN_SERVICE_EXCEPTION = 6;
	private static final int CONNECT_TIMEOUT_EXCEPTION = 7;
	private static final int SOCKET_TIMEOUT_EXCEPTION = 8;
	private static final int TIMEOUT_CONNECTION = 10000;
	private static final int TIMEOUT_SOCKET = 10000;
	
	/**
	 * Processor callback. This callback is fired when the request is finished
	 * 
	 * @see ProcessorCallback
	 */
	private ProcessorCallback mProcessorCallback;
	
	/**
	 * HashMap to store {@link HTTPContainer} corresponding to {@link RESTRequest}
	 * 
	 * <p>
	 * <ul>
	 * <li><b>key</b> : the ID of the request</li>
	 * <li><b>value</b> : the {@link HTTPContainer} instance</li>
	 * </ul>
	 * </p>
	 */
	private HashMap<UUID, HTTPContainer> httpRequests;
	
	/**
	 * Constructor
	 */
	public HttpRequestHandler() {
		httpRequests = new HashMap<UUID, HTTPContainer>();
	}
	
	/**
	 * Prepares {@link HTTPContainer} and executes a HTTP GET request
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 * 
	 * @see HttpRequestHandler#processRequest(RESTRequest)
	 */
	public void get(RESTRequest<ResourceRepresentation<?>> r) {
		try {
			httpRequests.put(r.getID(), new HTTPContainer(new HttpGet(), new URI(r.getUrl()), r.getHeaders()));
			processRequest(r);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mProcessorCallback.callAction(URI_SYNTAX_EXCEPTION, r, null);
		}
	}
	
	/**
	 * Prepares {@link HTTPContainer} and executes a HTTP POST request
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 * 
	 * @param holder
	 * 		InputStream holding post data
	 * 
	 * @see HttpRequestHandler#processRequest(RESTRequest, InputStream)
	 */
	public void post(RESTRequest<ResourceRepresentation<?>> r, InputStream holder) {
		try {
			httpRequests.put(r.getID(), new HTTPContainer(new HttpPost(r.getUrl()), new URI(r.getUrl()), r.getHeaders()));
			processRequest(r, holder);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mProcessorCallback.callAction(URI_SYNTAX_EXCEPTION, r, null);
		}
	}
	
	/**
	 * Prepares {@link HTTPContainer} and executes a HTTP PUT request
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 * 
	 * @param holder
	 * 		InputStream holding post data
	 * 
	 * @see HttpRequestHandler#processRequest(RESTRequest, InputStream)
	 */
	public void put(RESTRequest<ResourceRepresentation<?>> r, InputStream holder) {
		try {
			httpRequests.put(r.getID(), new HTTPContainer(new HttpPut(r.getUrl()), new URI(r.getUrl()), r.getHeaders()));
			processRequest(r, holder);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mProcessorCallback.callAction(URI_SYNTAX_EXCEPTION, r, null);
		}
		
	}
	
	/**
	 * Prepares {@link HTTPContainer} and executes a HTTP DELETE request
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 * 
	 * @see HttpRequestHandler#processRequest(RESTRequest)
	 */
	public void delete(RESTRequest<ResourceRepresentation<?>> r) {
		try {
			httpRequests.put(r.getID(), new HTTPContainer(new HttpDelete(r.getUrl()), new URI(r.getUrl()), r.getHeaders()));
			processRequest(r);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Launch request in a worker thread and fires {@link ProcessorCallback}
	 * 
	 * @param request
	 * 		Instance of {@link RESTRequest}
	 * 
	 * @param holder
	 * 		InputStream holding post data
	 */
	private void processRequest(final RESTRequest<ResourceRepresentation<?>> request, final InputStream holder) {
		new Thread(new Runnable() {
	        public void run() {
	    		HTTPContainer currentHttpContainer = httpRequests.get(request.getID());
	    		HttpResponse response = null;
	    		HttpEntity responseEntity = null;
	    		int statusCode = 0;
	    		InputStream IS = null;
	    		try {
	    			response = currentHttpContainer.execute(holder);
	    			responseEntity = response.getEntity();
	    			StatusLine responseStatus = response.getStatusLine();
	    			statusCode                = responseStatus != null ? responseStatus.getStatusCode() : 0;
	    			IS = responseEntity.getContent();
	    		} catch (ClientProtocolException e) {
	    			// TODO Auto-generated catch block
	    			statusCode = CLIENT_PROTOCOL_EXCEPTION;
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			// TODO Auto-generated catch block
	    			if(e instanceof UnknownHostException)
	    				statusCode = UNKNOWN_HOST_EXCEPTION;
	    			else if(e instanceof MalformedURLException)
	    				statusCode = MALFORMED_URL_EXCEPTION;
	    			else if(e instanceof UnknownServiceException)
	    				statusCode = UNKNOWN_SERVICE_EXCEPTION;
	    			else if(e instanceof ConnectTimeoutException)
	    				statusCode = CONNECT_TIMEOUT_EXCEPTION;
	    			else if(e instanceof SocketTimeoutException)
	    				statusCode = SOCKET_TIMEOUT_EXCEPTION;
	    			else
	    				statusCode = IO_EXCEPTION;
	    			Log.e("debug", e.getStackTrace().toString());
	    		} finally {
	    			try {
	    				if(null != responseEntity)
	    					responseEntity.consumeContent();
	    			} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    		}
	    		mProcessorCallback.callAction(statusCode, request, IS);
	        }
	    }).start();
	}
	
	/**
	 * @see HttpRequestHandler#processRequest(RESTRequest, InputStream)
	 * 
	 * @param request
	 * 		Instance of {@link RESTRequest}
	 * 
	 */
	private void processRequest(final RESTRequest<ResourceRepresentation<?>> request) {
		new Thread(new Runnable() {
	        public void run() {
	    		try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    		HTTPContainer currentHttpContainer = httpRequests.get(request.getID());
	    		HttpResponse response = null;
	    		HttpEntity responseEntity = null;
	    		int statusCode = 0;
	    		InputStream IS = null;
	    		try {
	    			response = currentHttpContainer.execute();
	    			responseEntity = response.getEntity();
	    			StatusLine responseStatus = response.getStatusLine();
	    			statusCode                = responseStatus != null ? responseStatus.getStatusCode() : 0;
	    			IS = responseEntity.getContent();
	    			//Log.e(RestService.TAG, "IS RESPONSE SERVER = " + inputStreamToString(IS));
	    		} catch (ClientProtocolException e) {
	    			// TODO Auto-generated catch block
	    			statusCode = CLIENT_PROTOCOL_EXCEPTION;
	    			//e.printStackTrace();
	    		} catch (IOException e) {
	    			// TODO Auto-generated catch block
	    			if(e instanceof UnknownHostException)
	    				statusCode = UNKNOWN_HOST_EXCEPTION;
	    			else if(e instanceof MalformedURLException)
	    				statusCode = MALFORMED_URL_EXCEPTION;
	    			else if(e instanceof UnknownServiceException)
	    				statusCode = UNKNOWN_SERVICE_EXCEPTION;
	    			else if(e instanceof ConnectTimeoutException)
	    				statusCode = CONNECT_TIMEOUT_EXCEPTION;
	    			else if(e instanceof SocketTimeoutException)
	    				statusCode = SOCKET_TIMEOUT_EXCEPTION;
	    			else
	    				statusCode = IO_EXCEPTION;
	    			//e.printStackTrace();
	    		} finally {
		    		mProcessorCallback.callAction(statusCode, request, IS);
	    			try {
	    				if(null != responseEntity)
	    					responseEntity.consumeContent();
	    			} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    		}
	        }
	    }).start();
	}
	
	/**
	 * <b>Binder callback fires when the request is finished</b>
	 * 
	 * @author Pierre Criulanscy
	 *
	 * @version 0.5
	 */
	public interface ProcessorCallback {
		
		/**
		 * Method to handle the callback
		 * 
		 * @param statusCode
		 * 		The status code of the request
		 * 
		 * @param request
		 * 		Instance of the current {@link RESTRequest}
		 * 
		 * @param resultStream
		 * 		Server response
		 */
		abstract public void callAction(int statusCode, RESTRequest<ResourceRepresentation<?>> request, InputStream resultStream);
	}
	
	/**
	 * Set the processor callback
	 * 
	 * @param callback
	 * 		@see ProcessorCallback
	 * 
	 * @see HttpRequestHandler#mProcessorCallback
	 */
	public void setProcessorCallback(ProcessorCallback callback) {
		mProcessorCallback = callback;
	}
	
	
	/**
	 * <b>Holder inner class holding http client, context and other http stuff</b>
	 * 
	 * @author Pierre Criulanscy
	 * 
	 * @version 0.5
	 */
	private class HTTPContainer {
		
		/**
		 * Actual HttpClient
		 * 
		 * @see HttpClient
		 */
		private HttpClient mHttpClient;
		
		/**
		 * Actual HttpClient
		 * 
		 * @see HttpClient
		 */
		private HttpContext mHttpContext;
		
		/**
		 * Actual HttpRequestBase
		 * 
		 * @see HttpRequestBase
		 */
		private HttpRequestBase mRequest;
		
		/**
		 * Actual HttpParams
		 * 
		 * @see HttpParams
		 */
		private HttpParams mHttpParams;
		
		/**
		 * Constructor. Defines a {@link BasicHttpContext}, {@link BasicHttpContext} and {@link DefaultHttpClient}
		 * 
		 * @param request
		 * 		Instance of {@link HttpRequestBase}
		 * 
		 * @param uri
		 * 		Uri to retrieve
		 * 
		 * @param headers
		 * 		List of {@link SerializableHeader}
		 * 
		 * @see HTTPContainer#mHttpClient
		 * @see HTTPContainer#mHttpContext
		 * @see HTTPContainer#mHttpParams
		 * @see HTTPContainer#mRequest
		 */
		public HTTPContainer(HttpRequestBase request, URI uri, List<SerializableHeader> headers) {
			mHttpParams = new BasicHttpParams();
			mHttpContext = new BasicHttpContext();
			HttpConnectionParams.setConnectionTimeout(mHttpParams, TIMEOUT_CONNECTION);
			HttpConnectionParams.setSoTimeout(mHttpParams, TIMEOUT_SOCKET);
			mHttpClient = new DefaultHttpClient(mHttpParams);
			mRequest = request;
			mRequest.setURI(uri);
			setHeaders(headers);
		}

		/**
		 * Add header to {@link HTTPContainer#mRequest}
		 * 
		 * @param headers
		 * 		List of {@link SerializableHeader}
		 */
		private void setHeaders(List<SerializableHeader> headers) {
			if(null != headers) {
				for(SerializableHeader h : headers) {
					mRequest.addHeader(h.getName(), h.getValue());
				}
			}
		}
		
		/**
		 * Executes the request
		 * 
		 * @return
		 * 		Instance of {@link HttpResponse}
		 * 
		 * @throws ClientProtocolException
		 * @throws IOException
		 */
		public HttpResponse execute() throws ClientProtocolException, IOException {
			if(mRequest instanceof HttpGet || mRequest instanceof HttpDelete)
				return mHttpClient.execute(mRequest, mHttpContext);
			return null;
		}

		/**
		 * Executes the request with data. Add header to manager JSON. (TODO change this)
		 * 
		 * @param holder
		 * 		Data to send
		 * 
		 * @return
		 * 		Instance of {@link HttpResponse}
		 * 
		 * @throws ClientProtocolException
		 * @throws IOException
		 */
		public HttpResponse execute(InputStream holder) throws ClientProtocolException, IOException {
			if(mRequest instanceof HttpPost || mRequest instanceof HttpPut) {
				mRequest.setHeader("Accept", "application/json");
				mRequest.setHeader("Content-type", "application/json");
				String str = inputStreamToString(holder);
				StringEntity se = new StringEntity(str);
				if(mRequest instanceof HttpPost) {
					((HttpPost) mRequest).setEntity(se);
				}
				else
					((HttpPut) mRequest).setEntity(se);
				return mHttpClient.execute(mRequest, mHttpContext);
			}
			return null;
		}
		
		private String inputStreamToString(InputStream is) {
	        BufferedReader bufferedReader;
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				StringBuilder inputStringBuilder = new StringBuilder();
		        String line;
				try {
					line = bufferedReader.readLine();
					while(line != null){
			            inputStringBuilder.append(line);inputStringBuilder.append('\n');
			            try {
							line = bufferedReader.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
					return inputStringBuilder.toString();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return null;
		}
		
	}
	
}
