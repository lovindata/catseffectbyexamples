package com.ilovedatajjia
package _1_concurrency.old

import util._

import cats.effect.{IO, IOApp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object _C_Async extends IOApp.Simple {

  // Bring result from other threads into CE
  val supposeThisNonCEThread: Future[Int] = Future {
    println(s"[${Thread.currentThread().getName}] Start a run on non CE thread")
    Thread.sleep(1000)
    0
  }
  val intoCEThread: IO[Int] = IO.async_ { callBack: (Either[Throwable, Int] => Unit) => // <- Semantic blocking here until callBack call
    //supposeThisNonCEThread.onComplete((x: Try[Int]) => callBack(x.toEither))
    ()
  }.debug

  // Simple version for Future
  val ioFromFuture: IO[Int] = IO.fromFuture(IO(Future(throw new RuntimeException("Throw inside the Future"))))

  // Run(s)
  override def run: IO[Unit] =
    //intoCEThread.void
    ioFromFuture.void

}
