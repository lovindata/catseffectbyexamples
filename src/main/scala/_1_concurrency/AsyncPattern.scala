package com.ilovedatajjia
package _1_concurrency

import util.Utils.DebugWrapper

import cats.effect.kernel.Outcome
import cats.effect.{IO, IOApp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Try

/**
 * CCL
 *  - IO.async_ { callBack: (Either[Throwable, A] => Unit) => _ } for retrieve results from other than CE not concurrent
 *  - IO.async { callBack: (Either[Throwable, A] => Unit) => IO(_).as(Some(IO(_)) } idem but with cancellation management ".as(Some(IO(_))"
 *  - Both are blocking until the callBack method is called!
 */
object AsyncPattern extends IOApp.Simple {

  // IO.async_
  def blockingAsyncFutureToIO[A](myFuture: Future[A]): IO[A] = {

    IO.async_ { callBack: (Either[Throwable, A] => Unit) =>
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

  def testFasterAsyncFromFutureToIO: IO[Unit] = {
    val inputFuture: Future[Int] = Future {
      println(s"[${Thread.currentThread().getName}] External not an IO thread blocked starting...")
      Thread.sleep(5000)
      3
    }

    IO.fromFuture(IO(inputFuture)).debug.void
  }

  // IO.async
  def blockingFutureToIOWithCancellationHandled[A](myFuture: Future[A]): IO[A] = {

    IO.async { callBack: (Either[Throwable, A] => Unit) =>

      IO {
        myFuture.onComplete { myRes: Try[A] =>
          callBack(myRes.toEither)
        }
      }.as(Some(IO("putaing cancelled").debug.void))

    }

  }

  def testBlockingFutureToIOWithCancellationHandled: IO[Outcome[IO, Throwable, Int]] = {
    val inputFuture: Future[Int] = Future {
      println(s"[${Thread.currentThread().getName}] External not an IO thread blocked starting...")
      Thread.sleep(5000)
      3
    }

    val ioToStart: IO[Int] = blockingFutureToIOWithCancellationHandled(inputFuture)
    for {
      fib <- ioToStart.start
      _ <- IO.sleep(6000.millis) >> IO("cancelling fib...").debug >> fib.cancel
      finalRes <- fib.join
    } yield finalRes
  }

  override def run: IO[Unit] = testBlockingFutureToIOWithCancellationHandled.debug.void

}
