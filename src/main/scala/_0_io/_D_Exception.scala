package com.ilovedatajjia
package _0_io

import util.Utils._
import cats.effect.{IO, IOApp}

object _D_Exception extends IOApp.Simple {

  // Automatic throw when exception
  val failingIO1: IO[String] = IO(throw new RuntimeException("Failing with `throw`"))
  val failingIO2: IO[String] = IO.raiseError(new RuntimeException("Failing without `throw`"))

  // Functions to handle exception(s)
  val multiCaseHandling: IO[String] = failingIO2.handleErrorWith {
    case e: RuntimeException => IO(s"""RuntimeException handled \"${e.getMessage}\"""").debug
    case e => IO(s"""Other exception handled \"${e.getMessage}\"""").debug
  }
  val eitherHandling: IO[Either[Throwable, String]] = failingIO2.attempt.debug

  // Handle & Processing in one-go
  val redeemHandlingProcessing: IO[String] = failingIO2.redeem(
    (e: Throwable) => s"It failed `$e`",
    (res: String) => s"It succeeded `$res`"
  ).debug
  val redeemIO: IO[String] = failingIO2.redeemWith(
    (e: Throwable) => IO(s"It failed `$e`").debug,
    (res: String) => IO(s"It succeeded `$res`").debug
  )

  // Run(s)
  override def run: IO[Unit] = {
    // failingIO1.void
    // failingIO2.void
    // multiCaseHandling.void
    // eitherHandling.void
    // redeemHandlingProcessing.void
    redeemIO.void
  }

}
