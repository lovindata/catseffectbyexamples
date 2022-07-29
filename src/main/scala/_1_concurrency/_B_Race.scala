package com.ilovedatajjia
package _1_concurrency

import util.Utils._
import cats.effect.{IO, IOApp}
import scala.concurrent.duration.DurationInt

object _B_Race extends IOApp.Simple {

  // Simple race
  val ioSimpleRace1: IO[String] = IO.sleep(1.second) >> IO("Finished ioSimpleRace1").debug
  val ioSimpleRace2: IO[Int] = IO.sleep(2.second) >>
    IO("Finished ioSimpleRace2").debug >>
    IO(5) // <- Will be canceled because slower
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

  // Run(s)
  override def run: IO[Unit] =
    //simpleRaceEither.void
    complexRaceFiber

}
