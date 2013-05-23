RESTDroid : REST client library for Android
===========================================

Alpha release 0.8.1.1 : Testers and contributors are welcome :)

RESTDroid provides a way to handle REST call to REST web-service. RESTDroid only packed fundamental logic to handle request but comes with additionnal logic such as automatic data persistency with remote server. Using or extending this logic is the role of Module. Here you can found severals Module such as an ORMlite-Jackon module to handle data persistence and mapping/parsing.

[RESTDroid Documentation](http://pcreations.fr/RESTDroid/doc/)

RESTDroid in a nutshell :

*	Make __asynchronous__ REST request
*	You're __not limited to one web service__
*	Requests hold __POJO's__ (can be your database model)
*	Network calls are __not tied to your Activity__, if the Activity is killed, network / database operations (ore whathever you decided to do) are still running
*	You can __notify your Activities__ with request listeners
*	You can __dynamically change the process logic__ via RESTDroid Module (choose to cache & persist, only debug, not to cache, or whatever you want/need by creating a new RESTDroid Module)
*	You can know at any moment if a particular local resource is remotely syncronized. Data persistence between local and remote is automatically handles.
*	You can __easily manage caching__ for your request (new in 0.8)
*	You can __specify a behavior at failure__ for your request such as __automatically retry request when anoter one has succeeded__ or __retry the request every X seconds untils the request is successfull__. You can of course __implement your own behavior at failure__ (new in 0.8)

Futures features for v1
----------------

#ROADMAP

*	Use HttpConnection instead of apache HTTP client
*	Handle authentication and certificate
*	Create a good Exception handling model

Migration guide
---------------

To migrate on version 0.8.x just refactor any calls to setResourceRepresentation or getResourceRepresentation to only setResource / getResource. Some methods signature will change in that way too but Eclipse should warn you.

User guide
----------

You can find the whole guide on my website : [RESTDroid guide](http://pcreations.fr/restdroid-guides)
