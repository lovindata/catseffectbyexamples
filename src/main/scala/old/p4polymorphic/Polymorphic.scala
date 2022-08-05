package com.ilovedatajjia
package old.p4polymorphic

import util._

import cats.effect._
import cats.implicits._

import scala.concurrent.duration.DurationInt
import scala.language.higherKinds

object Polymorphic extends IOApp.Simple {

  /*
  - CCL
  - Generalize the concept of IO on other F with `MonadCancel[F]`
  - (and by importing Monad and Functor class of cats)
    - import cats.syntax.functor._ (for map)
    - import cats.syntax.flatMap._ (for flatMap)
  - import cats.effect.syntax.monadCancel._ (for .guaranteeCase)
   */
  implicit val mc: MonadCancel[IO, Throwable] = MonadCancel[IO]
  def demoMonadCancel: IO[Unit] = {
    def uncancelableCoucou[F[_]](x: F[String])(implicit mc: MonadCancel[F, Throwable]): F[String] = for {
      _ <- mc.canceled
      _ <- mc.uncancelable(_ => mc.pure("coucou"))
      res <- x
    } yield res

    uncancelableCoucou(IO("it compiles :)")).debug.void
  }

  /*
  - CCL
  - Generalize the concept Fiber on other F with `Spawn[F]`
  - (and by importing Monad and Functor class of cats)
    - import cats.syntax.functor._ (for map)
    - import cats.syntax.flatMap._ (for flatMap)
   */
  implicit val spawn: Spawn[IO] = Spawn[IO]
  def demoSpawn: IO[Unit] = {
    def spawnCoucou[F[_]](x: F[String])(implicit spawn: Spawn[F]): F[String] = for {
      fib <- spawn.start(x)
      _ <- fib.join
      res <- x
    } yield res

    spawnCoucou(IO("it compiles :)")).debug.void
  }

  /*
  - CCL
  - Generalize the concept of `Ref` and `Deferred` on other F with `Concurrent[F]`
  - (and by importing Monad and Functor class of cats)
    - import cats.syntax.functor._ (for map)
    - import cats.syntax.flatMap._ (for flatMap)
   */
  implicit val concurrent: Concurrent[IO] = Concurrent[IO]
  def demoConcurrent: IO[Unit] = {
    def concurrentCoucou[F[_]](x: F[String])(implicit concurrent: Concurrent[F]): F[String] = for {
      test <- concurrent.ref[String]("coucou")
      _ <- test.get
      res <- x
    } yield res

    concurrentCoucou(IO("it compiles :)")).debug.void
  }

  /*
  - CCL
  - Generalize the concept of `_.sleep` on other F with `Temporal[F]`
  - (and by importing Monad and Functor class of cats)
    - import cats.syntax.functor._ (for map)
    - import cats.syntax.flatMap._ (for flatMap)
   */
  implicit val temporal: Temporal[IO] = Temporal[IO]
  def demoTemporal: IO[Unit] = {
    def temporalCoucou[F[_]](x: F[String])(implicit temporal: Temporal[F]): F[String] = {
      temporal.sleep(1.second) >> x
    }

    temporalCoucou(IO("it compiles :)")).debug.void
  }

  override def run: IO[Unit] = demoMonadCancel
}
