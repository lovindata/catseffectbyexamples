package com.ilovedatajjia
package _1_concurrency

import util._
import cats.effect.{IO, IOApp}
import scala.concurrent.duration.DurationInt

object _C_Race extends IOApp.Simple {

  // Define some IOs
  val ioSimpleRace1: IO[String] = IO.sleep(1.second) >> IO("Finished ioSimpleRace1").debug
  val ioSimpleRace2: IO[Int] = IO.sleep(2.second) >>
    IO("Finished ioSimpleRace2").debug >>
    IO(5) // <- Will be canceled because slower

  // Simple race
  val simpleRaceEither: IO[Either[String, Int]] = IO.race(ioSimpleRace1, ioSimpleRace2).debug

  // Race with Outcome & Fiber handling
  val complexRaceFiber: IO[Unit] = for {
    _ <- IO("Starting complex race").debug
    res <- IO.racePair(ioSimpleRace1, ioSimpleRace2)
    _ = res match {
      case Left((outFibRace1, fibRace2)) => () // <- Do some manual Outcome / Fiber management here
      case Right((fibRace1, outFibRace2)) => () // <- Do some manual Outcome / Fiber management here
    }
    _ <- IO("Finished complex race").debug
  } yield ()

  // Timeout pattern
  //val ioTimedOut: IO[String] = ioSimpleRace1.timeout(500.millis) // <- Will timeout
  val ioTimedOut: IO[String] = ioSimpleRace1.timeout(1500.millis) // <- Will not

  // Run(s)
  override def run: IO[Unit] =
    //simpleRaceEither.void
    //complexRaceFiber
    ioTimedOut.void

}
