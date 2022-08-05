package com.ilovedatajjia
package _1_concurrency

import util._

import cats.effect.Outcome.{Canceled, Errored, Succeeded}
import cats.effect.{IO, IOApp, Poll}

import scala.concurrent.duration._

object _B_Uncancelable extends IOApp.Simple {

  // Define some IOs
  val io2Seconds: IO[Unit] = IO("Starting 2 seconds wait").debug >>
    IO.sleep(2.seconds) >>
    IO("Finished 2 seconds wait").debug.void
  val io1Second: IO[Unit] = IO("Starting 1 second wait").debug >>
    IO.sleep(1.second) >>
    IO("Finished 1 second wait").debug.void

  // Define an uncancelable IO
  val ioUncancelableWithCancelableRegion: IO[Unit] = IO.uncancelable { cancelablePoll: Poll[IO] =>
    io2Seconds >>
      cancelablePoll(io1Second).onCancel(IO("Canceled in the region!").debug.void)
  }

  // Experiments on the uncancelable IO
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
    outUncancelableWithCancelableRegion

}
