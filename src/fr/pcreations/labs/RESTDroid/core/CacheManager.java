package fr.pcreations.labs.RESTDroid.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * <b>CacheManager handles caching request in flat file</b>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.8
 */
public class CacheManager {
	
	/**
	 * Don't use caching for this request
	 */
	public static final long DURATION_NO_CACHE = 0L;
	
	public static final long DURATION_ONE_SECOND = 1000L;
	public static final long DURATION_ONE_MINUTE = 60 * DURATION_ONE_SECOND;
	public static final long DURATION_ONE_HOUR = 60 * DURATION_ONE_MINUTE;
	public static final long DURATION_ONE_DAY = 24 * DURATION_ONE_HOUR;
	public static final long DURATION_ONE_WEEK = 7 * DURATION_ONE_DAY;
	public static final long DURATION_ONE_MONTH = 30 * DURATION_ONE_WEEK;
	public static final long DURATION_ONE_YEAR = 365 * DURATION_ONE_DAY;

	/**
	 * Android cache directory
	 */
	private static File cacheDir;
	
	private CacheManager(){}
	
	/**
	 * Setter for {@link CacheManager#cacheDir}
	 * 
	 * @param cacheDir
	 * 		The directory used for caching
	 */
	public static void setCacheDir(File cacheDir) {
		CacheManager.cacheDir = cacheDir;
	}
	
	/**
	 * Getter for {@link CacheManager#cacheDir}
	 * 
	 * @return
	 * 		{@link CacheManager#cacheDir}
	 */
	public static File getCacheDir() {
		return CacheManager.cacheDir;
	}
	
	/**
	 * Cache request in {@link CacheManager#cacheDir}
	 * 
	 * @param request
	 * 		The request to cache
	 * 
	 * @throws IOException
	 */
	public static void cacheRequest(RESTRequest<? extends Resource> request) throws IOException {
		InputStream input = request.getResultStream();
		try {
		    final File file = new File(CacheManager.getCacheDir(), String.valueOf(request.getUrl().hashCode()));
		    final OutputStream output = new FileOutputStream(file);
		    try {
		        try {
		            final byte[] buffer = new byte[1024];
		            int read;

		            while ((read = input.read(buffer)) != -1)
		                output.write(buffer, 0, read);

		            output.flush();
		        } finally {
		            output.close();
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    input.close();
		}
	}
	
	/**
	 * Retrieves a {@link RESTRequest} from cache
	 * 
	 * @param r
	 * 		The {@link RESTRequest} to retrieve
	 * 
	 * @return
	 */
	public static InputStream getRequestFromCache(RESTRequest<? extends Resource> r) {
		InputStream input = null;
		int requestHashcode = r.getUrl().hashCode();
		Date date = new Date();
		long actualTime = date.getTime();
		try {
		    final File file = new File(CacheManager.getCacheDir(), String.valueOf(requestHashcode));
		    if(file.exists()) {
		    	long lastModifiedTime = file.lastModified();
		    	long difference = actualTime - lastModifiedTime;
		    	if(actualTime - file.lastModified() <= r.getExpirationTime()) {
			    	input = new BufferedInputStream(new FileInputStream(file));
			    	file.setLastModified(lastModifiedTime);
			    	lastModifiedTime = file.lastModified();
			    	difference = difference + 5;
				    return input;
		    	}
		    	return null;
		    }
		} catch(Exception e) {
		    e.printStackTrace();  
		}
		return null;
	}
	
}
