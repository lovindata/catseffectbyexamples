package com.ilovedatajjia
package p4polymorphic

import util._

import cats.effect.{IO, IOApp, Sync}

import scala.language.higherKinds

/**
 * CCL
 *  - Generalize the concept of `_.delay` or `_.blocking` on other F with `Sync[F]`
 */
object SyncDemo extends IOApp.Simple {

  val syncIO: Sync[IO] = Sync[IO]
  val delayedIO: IO[String] = syncIO.delay {
    println("coucou")
    "coucouNotPrint"
  }
  val blockingIO: IO[Int] = syncIO.blocking {
    println("coucou")
    47
  }
  val deferIO: IO[Int] = syncIO.defer {
    println("coucou")
    IO(47)
  }

  def syncDemo: IO[Unit] = for {
    _ <- delayedIO.debug
    _ <- blockingIO.debug
    _ <- deferIO.debug
  } yield ()

  override def run: IO[Unit] = syncDemo
}
