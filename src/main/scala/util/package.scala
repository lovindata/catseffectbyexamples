package com.ilovedatajjia

import cats.effect.IO

package object util {

  // Implicit class to have extension methods on a given class
  implicit class DebugWrapper[A](io: IO[A]) {

    // _.debug to print the IO thread name & its value
    def debug: IO[A] = for {
      ioEval <- io
      threadName = Thread.currentThread().getName
      _ = println(s"[$threadName] $ioEval")
    } yield ioEval

  }

}
