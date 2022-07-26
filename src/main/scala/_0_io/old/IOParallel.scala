package com.ilovedatajjia
package _0_io.old

import util.Utils._
import cats.Parallel
import cats.effect.implicits._
import cats.effect.{IO, IOApp}
import cats.implicits._

/**
 * CCL
 *  - implicit Class for having _.<my_func> (cf [[util.Utils]])
 *  - IO.Par[A] or Parallel[IO].parallel
 *  - parMapN for an auto parallel evaluation of IO
 */
object IOParallel extends IOApp.Simple {

  // Some IOs
  val str0: IO[String] = IO(s"${Thread.currentThread().getName} coucou ")
  val str1: IO[String] = IO(s"${Thread.currentThread().getName} mon chien")

  // Combine IO
  val strCombined0: IO[String] = for {
    str0eval <- str0
    str1eval <- str1
  } yield str0eval + str1eval

  // Define
  val poulet: IO[String] = IO("poulet ").debug
  val frite: IO[String] = IO("frite").debug

  // Combine IO
  val miam: IO[String] = (poulet, frite).mapN(_ + _).debug

  // Define parallel
  val pouletParallel: IO.Par[String] = Parallel[IO].parallel(poulet)
  val friteParallel: IO.Par[String] = Parallel[IO].parallel(frite)

  // Combine IO
  val miamParallel: IO.Par[String] = (pouletParallel, friteParallel).mapN(_ + _)
  val miamSeq: IO[String] = Parallel[IO].sequential(miamParallel).debug

  // Easy parallel
  val miamAuto: IO[String] = (poulet, frite).parMapN(_ + _).debug

  // Run
  override def run: IO[Unit] = miamAuto.map(println)

}
