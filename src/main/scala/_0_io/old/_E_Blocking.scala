package com.ilovedatajjia
package _0_io.old

import util._
import cats.effect.{IO, IOApp}
import scala.concurrent.duration._

object _E_Blocking extends IOApp.Simple {

  // Semantic blocking VS Actual blocking (on different blocking threads pool IO)
  val ioOnBlocking: IO[Unit] = IO.blocking {
    Thread.sleep(2000)
    println(s"[${Thread.currentThread().getName}] Computed on blocking thread pool")
  }
  val ioSemanticBlocking: IO[Unit] = IO.sleep(2.seconds) >> IO("Computed on normal CE normal thread pool")
  val runBlockingIO: IO[Unit] = for {
    _ <- ioOnBlocking
    _ <- ioSemanticBlocking.debug
    _ <- ioSemanticBlocking.debug
    _ <- ioOnBlocking
    _ <- ioOnBlocking
    _ <- ioSemanticBlocking.debug
    _ <- ioOnBlocking
  } yield ()

  // Run(s)
  override def run: IO[Unit] =
    runBlockingIO

}
