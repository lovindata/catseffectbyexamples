package com.ilovedatajjia
package _2_coordination

import util.DebugWrapper
import cats.effect.std.CyclicBarrier
import cats.effect.{IO, IOApp}
import cats.implicits._
import scala.concurrent.duration.{DurationInt, FiniteDuration}

object _E_CyclicBarrier extends IOApp.Simple {

  // Define multiple IO run(s)
  val iosParam: List[(String, FiniteDuration)] = List(
    // (<id>, <computationDuration>)
    ("0", 3.seconds),
    ("1", 10.seconds),
    ("2", 5.second),
    ("3", 13.seconds),
    ("4", 7.seconds),
    ("5", 1.seconds),
  )
  def iosRun(id: String, computationDuration: FiniteDuration, varCcBarrier: CyclicBarrier[IO]): IO[Unit] = for {
    _ <- IO(s"[ioRun$id] Starting computation").debug >> IO.sleep(computationDuration)
    _ <- IO(s"[ioRun$id] Finished computation & Waiting for at least 2 finished").debug >> varCcBarrier.await
    _ <- IO(s"[ioRun$id] Yes finally free!").debug
  } yield ()

  // Define one sync run by 2
  val ioSyncBy2: IO[Unit] = for {
    varCcBarrier <- CyclicBarrier[IO](2)
    _ <- iosParam.parTraverse {
      case (id, computationDuration) => iosRun(id, computationDuration, varCcBarrier) // Blocking parallel start
    }
  } yield ()

  // Run(s)
  override def run: IO[Unit] = ioSyncBy2

}
