package com.ilovedatajjia
package util

import cats.effect.IO

object Utils {

  implicit class DebugWrapper[A](io: IO[A]) {

    def debug: IO[A] = for {
      ioEval <- io
      threadName = Thread.currentThread().getName
      _ = println(s"[$threadName] $ioEval")
    } yield ioEval

  }

}
