#Change log 0.7.1
*	Fix bug with RESTRequest factory in WebService class
*	Result stream send by the server is now accessible within RESTRequest class by calling getResultStream()

#Change log 0.7.0

*	Request listeners now manage with RequestListeners class in order to avoid listener duplication
*	GET/POST/PUT/DELETE methods from WebService class now return instance of RESTRequest already pending or a new instance
*	Request are now executed when you want. Use WebService#executeRequest() from WebService class