RESTDroid : REST client library for Android
===========================================

Alpha release 0.7.1

RESTDroid provides a way to handle REST call to REST web-service. RESTDroid only packed fundamental logic to handle request. Extends this logic is the role of Module. Here you can found severals Module such as an ORMlite-Jackon module to handle data persistence and mapping/parsing.

[RESTDroid Documentation](http://pcreations.fr/labs/RESTDroid/doc)

RESTDroid in a nutshell :

*	Make __asynchronous__ REST request
*	You're __not limited to one web service__
*	Requests hold __POJO's__ (can be your database model)
*	Network calls are __not tied to your Activity__, if the Activity is killed, network / database operations (ore whathever you decided to do) are still running
*	You can __notify your Activities__ with request listeners
*	You can __dynamically change the process logic__ via RESTDroid Module (choose to cache & persist, only debug, not to cache, or whatever you want/need by creating a new RESTDroid Module)

For contributors
----------------

#TO DO FOR V1

*	Create a "Main" Service to holds request creation (in order to avoid string ID)
*	Create a CacheManager to handles cache (add a method to DaoAccess to manage that)
*	Use HttpConnection instead of apache HTTP client

User guide
----------

# Getting started

## RESTDroid available Modules :

*	[ORMLiteJacksonModule](https://github.com/PCreations/ORMLiteJacksonModule)
*	[TestModule](https://github.com/PCreations/RESTDroid-Test-Module)

## Forward and return path schema :

![Forward and return parth schema](http://pcreations.fr/labs/RESTDroid/RESTPattern.png)

## User guide

### Getting started

Download RESTDroid library and add it to your Android project. Update your android manifest :

<pre>
<code>
&lt;uses-permission android:name="android.permission.INTERNET" />
&lt;application
        ...
        &lt;activity
            ...
        &lt;/activity>
        &lt;service android:enabled="true" android:name="fr.pcreations.labs.RESTDroid.core.RestService">&lt;/service>
    &lt;/application>
&lt;service android:enabled="true" android:name="fr.pcreations.labs.RESTDroid.core.RestService">&lt;/service>
</code>
</pre>

Implement RESTDroid boils down to implement a RESTDroid Module or use an existing one. RESTDroid Core library implements many hooks throughout the processus on which you can attached you specific logic. See image below :
![Schema](http://hpics.li/6d236af)
All you have to do it's to create a new RESTDroid Module by extending Processor and WebService classes.

### Create a RESTDroid Module

Module is the class that holds your own implementation of Processor, WebService, DaoFactory and ParserFactory classes plus whatever you need. Let's consider a very simple example. We just want to create a Test Module that only display response server in LogCat and only send GET request. This Module can be usefull if you just want to test a new web-service. Of course this is a totally overkill implementation for this simple use, but it's a good way to understand how all classes works together.

First create the TestModule class and add unimplemented methods :

<pre>
<code>
public class TestModule extends Module {

	@Override
	public Processor setProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParserFactory setParserFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DaoFactory setDaoFactory() {
		// TODO Auto-generated method stub
		return null;
	}

}
</code>
</pre>

As you can see a Module is just a holder class to pack all you logic. We don't need a Parser class or Dao class, remember that it's a very simple example. We need a TestProcessor, let's create it :

<pre>
<code>
public class TestProcessor extends Processor {

	@Override
	protected void preRequestProcess(
			RESTRequest&lt;ResourceRepresentation&lt;?>> r)
			throws Exception {
		r.addHeader("Accept", "application/json");
		r.addHeader("Content-Type", "application/json");
		/* Parse.com Authentication headers */
		r.addHeader("X-Parse-Application-Id", TestWebService.APPLICATION_ID);
		r.addHeader("X-Parse-REST-API-Key", TestWebService.REST_API_KEY);
	}

	@Override
	protected void preGetRequest(
			RESTRequest&lt;ResourceRepresentation&lt;?>> r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void preDeleteRequest(
			RESTRequest&lt;ResourceRepresentation&lt;?>> r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected InputStream prePostRequest(
			RESTRequest&lt;ResourceRepresentation&lt;?>> r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected InputStream prePutRequest(
			RESTRequest&lt;ResourceRepresentation&lt;?>> r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int postRequestProcess(
			int statusCode, RESTRequest&lt;ResourceRepresentation&lt;?>> r, InputStream resultStream) {
		if(statusCode >= 200 && statusCode &lt;= 210)
			Log.i(TestService.TAG, inputStreamToString(resultStream));
		else
			Log.i(TestService.TAG, "Error : status code = " + String.valueOf(statusCode));
		return statusCode;
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
</code>
</pre>

Our TestProcessor could looks like this. We are overriding only few hooks :

*	preRequestProcess hook : used here to defined some headers to add in all request. In this example I decided to use [Parse.com](https://parse.com/) for rapid testing, so I need to use authentication header. It's a good hook to setting them up.
*	postRequestProcess hook : used here to display in LogCat the server's response

Now let's implement the TestWebService

<pre>
<code>
public class TestWebService extends WebService {

	public static final String APPLICATION_ID = "your_application_id";
	public static final String REST_API_KEY = "your_rest_api_key";
	public static final String TAG = "fr.pcreations.labs.RESTDROID.sample.TestWebService.TAG";
	
	private static final String BASE_URI = "https://api.parse.com/1/classes/";
	private static final String TEST_OBJECT = "Test/";
	
	/* Must defines this constructor for dynamic instanciation */
	public DebugWebService(Context context) {
		super(context);
	}
	
	public void getTest(RESTRequest&lt;TestObject> r, String id) {
		get(r, BASE_URI + TEST_OBJECT + id);
	}

}
</code>
</pre>

TestWebService acts as a helper. It provides a simple asynchronous API to the user interface. When implementing your own WebService class you must implement the constructor for dynamic instanciation.

getTest() takes a RESTRequest<TestObject> as a first argument. RESTRequest is the object that represents our request and must be parameterized with the item that will be sent/received.

In this example, I've created un Test object in Parse.com which have the following fields :

* String objectId
* String content
* String title
* String createdAt
* String updatedAt

Let's create this ResourceRepresentation :

<pre>
<code>
public class TestObject implements ResourceRepresentation&lt;String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2958835534649642979L;
	
	private String mId;
	private String mContent;
	private String mTitle;
	private Date mCreatedAt;
	private Date mUpdatedAt;
	
	public TestObject(String mId, String mContent, String mTitle,
			Date mCreatedAt, Date mUpdatedAt) {
		super();
		this.mId = mId;
		this.mContent = mContent;
		this.mTitle = mTitle;
		this.mCreatedAt = mCreatedAt;
		this.mUpdatedAt = mUpdatedAt;
	}

	public String getId() {
		return mId;
	}


	/* METHOD FOR DATA PERSISTENCE AND CACHING */

	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getResultCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getTransactingFlag() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setId(String id) {
		mId = id;
	}

	public void setState(int stateRetrieving) {
		// TODO Auto-generated method stub
		
	}

	public void setTransactingFlag(boolean transacting) {
		// TODO Auto-generated method stub
		
	}

	public void setResultCode(int resultCode) {
		// TODO Auto-generated method stub
		
	}

	/* END METHOD FOR DATA PERSISTENCE AND CACHING */

	public String getContent() {
		return mContent;
	}

	public String getTitle() {
		return mTitle;
	}

	public Date getCreatedAt() {
		return mCreatedAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setmContent(String content) {
		mContent = content;
	}

	public void setmTitle(String title) {
		mTitle = title;
	}

	public void setmCreatedAt(Date createdAt) {
		mCreatedAt = createdAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		mUpdatedAt = updatedAt;
	}
	
}
</code>
</pre>

Interface ResourceRepresentation is paramaterized with the type of item id field.

We're almost done, just a little obvious step : set the TestProcessor in TestModule :

<pre>
<code>
public class TestModule extends Module {

	@Override
	public Processor setProcessor() {
		return new TestProcessor();
	}

	@Override
	public ParserFactory setParserFactory() {
		return null;
	}

	@Override
	public DaoFactory setDaoFactory() {
		return null;
	}

}
</code>
</pre>

### Use a RESTDroid Module

Using a RESTDroid Module is very simple. All you have to do is to register this module to your WebService class. It's a very small snippet :

<pre>
<code>
public class TestActivity extends Activity {

	private DebugWebService ws;
	private RESTRequest&lt;TestObject> getTestRequest;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RESTDroid.init(getApplicationContext());
        try {
			ws = (TestWebService) RESTDroid.getInstance().getWebService(TestWebService.class);
			ws.registerModule(new TestModule());
			getTestRequest = ws.getTest(TestObject.class, "mG2hB0Xvco"); /* we want to retrieve the object with id "mG2hB0Xvco" from the 
			server */
			getTestRequest.setRequestListeners(new TestRequestListeners()); /* we now register RequestListeners class */
			ws.executeRequest(getTestRequest) /* and we execute the request */
		} catch (RESTDroidNotInitializedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        setContentView(R.layout.activity_main);
    }

    public class TestRequestListeners extends RequestListeners {
    	private OnStartedRequestListener onStart = new OnStartedRequestListener() {

    		public void onStartedRequest() {
    			Log.i(TestWebService.TAG, "getTestRequest has started");
    		}
    		
    	};
    	
    	private OnFinishedRequestListener onFinished = new OnFinishedRequestListener() {

    		public void onFinishedRequest(int resultCode) {
    			Log.i(TestWebService.TAG, "getTestRequest has finished with code " + resultCode);
    		}
    		
    	};
    	
    	private OnFailedRequestListener onFailed = new OnFailedRequestListener() {
    		
    		public void onFailedRequest(int resultCode) {
    			Log.i(TestWebService.TAG, "getTestRequest has failed with code " + resultCode);
    		}
    		
    	};
    	
    	public TestRequestListeners() {
    		super();
    		addOnStartedRequestListener(onStart);
    		addOnFinishedRequestListener(onFinished);
    		addOnFailedRequestListener(onFailed);
    	}
    }

}
</code>
</pre>

We're done ! Launch your test application and you will see in LogCat the response server :).
Again, this a totally overkill implementation for this use, you've seen only 10% of RESTDroid potential. Now let's take a look of a complete exemple with ORMLite for local database and Jackson for parsing/mapping and request listeners : [ORMLiteJacksonModule](https://github.com/PCreations/ORMLiteJacksonModule)
