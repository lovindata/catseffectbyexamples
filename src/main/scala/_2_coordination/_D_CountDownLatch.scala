package com.ilovedatajjia
package _2_coordination

import util.DebugWrapper
import cats.effect.std.CountDownLatch
import cats.effect.{IO, IOApp}
import cats.implicits._
import scala.concurrent.duration.{DurationInt, FiniteDuration}

object _D_CountDownLatch extends IOApp.Simple {

  // Define multiple IO run(s)
  val iosParam: List[(String, FiniteDuration)] = List(
    // (<id>, <computationDuration>)
    ("0", 3.seconds),
    ("1", 10.seconds),
    ("2", 1.second)
  )
  def iosRun(id: String, computationDuration: FiniteDuration, varCdLatch: CountDownLatch[IO]): IO[Unit] = for {
    _ <- IO(s"[ioRun$id] Starting computation").debug >> IO.sleep(computationDuration)
    _ <- IO(s"[ioRun$id] Finished computation & Release CdLatch").debug >> varCdLatch.release
  } yield ()

  // Define one sync run by 2
  val ioSyncBy2: IO[Unit] = for {
    varCdLatch <- CountDownLatch[IO](2)
    _ <- iosParam.parTraverse {
      case (id, computationDuration) => iosRun(id, computationDuration, varCdLatch).start // Unblocked parallel start
    }
    _ <- IO("[ioSyncBy2] Waiting for at least 2 finished").debug >> varCdLatch.await
    _ <- IO("[ioSyncBy2] Yes finally free!").debug
  } yield ()

  // Run(s)
  override def run: IO[Unit] = ioSyncBy2

}
