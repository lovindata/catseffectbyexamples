package com.ilovedatajjia
package p3coordination

import cats.effect.{Deferred, IO, IOApp, Ref}

import scala.concurrent.duration._
import util._

import cats.implicits._

/**
 * CCL
 *  - Initialization like Ref[IO, A] at starts of 'for'
 *  - IO.deferred[A]
 *  - _.get (for the waiting fiber) AND _.complete(myValInTypeA) (for the computing fiber)
 *  - _.guaranteeCase (for IO[_]) equivalent to "finally" if fiber terminate
 */
object DeferredCoor extends IOApp.Simple {

  // Downloading demo
  def demoDeferredFileDownload: IO[Unit] = {

    val fileParts: List[String] = List("I ", "lo", "ve ea", "t frie", "d ch", "icken! (KFC S", "O GO", "OD)! 🤪 <EOF>")

    def downloadParts(part: String, contentRef: Ref[IO, String], contentDeferred: Deferred[IO, String]): IO[Unit] = for {
      currentRef <- contentRef.updateAndGet(_ + part)
      _ <- IO.sleep(1.second) >> IO(s"[downloader] got the part '$part'").debug
      _ <- if (currentRef.endsWith("<EOF>")) contentDeferred.complete(currentRef)
      else IO.unit
    } yield ()

    def notifyFinishedDownload(contentDeferred: Deferred[IO, String]): IO[Unit] = for {
      _ <- IO("[notifier] starts downloading all...").debug
      finalDownloadRes <- contentDeferred.get
      _ <- IO(s"[notifier] downloading finished with '$finalDownloadRes'").debug
    } yield ()

    for {
      contentRef <- IO.ref("")
      contentDeferred <- IO.deferred[String]
      notifyFib <- notifyFinishedDownload(contentDeferred).start
      downloadFib <- fileParts
        .map(downloadParts(_, contentRef, contentDeferred))
        .sequence
        //.parSequence
        .start
      _ <- notifyFib.join
      _ <- downloadFib.join
    } yield ()

  }

  // Notif timer at 10
  def notifTimerAt10: IO[Unit] = {

    def notifier(signalToComplete: Deferred[IO, String]): IO[Unit] = for {
      _ <- IO("[notifier] Starting notification").debug
      receivedSignalMsg <- signalToComplete.get
      _ <- IO(s"[notifier] time's up => Received with $receivedSignalMsg").debug
    } yield ()

    def runTimer(counts: Int, signalToComplete: Deferred[IO, String]): IO[Unit] = for {
      _ <- IO.sleep(1.second)
      // gottenTimer <- refTimer.updateAndGet(_ + 1)
      _ <- IO(s"[timer] Timer at $counts").debug
      _ <- if (counts == 10) {
        signalToComplete.complete("YOYO c'est arrivé à 10")
      } else {
        runTimer(counts + 1, signalToComplete)
      }
    } yield ()

    for {
      // refIncrement <- IO.ref(0)
      deferSignal <- IO.deferred[String]
      notifFib <- notifier(deferSignal).start
      runTimerFib <- runTimer(0, deferSignal).start
      _ <- notifFib.join
      _ <- runTimerFib.join
    } yield ()

  }

  override def run: IO[Unit] = notifTimerAt10
}
