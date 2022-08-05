package com.ilovedatajjia
package _0_io

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits._

object _A_Introduction extends App {

  // Defining & Chaining (order of execution) IO
  val io1: IO[String] = IO("Welcome to IO introductions")
  val io2: IO[String] = IO("How chaining works")
  val ioChainedLazy: IO[String] = io1 >> io2 // Lazy evaluation
  val ioChainedEager: IO[String] = io1 *> io2 // Eager evaluation (Can crash)
  val ioReverseChain: IO[String] = io2 <* io1 // `<*` only existing for eager evaluation
  val ioChainedForYield: IO[String] = for {
    io1Str <- io1
    io2Str <- io2
  } yield io1Str + io2Str

  // Some functions for IO manipulation(s)
  val ioToApplyFunc: IO[Boolean] = IO(false)
  ioToApplyFunc.map(!_)
  ioToApplyFunc.flatMap(_ => IO(true))
  ioToApplyFunc.as(true)
  ioToApplyFunc.void // Gives an IO[Unit]
  ioToApplyFunc.map(println).foreverM // An IO running forever
  (ioToApplyFunc, ioToApplyFunc.map(!_)).mapN(_ || _) // Combining IO from Tuple

  // Running IO
  val ioToRun: IO[Int] = IO(0)
  val resAfterRun: Int = ioToRun.unsafeRunSync() // Retrieve the value on the main thread

}
