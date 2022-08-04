package com.ilovedatajjia
package _2_coordination

import util._
import cats.effect.{IO, IOApp, Ref}

object _A_Ref extends IOApp.Simple {

  // Ref demonstration (== Thread-safe atomic mutable variable)
  val aRefForDemo: IO[Ref[IO, Long]] = IO.ref(0L) // Define a Ref (it is an IO operation)
  val ioRefDemo: IO[Unit] = for {
    ref <- aRefForDemo
    res0 <- ref.get
    _ <- IO(s"res0 == $res0").debug
    res1 <- ref.set(1L)
    _ <- IO(s"res1 == $res1").debug
    res2 <- ref.getAndSet(2L)
    _ <- IO(s"res2 == $res2").debug // And ref == 2L
    res3 <- ref.update(_ + 1L)
    _ <- IO(s"res3 == $res3").debug // And ref == 3L
    res4 <- ref.getAndUpdate(_ + 1L)
    _ <- IO(s"res4 == $res4").debug // And ref == 4L
    res5 <- ref.updateAndGet(_ + 1L)
    _ <- IO(s"res5 == $res5").debug
    res6 <- ref.modify((x: Long) => (x + 1L, s"The final value is $x"))
    _ <- IO(s"res6 == $res6").debug
  } yield ()

  // Run(s)
  override def run: IO[Unit] =
    ioRefDemo

}
