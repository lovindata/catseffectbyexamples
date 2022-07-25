package com.ilovedatajjia
package p3coordination

import util.Utils._

import cats.effect.std.Semaphore
import cats.effect.{IO, IOApp}
import cats.implicits._

import scala.concurrent.duration._
import scala.util.Random

/**
 * CCL
 *  - Semaphore[IO](nbResources)
 *  - _.acquire, _.release OR _.acquireN, _.releaseN
 */
object SemaphoreMonPote extends IOApp.Simple {

  def someTaskUnderSemaphores(id: Int, nbSemNeeded: Int, sem: Semaphore[IO]): IO[Int] = for {
    _ <- IO(s"[task $id] TRY acquired $nbSemNeeded").debug
    _ <- sem.acquireN(nbSemNeeded)
    countAll <- sem.count
    availableAll <- sem.available
    // critical section
    _ <- IO(s"[task $id] acquired $nbSemNeeded so now $availableAll / 3").debug
    res <- IO(s"[task $id] starting task").debug >> IO.sleep(1.second) >> IO(Random.nextInt(100))
    _ <- IO(s"[task $id] finished task").debug
    _ <- IO(s"[task $id] release $nbSemNeeded so now $availableAll / 3").debug
    // end of critical section
    _ <- sem.releaseN(nbSemNeeded)
  } yield res

  def demoSemaStyle: IO[Unit] = for {
    sem <- Semaphore[IO](2)
    _ <- (1 to 3).toList.parTraverse { id: Int =>
      someTaskUnderSemaphores(id, 1, sem)
    }
  } yield ()

  override def run: IO[Unit] = demoSemaStyle
}
