package com.ilovedatajjia
package _2_coordination.old

import util._

import cats.effect.{IO, IOApp, Ref}
import cats.implicits._

import scala.concurrent.duration._

/**
 * CCL
 *  - Initialization at starts of 'for' (IO.ref(_))
 *  - "_.set" IO[Unit], "_.get" IO[A], "_.setAndGet" IO[A]
 *  - "_.update" IO[Unit], "_.updateAndGet" IO[A], "_.modify" IO[B]
 */
object Ref extends IOApp.Simple {

  // Tokenizer unsafe
  def demoUnSafe: IO[Unit] = {
    var count = 0

    def tokenizerCount(text: String): IO[Unit] = {
      val splitsCount = text.split(" ").length
      for {
        _ <- IO("Start counting...").debug
        newCount <- IO(count + splitsCount)
        _ <- IO(s"Write newCount $newCount to current count").debug
        _ <- IO(count += newCount)
      } yield ()
    }

    List("J'aime le KFC c'est trop bon le poulet", "Coucou toi t'es moche", "YES YES YES YES YES!!")
      .map(tokenizerCount)
      .parSequence
      .void
  }

  def demoSafe: IO[Unit] = {
    val count: IO[Ref[IO, Int]] = IO.ref(0)

    def tokenizerCount(text: String, refIO: Ref[IO, Int]): IO[Unit] = {
      val splitsCount = text.split(" ").length
      for {
        _ <- IO("Start counting...").debug
        msgNewCount <- refIO.modify { x =>
          val z = x + splitsCount
          (z, s"Write newCount $z to current count")
        }
        _ <- IO(msgNewCount).debug
      } yield ()
    }

    for {
      refCount <- count
      _ <- List("J'aime le KFC c'est trop bon le poulet", "Coucou toi t'es moche", "YES YES YES YES YES!!")
        .map(tokenizerCount(_, refCount))
        .parSequence
        .void
    } yield ()

  }

 // Ticking unsafe
  def tickingClockImpure: IO[Unit] = {

    var incrementCounts: Long = 0L
    def incrementer: IO[Unit] = for {
      _ <- IO.sleep(1.second)
      _ <- IO(System.currentTimeMillis()).debug
      _ <- IO(incrementCounts += 1)
      _ <- incrementer
    } yield ()

    def printerStateIncrement: IO[Unit] = for {
      _ <- IO.sleep(5.seconds)
      _ <- IO(s"incremented: $incrementCounts").debug
      _ <- printerStateIncrement
    } yield ()

    for {
      _ <- (incrementer, printerStateIncrement).parTupled
    } yield ()

  }

  def tickingClockPure: IO[Unit] = {
    // DURING "run" => /!\ Remember that IO is not evaluated until the de-wrap

    def incrementer(refCounts: Ref[IO, Long]): IO[Unit] = for {
      _ <- IO.sleep(1.second)
      _ <- IO(System.currentTimeMillis()).debug
      _ <- refCounts.update(_ + 1)
      _ <- incrementer(refCounts)
    } yield ()

    def printerStateIncrement(refCounts: Ref[IO, Long]): IO[Unit] = for {
      _ <- IO.sleep(5.seconds)
      evalValue <- refCounts.get
      _ <- IO(s"Incremented $evalValue").debug
      _ <- printerStateIncrement(refCounts)
    } yield ()

    for {
      refCounts <- IO.ref(0L) // START A REFERENCE AND ITS EVALUATED BECAUSE OF DE-WRAP
      _ <- (incrementer(refCounts), printerStateIncrement(refCounts)).parTupled
    } yield ()

  }

  override def run: IO[Unit] = tickingClockPure

}
