package com.ilovedatajjia
package _1_concurrency

import util.Utils.DebugWrapper

import cats.effect.kernel.Outcome.Succeeded
import cats.effect.{IO, IOApp}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
 * CCL
 *  - `IO.race(ioA, ioB) => IO[Either[A, B]]`
 *  - `IO.racePair(ioA, ioB) => IO[Either[(OutcomeIO[A], FiberIO[B]), (FiberIO[A], OutcomeIO[B])]]`
 */
object RacePattern extends IOApp.Simple {

  // IO.race
  def timeout[A](ioa: IO[A], duration: FiniteDuration): IO[A] = {
    IO.race(ioa, IO.sleep(duration)).flatMap {
      case Left(a) => IO(a)
      case Right(_) => IO.raiseError(new RuntimeException("Your IO was not fast enough"))
    }
  }

  def evalTimeout(): IO[Unit] =
    timeout(IO("starting IO").debug >> IO.sleep(4.seconds) >> IO("done").debug, 3.seconds).void

  val test: cats.effect.OutcomeIO[String] = ???

  // IO.racePair
  def unRace[A, B](ioa: IO[A], iob: IO[B]): IO[Either[A, B]] = {
    IO.racePair(ioa, iob).flatMap {
      case Left((_, ioBFib)) =>
        ioBFib.join.flatMap {
          case Succeeded(ioB) => ioB.map(x => Right(x))
          case _ => IO.raiseError(new RuntimeException("Issue when processing unRace iob"))
        }
      case Right((ioAFib, _)) =>
        ioAFib.join.flatMap {
          case Succeeded(ioA) => ioA.map(x => Left(x))
          case _ => IO.raiseError(new RuntimeException("Issue when processing unRace ioa"))
        }
    }
  }

  def evalUnRace(): IO[Unit] = {
    val ioA: IO[String] = IO.sleep(3.seconds) >> IO("done ioA")
    val ioB: IO[String] = IO.sleep(4.seconds) >> IO("done ioB")
    unRace(ioA, ioB).debug.void
  }

  override def run: IO[Unit] = evalUnRace()
}
