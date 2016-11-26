import java.io.{ FileNotFoundException, InputStream }
import java.net.{ MalformedURLException, URL }

import scala.io.Source
import scala.util.{ Failure, Success, Try }

case class UnderAgeException(message: String) extends Exception(message)

def buyCigarettes(customer: Customer): Cigarettes =
  if (customer.age < 16)
    throw UnderAgeException(s"Customer must be older than 16 but was ${customer.age}")
  else new Cigarettes

case class Customer(age: Int)

class Cigarettes

val youngCustomer = Customer(14)

try {
  buyCigarettes(youngCustomer)
  "Yo, here are your cancer stikcks! Happy smoking'!"
} catch {
  case UnderAgeException(msg) => msg
}

/*
Error handling, the functional way

Hence, in Scala, it’s usually preferred to signify that an error has occurred by returning an appropriate value from your function.


Where Option[A] is a container for a value of type A that may be present or not, Try[A] represents a computation that may result in a value of type A, if it is successful, or in some Throwable if something has gone wrong.


There are two different types of Try: If an instance of Try[A] represents a successful computation, it is an instance of Success[A], simply wrapping a value of type A. If, on the other hand, it represents a computation in which an error has occurred, it is an instance of Failure[A], wrapping a Throwable
 */

def parseURL(url: String): Try[URL] = Try(new URL(url))

/*
Try apply
    by name parameter
    executed inside the apply method of the Try object. Inside that method, non-fatal exceptions are caught, returning a Failure containing the respective exception.
 */

// It’s also possible to use getOrElse to pass in a default value to be returned if the Try is a Failure:
parseURL("http://www.google.com") getOrElse new URL("http://www.bing.com")

/*
Mapping a Try[A] that is a Success[A] to a Try[B] results in a Success[B]. If it’s a Failure[A], the resulting Try[B] will be a Failure[B], on the other hand, containing the same exception as the Failure[A]
 */

parseURL("http://www.google.com").map(_.getProtocol)
parseURL("garbage").map(_.getProtocol)

def inputStreamForURL(url: String): Try[Try[Try[InputStream]]] = parseURL(url).map { u =>
  Try(u.openConnection()).map(conn => Try(conn.getInputStream))
}

def inputStreamForURLGood(url: String): Try[InputStream] = parseURL(url).flatMap { u =>
  Try(u.openConnection()).flatMap(conn => Try(conn.getInputStream))
}
/*
Now we get a Try[InputStream], which can be a Failure wrapping an exception from any of the stages in which one may be thrown, or a Success that directly wraps the InputStream, the final result of our chain of operations.
 */

/*
The filter method returns a Failure if the Try on which it is called is already a Failure or if the predicate passed to it returns false (in which case the wrapped exception is a NoSuchElementException).
 */
def parseHttpURL(url: String) = parseURL(url).filter(_.getProtocol == "http")
parseHttpURL("http://apache.openmirror.de") // results in a Success[URL]
parseHttpURL("ftp://mirror.netcologne.de/apache.org") // results in a Failure[URL]

// The function passed to foreach is executed only if the Try is a Success, which allows you to execute a side-effect.
parseHttpURL("http://danielwestheide.com").foreach(println)
parseHttpURL("ftp://danielwestheide.com").foreach(println)

def getURLContent(url: String): Try[Iterator[String]] =
  for {
    url <- parseURL(url)
    connection <- Try(url.openConnection())
    is <- Try(connection.getInputStream)
    source = Source.fromInputStream(is)
  } yield source.getLines()

getURLContent("garbage")

getURLContent("http://danielwestheide.com/foobar") match {
  case Success(lines) => lines.foreach(println)
  case Failure(ex)    => println(s"Problem rendering URL content: ${ex.getMessage}")
}

/*
An alternative is recover, which expects a partial function and returns another Try. If recover is called on a Success instance, that instance is returned as is. Otherwise, if the partial function is defined for the given Failure instance, its result is returned as a Success.
 */

val content = getURLContent("garbage") recover {
  case e: FileNotFoundException => Iterator("Requested page does not exist")
  case e: MalformedURLException => Iterator("Please make sure to enter a valid URL")
  case _                        => Iterator("An unexpected error has occurred. We are so sorry!")
}

content.get.foreach(println)

/*
The Try type allows you to encapsulate computations that result in errors in a container and to chain operations on the computed values in a very elegant way.
 */ 