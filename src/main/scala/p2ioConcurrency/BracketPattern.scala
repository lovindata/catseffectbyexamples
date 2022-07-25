package com.ilovedatajjia
package p2ioConcurrency

import util.Utils._

import cats.effect.{IO, IOApp}

import scala.concurrent.duration._
import java.io.{File, FileReader}
import java.util.Scanner

/**
 * CCL
 *  - IO(<acquireRessource>).bracket { acquiredREssource =>
 *    <someCodes>
 *    } {
 *    <releaseRessource>
 *    }
 *  - Chaining implicit definite iteration via recursive functions
 *    def myRec =
 *    if (_)
 *    IO(_) >> ... >> myRec
 *    else
 *    IO.unit
 */
object BracketPattern extends IOApp.Simple {

  def openFileScanner(path: String): IO[Scanner] = IO(new Scanner(new FileReader(new File(path))))

  def readLineByLineIO(scanner: Scanner): IO[Unit] = {
    if (scanner.hasNextLine) {
      IO(scanner.nextLine).debug >> IO.sleep(100.milliseconds) >> readLineByLineIO(scanner)
    } else {
      IO.unit
    }
  }

  def bracketReadFile(path: String): IO[Unit] = IO(openFileScanner(path)).bracket {
    ioScanner =>
      for {
        scannerEval <- ioScanner
        _ <- readLineByLineIO(scannerEval)
      } yield ()
  } {
    ioScanner => ioScanner.map(_.close())
  }

  override def run: IO[Unit] = bracketReadFile(
    "D:\\prog\\proj\\cats-effect-learning\\cats-effect-learning\\src\\main\\scala\\bracket\\BracketPattern.scala"
  )

}
