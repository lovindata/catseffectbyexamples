package com.ilovedatajjia
package _2_coordination

import util._
import cats.effect.std.Semaphore
import cats.effect.{IO, IOApp}
import scala.concurrent.duration.DurationInt

object _C_Semaphore extends IOApp.Simple {

  // Semaphore (== Resources needed for continuing an execution)
  val aSemaphore3: IO[Semaphore[IO]] = Semaphore[IO](3)
  def computeWithSemaphore(nbSemaNeeded: Int, sema: Semaphore[IO]): IO[Unit] = for {
    // Wait for resources
    _ <- IO(s"Try to get $nbSemaNeeded resource(s)").debug
    _ <- sema.acquireN(nbSemaNeeded)

    // Start computation
    _ <- IO(s"Successfully got $nbSemaNeeded resource(s) AND Start computation").debug
    _ <- IO.sleep(3.seconds) >> sema.releaseN(nbSemaNeeded)

    // Release resources
    _ <- IO(s"Finished computation AND Successfully released $nbSemaNeeded resource(s)").debug
  } yield ()
  val demoSemaphore: IO[Unit] = for {
    sema <- aSemaphore3
    fib1stComputation <- computeWithSemaphore(2, sema).start // 1st computation needing 2 semaphores
    fib2ndComputation <- computeWithSemaphore(2, sema).start // 2nd computation needing also 2 semaphores
    _ <- fib2ndComputation.join
    _ <- fib1stComputation.join
  } yield ()

  // Run(s)
  override def run: IO[Unit] =
    demoSemaphore

}
