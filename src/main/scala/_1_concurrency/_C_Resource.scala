package com.ilovedatajjia
package _1_concurrency

import util._
import cats.effect.{IO, IOApp, Resource}
import scala.io.{BufferedSource, Source}

object _C_Resource extends IOApp.Simple {

  // Bracket pattern
  val pathFile: String = "src/main/scala/_1_concurrency/_C_Resource.scala"
  val ioBracket: IO[String] = IO(Source.fromFile(pathFile)).bracket {
    (reader: BufferedSource) => IO(reader.mkString)
  }{
    (reader: BufferedSource) => IO(reader.close())
  }.debug

  // Resource pattern
  val ioResourceMake: Resource[IO, (BufferedSource, BufferedSource)] = for {
    resource1 <- Resource.make(IO(Source.fromFile(pathFile)))((reader: BufferedSource) => IO(reader.close()))
    resource2 <- Resource.make(IO(Source.fromFile(pathFile)))((reader: BufferedSource) => IO(reader.close()))
      // Let suppose it is a different BufferedSource
  } yield (resource1, resource2)
  val ioUseResourceUse: IO[String] = for {
    res <- ioResourceMake.use {
      case (reader1: BufferedSource, reader2: BufferedSource) => IO {
        "Do some stuff with reader1 & reader2" // <- Do some stuff with reader1 & reader2
      }
    }
    _ <- IO(res).debug
  } yield res

  // Run(s)
  override def run: IO[Unit] =
    //ioBracket.void
    ioUseResourceUse.void

}
