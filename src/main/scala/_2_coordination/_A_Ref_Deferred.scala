package com.ilovedatajjia
package _2_coordination

import util._
import cats.effect.{Deferred, IO, IOApp, Ref}
import scala.concurrent.duration.DurationInt

object _A_Ref_Deferred extends IOApp.Simple {

  // Ref demonstration (== Thread-safe atomic mutable variable)
  val aRefForDemo: IO[Ref[IO, Long]] = IO.ref(0L) // Define a Ref (it is an IO operation)
  val ioRefDemo: IO[Unit] = for {
    ref <- aRefForDemo
    res0 <- ref.get
    _ <- IO(s"res0 == $res0").debug
    res1 <- ref.set(1L)
    _ <- IO(s"res1 == $res1").debug
    res2 <- ref.getAndSet(2L)
    _ <- IO(s"res2 == $res2").debug // And ref == 2L
    res3 <- ref.update(_ + 1L)
    _ <- IO(s"res3 == $res3").debug // And ref == 3L
    res4 <- ref.getAndUpdate(_ + 1L)
    _ <- IO(s"res4 == $res4").debug // And ref == 4L
    res5 <- ref.updateAndGet(_ + 1L)
    _ <- IO(s"res5 == $res5").debug
    res6 <- ref.modify((x: Long) => (x + 1L, s"The final value is $x"))
    _ <- IO(s"res6 == $res6").debug
  } yield ()

  // Deferred demo (== block & go signal between thread)
  def iWaitYourSignal(deferred: Deferred[IO, Long]): IO[Unit] = for {
    _ <- IO(s"[${java.time.LocalDateTime.now().toLocalTime}] [iWaitYourSignal] " +
      s"Starting to wait your signal & value").debug
    x <- deferred.get
    _ <- IO(s"[${java.time.LocalDateTime.now().toLocalTime}] [iWaitYourSignal] " +
      s"Signal received with the value $x").debug
  } yield ()
  def iWillSignalYou(deferred: Deferred[IO, Long]): IO[Unit] = for {
    _ <- IO(s"[${java.time.LocalDateTime.now().toLocalTime}] [iWillSignalYou] " +
      "Starting to compute").debug
    _ <- IO.sleep(3.seconds) >> deferred.complete(0L)
    _ <- IO(s"[${java.time.LocalDateTime.now().toLocalTime}] [iWillSignalYou] " +
      s"Signal send along with the value 0").debug
  } yield ()
  val ioDeferredDemo: IO[Unit] = for {
    deferred <- IO.deferred[Long]
    fibIWaitYourSignal <- iWaitYourSignal(deferred).start
    fibIWillSignalYou <- iWillSignalYou(deferred).start
    _ <- fibIWaitYourSignal.join
    _ <- fibIWillSignalYou.join
  } yield ()

  // Run(s)
  override def run: IO[Unit] =
    //ioRefDemo
    ioDeferredDemo

}
