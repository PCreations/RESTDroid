#Change log 0.8.1
*   Rename OnSucceedRequestListener to OnSucceededRequestListener

#Change log 0.8
*   You can now send and receive ResourceRepresentation list via ResourcesList interface
*   You can easily manage caching your request thanks to CacheManager
*   An ExecutorService is now used to manage thread pool
*   Failed request are automatically handle by FailBehavior and FailBehaviorManager
*   Fixed bug when trying to chain request in instance of RequestListeners class (thank you to Olivier Bregeras to have pointed me out this error)

#Change log 0.7.2.3
*	Hotfixes : Remove useless import android.download.Request and rename MainActivity of RESTDroid project

#Change log 0.7.2.2
*	Hotfixes : Removes useless res/ files and 3 seconds test latency in HttpRequestHandler

#Change log 0.7.2.1
*	Hotfix : Set default charset to UTF-8

#Change log 0.7.2
*	Fix bug when dealing with post request (request was not correctly initialized)
*	Fixed bug when calling getResultStream() when request's result stream is null
*	RequestListeners now holds a reference to the RESTRequest wich is holding it
*	Fix bug in Processor.checkRequest when resource's result code is 200 but returns false to not resend the request

#Change log 0.7.1
*	Fix bug with RESTRequest factory in WebService class
*	Result stream send by the server is now accessible within RESTRequest class by calling getResultStream()

#Change log 0.7.0

*	Request listeners now manage with RequestListeners class in order to avoid listener duplication
*	GET/POST/PUT/DELETE methods from WebService class now return instance of RESTRequest already pending or a new instance
*	Request are now executed when you want. Use WebService#executeRequest() from WebService class