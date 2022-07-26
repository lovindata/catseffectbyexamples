package com.ilovedatajjia
package _1_concurrency

import util.Utils._

import cats.effect.kernel.Resource
import cats.effect.{IO, IOApp}

import scala.concurrent.duration._
import java.io.{File, FileReader}
import java.util.Scanner

/**
 * CCL
 *  - Resource.make(x => IO(_))(x => IO(_))
 *  - myResource.use(xWithoutIO => _)
 */
object ResourcePattern extends IOApp.Simple {

  def resourceGetter(path1: String, path2: String): Resource[IO, (Scanner, Scanner)] = for {
    scan1 <- Resource.make(IO(new Scanner(new FileReader(new File(path1)))))(x => IO(x.close()))
    scan2 <- Resource.make(IO(new Scanner(new FileReader(new File(path2)))))(x => IO(x.close()))
  } yield (scan1, scan2)

  def readInSwitch(scanner1: Scanner, scanner2: Scanner): IO[Unit] = {
    (scanner1.hasNextLine, scanner2.hasNextLine) match {
      case (true, true) =>
        IO(scanner1.nextLine()).debug >> IO(scanner2.nextLine()).debug >> IO.sleep(100.milliseconds) >> readInSwitch(scanner1, scanner2)
      case (true, _) =>
        IO(scanner1.nextLine()).debug >> IO.sleep(100.milliseconds) >> readInSwitch(scanner1, scanner2)
      case (_, true) =>
        IO(scanner2.nextLine()).debug >> IO.sleep(100.milliseconds) >> readInSwitch(scanner1, scanner2)
      case (false, false) =>
        IO.unit
    }
  }

  def runResourcesTest(path1: String, path2: String): IO[Unit] = {
    val resourcesGot: Resource[IO, (Scanner, Scanner)] = resourceGetter(path1, path2)
    resourcesGot.use {
      case (scanner1, scanner2) => readInSwitch(scanner1, scanner2)
    }
  }

  override def run: IO[Unit] = runResourcesTest(
    "D:\\prog\\proj\\cats-effect-learning\\cats-effect-learning\\src\\main\\scala\\bracketAndResource\\BracketPattern.scala",
    "D:\\prog\\proj\\cats-effect-learning\\cats-effect-learning\\src\\main\\scala\\bracketAndResource\\ResourcePattern.scala"
  )

}
