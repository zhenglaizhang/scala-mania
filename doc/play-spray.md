
## Play or Spray it

* Play has lots of cool things and we can use it to make a REST API, but Spray is exactly a framework to make REST APIs
* Spray “should” be faster since it’s more lightweight.

### No

 I think both options could result in an equally good project. Obviously, the choice depends on the team that will work with it and depends on the project features.
 
 * One of the main reasons to choose Akka-HTTP over Play could be performance (number of requests per second).
 * I found that Akka-HTTP is faster than Play, on average 6% faster.
 * I know that we cannot measure the performance of a framework just with one test scenario, but it gives me the idea that the performance could be very similar. 
 
 
 * Team experience
 * Team motivation
 * Project Features
    * Both frameworks support all kinds of authentication, but, maybe in Play, we have more options to do it.
 * Project Maintenance


> Jonas Boner from Typesafe has referred to Akka-http as "Spray 2.0". So don't expect any future versions of Spray and at some point you'll have to make the switch. I saw Jonas' Akka-http presentation at Scala Days and it looks like porting Spray code to Akka-http should be straight forward as the DSL is mostly unchanged (even though the underlying implementation of the library will be different).
  
  To answer your questions specifically: Spray is finished as a separate project, it is being imported into Akka under the name Akka-http (not a merge as Akka didn't have any equivalent before hand). If you need to start development now go with Spray, if you can afford to work with the inevitable bugs in a preview release go with Akka-http. Your Spray code will never stop working, but it won't be supported either outside of minor bug fixes. All new functionality will be added in Akka-http, so instead of updating to Spray 2.0 you update to Akka-http.
  
  
> The Akka/Spray integration—named Akka HTTP—will provide an ideal way of producing and consuming embeddable REST services.


