package com.ilovedatajjia
package _1_concurrency

import util._
import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.effect.{IO, IOApp, Poll}
import scala.concurrent.duration.DurationInt

object _A_Fiber extends IOApp.Simple {

  // Starting fiber, Join fiber AND Managing outcome
  val ioToStart: IO[String] = IO("Starting computation...").debug >>
    IO.sleep(1.second) >>
    //IO.raiseError(new RuntimeException("Exception raised!")) >> // To simulate an crashing IO -> Errored
    IO("Finished computation").debug
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

  // Uncancelable region(s)
  val io2Seconds: IO[Unit] = IO("Starting 2 seconds wait").debug >>
    IO.sleep(2.seconds) >>
    IO("Finished 2 seconds wait").debug.void
  val io1Second: IO[Unit] = IO("Starting 1 second wait").debug >>
    IO.sleep(1.second) >>
    IO("Finished 1 second wait").debug.void
  val ioUncancelableWithCancelableRegion: IO[Unit] = IO.uncancelable { cancelablePoll: Poll[IO] =>
    io2Seconds >>
      cancelablePoll(io1Second).onCancel(IO("Canceled in the region!").debug.void)
  }
  val outUncancelableWithCancelableRegion: IO[Unit] = for {
    _ <- IO("Starting computation").debug
    fib <- ioUncancelableWithCancelableRegion.start
    //_ <- IO.sleep(1000.millis) >> fib.cancel // Happening in the uncancelable region
    //_ <- IO.sleep(2500.millis) >> fib.cancel // Happening in the cancelable region
    _ <- IO.sleep(3500.millis) >> fib.cancel // Happening after the fiber finished
    _ <- fib.join
    _ <- IO("Finished computation").debug
  } yield ()

  // Run(s)
  override def run: IO[Unit] =
    //out.void
    outUncancelableWithCancelableRegion

}
