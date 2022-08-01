package com.ilovedatajjia
package _0_io

import util._
import cats.Traverse
import cats.effect.{IO, IOApp}
import cats.implicits._

object _C_Traversal extends IOApp.Simple {

  // Sequential from List[IO[_]] to IO[List[_]]
  val listIO: List[IO[Int]] = (0 until 5).toList.map(IO(_))
  val ioList1: IO[List[Int]] = Traverse[List].sequence(listIO).debug
  val ioList2: IO[List[String]] = Traverse[List].traverse(listIO)(io => io.map(_.toString).debug).debug

  // Traversal with parallelization
  val ioListParallel1: IO[List[Int]] = listIO.parSequence.debug
  val ioListParallel2: IO[List[String]] = listIO.parTraverse(io => io.map(_.toString).debug).debug

  // Run(s)
  override def run: IO[Unit] =
    //ioList1.void
    //ioList2.void
    //ioListParallel1.void
    ioListParallel2.void

}
