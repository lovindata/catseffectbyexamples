package com.ilovedatajjia
package _0_io

import util._
import cats.Parallel
import cats.effect.implicits._
import cats.effect.{IO, IOApp}
import cats.implicits._

/**
 *  - Implicit Class for having `IO(_).debug` (cf [[util.Utils]])
 *  - Extend `IOApp.Simple` for a given `run` function
 */
object _B_Parallel extends IOApp.Simple {

  // An example using debug
  IO("Hey! You can have my value & running thread using _.debug").debug

  // Sequential vs Parallel
  val io1: IO[String] = IO("First IO to run").debug
  val io2: IO[String] = IO("Second IO to run").debug
  val ioSequentialRun: IO[String] = (io1, io2).mapN(_ + _).as("On same thread").debug

  val io1Parallel: IO.Par[String] = Parallel[IO].parallel(io1)
  val io2Parallel: IO.Par[String] = Parallel[IO].parallel(io2)
  val ioParallel: IO.Par[String] = (io1Parallel, io2Parallel).mapN(_ + _)
  val ioParallelRun: IO[String] = Parallel[IO].sequential(ioParallel).as("On different thread").debug

  // More easy way to use parallel run
  val ioParallelRunEasy: IO[String] = (io1, io2).parMapN(_ + _).as("On different thread").debug

  // Run(s)
  override def run: IO[Unit] =
    //ioSequentialRun.void
    //ioParallelRun.void
    ioParallelRunEasy.void

}
