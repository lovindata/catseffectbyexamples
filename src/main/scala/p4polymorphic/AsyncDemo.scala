package com.ilovedatajjia
package p4polymorphic

import util._

import cats.effect.{Async, IO, IOApp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

/**
 * CCL
 *  - Generalize the concept of IO with only `_.async_` or `_.async` on other F with `Sync[F]`
 */
object AsyncDemo extends IOApp.Simple {

  val asyncGeneralized: Async[IO] = Async[IO]

  def blockingAsyncFutureToIO[A](myFuture: Future[A]): IO[A] = {

    asyncGeneralized.async_ { callBack: (Either[Throwable, A] => Unit) =>
      // Will block at this line of code until the callBack is called
      myFuture.onComplete { futureRes: Try[A] =>
        println(s"[${Thread.currentThread().getName}] Finished computation...")
        val eitherRes: Either[Throwable, A] = futureRes.toEither
        callBack(eitherRes)
      }
    }

  }
  def testBlockingAsyncFutureToIO: IO[Unit] = {
    val inputFuture: Future[Int] = Future {
      println(s"[${Thread.currentThread().getName}] External not an IO thread blocked starting...")
      Thread.sleep(5000)
      3
    }

    blockingAsyncFutureToIO(inputFuture).debug.void
  }

  override def run: IO[Unit] = testBlockingAsyncFutureToIO
}
