package com.ilovedatajjia
package p3coordination

import cats.effect.{Deferred, IO, IOApp, Ref}
import cats.implicits._
import util.Utils._
import scala.concurrent.duration._
import scala.collection.immutable.Queue
import scala.util.Random

/**
 * CCL
 *  - _.enqueue(_) AND _.dequeue(_) AND _.isEmpty for Queue (scala.collection.immutable.Queue)
 *  - Mutex _.acquire AND _.release
 */
object MutexCoor extends IOApp.Simple {

  def lockingTask(id: Int, mutex: Mutex): IO[Int] = for {
    _ <- IO(s"[task $id] Waiting for permission to start").debug
    _ <- mutex.acquire
    _ <- IO(s"[task $id] Starting task").debug
    generatedInt <- IO.sleep(1.second) >> IO(Random.nextInt(100))
    _ <- IO(s"[task $id] Generated is $generatedInt").debug
    _ <- mutex.release
    _ <- IO(s"[task $id] Release permission").debug
  } yield generatedInt

  def demoLaunch: IO[Unit] = for {
    mutexCreated <- Mutex.create
    _ <- (1 to 10).toList.parTraverse(lockingTask(_, mutexCreated))
  } yield ()

  override def run: IO[Unit] = demoLaunch

}

case class State(locked: Boolean, queue: Queue[Deferred[IO, Unit]])

object Mutex {

  def create: IO[Mutex] = for {
    refState <- IO.ref(State(locked = false, queue = Queue.empty[Deferred[IO, Unit]]))
  } yield Mutex(refState)

}

case class Mutex(internalState: Ref[IO, State]) {

  def acquire: IO[Unit] = for {
    createDefer <- IO.deferred[Unit]
    _ <- internalState.modify {
      case State(false, _) =>
        (State(locked = true, queue = Queue.empty[Deferred[IO, Unit]]), IO.unit)
      case State(true, queue: Queue[Deferred[IO, Unit]]) =>
        (State(locked = true, queue = queue.enqueue(createDefer)), createDefer.get)
    }.flatten // If I want my code to be executed I need on the same IO level
  } yield ()

  def release: IO[Unit] = internalState.modify {
    case State(false, _) => (State(locked = false, queue = Queue.empty[Deferred[IO, Unit]]), IO.unit)
    case State(true, queue) =>
      if (queue.isEmpty) (State(locked = false, queue = Queue.empty[Deferred[IO, Unit]]), IO.unit)
      else {
        val (sigFromQueue: Deferred[IO, Unit], newQueue: Queue[Deferred[IO, Unit]]) = queue.dequeue
        (State(locked = true, queue = newQueue), sigFromQueue.complete().void)
      }
  }.flatten

}