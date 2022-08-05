package com.ilovedatajjia
package _1_concurrency

import util._
import cats.effect.{IO, IOApp, Poll}
import cats.effect.Outcome.{Canceled, Errored, Succeeded}
import scala.concurrent.duration._

object _A_Fiber extends IOApp.Simple {

  // Define an IO
  val ioToStart: IO[String] = IO("Starting computation...").debug >>
    IO.sleep(1.second) >>
    //IO.raiseError(new RuntimeException("Exception raised!")) >> // To simulate an crashing IO -> Errored
    IO("Finished computation").debug

  // Starting fiber, Join fiber AND Managing its outcome
  val out: IO[String] = for {
    fib <- ioToStart.start
    _ <- fib.cancel // To simulate a cancel fiber -> Canceled
    _ <- IO.sleep(3.second) // Simulate some intermediate computation before join
    fibOut <- fib.join
    res <- fibOut match {
      case Succeeded(fa) => fa
      case Errored(e) => IO(e.getMessage).debug
      case Canceled() => IO("Canceled fiber :/").debug
    }
  } yield res

  // Run(s)
  override def run: IO[Unit] =
    out.void

}
