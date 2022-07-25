package com.ilovedatajjia
package p3coordination

import cats.effect.std.CyclicBarrier
import cats.effect.{IO, IOApp}
import cats.implicits._
import scala.concurrent.duration._
import util.Utils._
import scala.util.Random

/**
 * CCL
 *  - CycleBarrier[IO](nbCountsNecessary)
 *  - _.await
 */
object CycleBarrierOMG extends IOApp.Simple {

  def subUser(userId: Int, cBar: CyclicBarrier[IO]): IO[Unit] = for {
    _ <- IO(s"[$userId] Subscribing for FREE stuff 🤪").debug >> IO.sleep(Random.nextInt(5000).millis)
    _ <- IO(s"[$userId] Subscribed now waiting").debug >> cBar.await
    _ <- IO(s"[$userId] OMG KFC so good >.<").debug
  } yield ()

  def demoTest: IO[Unit] = for {
    cBar <- CyclicBarrier[IO](3)
    _ <- (1 to 10).toList.parTraverse(userId => subUser(userId, cBar))
  } yield ()

  override def run: IO[Unit] = demoTest
}
