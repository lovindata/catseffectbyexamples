package com.ilovedatajjia
package _0_io.old

import util.Utils._
import cats.Traverse
import cats.effect.implicits._
import cats.effect.{IO, IOApp}
import cats.implicits._
import scala.util.Random

/**
 * CCL
 *  - Reverse IO and Wrapper
 *  - List(_).parSequence
 *  - List(_).parTraverse(IO[_] => IO[_])
 */
object IOTraversal extends IOApp.Simple {

  // Define
  def tokenizerNLP(myString: String): Array[String] = {
    Thread.sleep(Random.nextInt(10000))
    myString.split(" ")
  }

  val toTokenize: List[IO[String]] = List(IO("Poulet frite"), IO("Steak fromage"))

  // bad IO (HARD TO WORK WITH)
  val badIOTokenize: List[IO[Int]] = toTokenize.map(_.map(x => tokenizerNLP(x).length).debug)

  // Sequential way
  val ioTokenize: IO[List[Int]] = Traverse[List].traverse(toTokenize)(_.map(x => tokenizerNLP(x).length).debug)
  val ioTokenizeJustReverse: IO[List[String]] = Traverse[List].traverse(toTokenize)(x => x.debug)
  val ioTokenizeJustReverseBest: IO[List[String]] = Traverse[List].sequence(toTokenize)

  // Parallel
  val ioTokenizeParallel: IO[List[Int]] = toTokenize.parTraverse(_.map(x => tokenizerNLP(x).length).debug)
  val ioTokenizeJustReverseParallel: IO[List[String]] = toTokenize.parTraverse(x => x.debug)
  val ioTokenizeJustReverseParallelBest: IO[List[String]] = toTokenize.parSequence

  // BEST BEST
  def bestIfProcessing(): IO[List[Int]] = {
    val toTokenize: List[IO[String]] = List(IO("Poulet frite"), IO("Steak fromage"))
    val reversed: IO[List[Int]] = toTokenize.parTraverse(_.map(x => tokenizerNLP(x).length).debug)
    //val reversed: IO[List[Array[String]]] = toTokenize.parSequence
    reversed
  }

  // Run
  override def run: IO[Unit] = bestIfProcessing().debug.void

}
