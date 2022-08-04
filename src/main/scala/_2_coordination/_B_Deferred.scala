package com.ilovedatajjia
package _2_coordination

import util._
import cats.effect.{Deferred, IO, IOApp}
import scala.concurrent.duration.DurationInt

object _B_Deferred extends IOApp.Simple {

  // Deferred demo (== Wait & Receive value + signal between threads)
  def iWaitYourSignal(deferred: Deferred[IO, Long]): IO[Unit] = for {
    _ <- IO(s"[iWaitYourSignal] Starting to wait your signal & value").debug
    x <- deferred.get
    _ <- IO(s"[iWaitYourSignal] Signal received with the value $x").debug
  } yield ()
  def iWillSignalYou(deferred: Deferred[IO, Long]): IO[Unit] = for {
    _ <- IO(s"[iWillSignalYou] Starting to compute").debug
    _ <- IO.sleep(3.seconds) >> deferred.complete(0L)
    _ <- IO(s"[iWillSignalYou] Signal send along with the value 0").debug
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
    ioDeferredDemo

}
