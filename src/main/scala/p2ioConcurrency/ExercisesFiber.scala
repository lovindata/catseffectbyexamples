package com.ilovedatajjia
package p2ioConcurrency

import util.Utils._
import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.effect.{IO, IOApp, Outcome}
import scala.concurrent.duration._
import cats.implicits._

/**
 * CCL
 *  - ">>" To chain IOs into one single IO
 *  - IO(_).start and IO(_).join
 *  - Succeeded, Errored, Canceled with .flatMap
 */
object ExercisesFiber extends IOApp.Simple {

  // Example 1
  def runIOInFiber[A](myIO: IO[A]): IO[A] = {

    val fiberResult: IO[Outcome[IO, Throwable, A]] = for {
      result <- myIO.start
      //_ <- result.cancel
      joined <- result.join
    } yield joined

    val handled: IO[A] = fiberResult.debug.flatMap {
      case Succeeded(fa) => fa
      case Errored(e) => IO.raiseError(e)
      case Canceled() => IO.raiseError(new RuntimeException("Fiber cancelled!"))
    }

    handled

  }

  def testRunIOInFiber(): IO[Unit] = {
    val resAll: IO[Int] = IO("starting").debug >> IO.raiseError(new Exception()) >> IO.sleep(1.seconds) >> IO("done").debug >> IO(42).debug
    //val resAll: IO[Int] = IO("starting").debug >> IO.sleep(1.seconds) >> IO("done").debug >> IO(42).debug
    runIOInFiber(resAll).void
  }

  // Example 2
  def runTupleIOInFiber[A, B](io1: IO[A], io2: IO[B]): IO[(A, B)] = {

    val res: IO[(Outcome[IO, Throwable, A], Outcome[IO, Throwable, B])] = for {
      io1Fiber <- io1.start
      io2Fiber <- io2.start
      res1 <- io1Fiber.join
      res2 <- io2Fiber.join
    } yield (res1, res2)

    res.flatMap {
      case (Succeeded(res1), Succeeded(res2)) => (res1, res2).mapN((_, _))
      case (Errored(e), _) => IO.raiseError(e)
      case (_, Errored(e)) => IO.raiseError(e)
      case _ => IO.raiseError(new RuntimeException("One computation failed!"))
    }

  }

  def testRunTupleIOInFiber(): IO[Unit] = {
    val resAllIO1: IO[Int] = IO("starting").debug >> IO.sleep(1.seconds) >> IO("done").debug >> IO(41).debug
    val resAllIO2: IO[Int] = IO("starting").debug >> IO.sleep(10.seconds) >> IO("done").debug >> IO(42).debug
    runTupleIOInFiber(resAllIO1, resAllIO2).debug.void
  }

  // Continue on Example3
  def runTimeoutIO[A](io: IO[A], durationMax: Int): IO[A] = {

    val fiberResult: IO[Outcome[IO, Throwable, A]] = for {
      ioEval <- io.start
      _ <- IO.sleep(durationMax.seconds) >> ioEval.cancel
      ioRes <- ioEval.join
    } yield ioRes

    val handled: IO[A] = fiberResult.debug.flatMap {
      case Succeeded(fa) => fa
      case Errored(e) => IO.raiseError(e)
      case Canceled() => IO.raiseError(new RuntimeException("Fiber cancelled!"))
    }

    handled

  }

  def testRunTimeoutIOr(): IO[Unit] = {
    val resAllIO1: IO[Int] = IO("starting").debug >> IO.sleep(5.seconds) >> IO("done").debug >> IO(41).debug
    runTimeoutIO(resAllIO1, 6).debug.void
  }

  override def run: IO[Unit] = testRunTimeoutIOr()
}
