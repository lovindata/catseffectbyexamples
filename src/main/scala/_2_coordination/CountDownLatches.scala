package com.ilovedatajjia
package _2_coordination

import util._

import cats.effect.std.CountDownLatch
import cats.effect.{IO, IOApp, Resource}
import cats.implicits._

import java.io.{File, FileWriter}
import scala.concurrent.duration._
import scala.io.{BufferedSource, Source}
import scala.util.Random

/**
 * CCL
 *  - CountDownLatch[IO](nbReleaseNeeded)
 *  - _.release AND _.await
 */
object CountDownLatches extends IOApp.Simple {

  // Server stuffs
  object FileServer {
    val fileChunks: Array[String] = Array(
      "Coucou KFC",
      "Fried chicken i love so much",
      "OOOOOOH YEAH 666"
    )
    def getNbChunks: IO[Int] = IO(fileChunks.length)
    def getFileChunk(i: Int): IO[String] = IO(fileChunks(i))
  }

  // Write content to a file
  def writeToFile(fileName: String, contentToWrite: String): IO[Unit] = {
    val writer: Resource[IO, FileWriter] = Resource.make(IO(new FileWriter(new File(fileName))))(x => IO(x.close()))
    writer.use(writer => IO(writer.write(contentToWrite)))
  }

  // Append file to another file in new line
  def appendSrcFileToDestFile(srcFile: String, dstFile: String): IO[Unit] = {
    val res: Resource[IO, (BufferedSource, FileWriter)] = for {
      reader <- Resource.make(IO(Source.fromFile(new File(srcFile))))(x => IO(x.close()))
      writer <- Resource.make(IO(new FileWriter(new File(dstFile), true)))(x => IO(x.close()))
    } yield (reader, writer)

    res.use {
      case (reader, writer) => IO(writer.append(s"${reader.getLines.mkString}\n"))
    }
  }

  // Download one chunk
  def dlOne(idChunk: Int, dstFile: String, dstFolder: String, cdLatch: CountDownLatch[IO]): IO[Unit] = for {
    contentIdChunk <- FileServer.getFileChunk(idChunk)
    _ <- IO(s"[downloading $idChunk] it's me I download...").debug >> IO.sleep(Random.nextInt(2000).millis)
    _ <- writeToFile(s"$dstFolder/$dstFile.part$idChunk", contentIdChunk)
    _ <- cdLatch.release
  } yield ()

  // Start download
  def startDownload(dstFile: String, dstFolder: String): IO[Unit] = for {
    nbAll <- FileServer.getNbChunks
    cdLatch <- IO(s"Starting final count down dududuuududuu of $nbAll").debug >> CountDownLatch[IO](nbAll)
    _ <- (0 until nbAll).toList.parTraverse(dlOne(_, dstFile, dstFolder, cdLatch))
    _ <- cdLatch.await >> IO(s"Download parts finished, starts dumping all").debug
    _ <- (0 until nbAll).toList.traverse(x => appendSrcFileToDestFile(s"$dstFolder/$dstFile.part$x", s"$dstFolder/$dstFile"))
  } yield ()

  override def run: IO[Unit] = startDownload("allText.txt", "D:\\prog\\proj\\cats-effect-learning\\cats-effect-learning\\src\\main\\resources")
}
