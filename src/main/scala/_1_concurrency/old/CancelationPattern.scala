package com.ilovedatajjia
package _1_concurrency.old

import util.Utils.DebugWrapper

import cats.effect.{IO, IOApp}

import scala.concurrent.duration.DurationInt

/**
 * CCL
 *  - IO(_).uncancelable
 *  - IO.uncancelable { cancelablePoll => _ }
 *  - If cancel signal is processed by the first encountered IO in the cancelablePoll
 */
object CancelationPattern extends IOApp.Simple {

  val inputBankTransaction: IO[String] =
    IO("input bank transaction").debug >> IO("<input user>").debug >> IO.sleep(2.seconds) >> IO(100) >> IO("user input registered").debug
  val transacBankTransaction: IO[Unit] =
    IO("starting sum transaction").debug >>
      IO.sleep(1.seconds) >>
      IO("transaction sucessful").debug.void

  def testingCancellation(): IO[Unit] = {
    val myWholeIO: IO[Unit] = IO.uncancelable { cancelablePoll =>
      inputBankTransaction >> cancelablePoll(transacBankTransaction).onCancel(IO("Interrupted before starting transac").debug >> IO.unit)
    }

    for {
      fib <- myWholeIO.start
      _ <- IO.sleep(4000.millis) >> fib.cancel // play with the milliseconds stuff
      _ <- fib.join
    } yield ()
  }

  override def run: IO[Unit] = testingCancellation()

}
