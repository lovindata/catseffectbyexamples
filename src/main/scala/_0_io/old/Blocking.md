CCL

- IO.sleep is scheduling the computation <X> seconds later => yield computation over the thread calling
- IO.blocking { _ } yield computation on a blocking thread (the thread will do nothing until computation is done) => yield computation over the thread calling
- IO.cede hint for CE3 to yield computation on another thread (not systematic)

```scala
  // Semantic blocking VS Actual blocking (on different blocking threads pool IO)
  val ioOnBlocking: IO[Unit] = IO.blocking {
    Thread.sleep(2000)
    println(s"[${Thread.currentThread().getName}] Computed on blocking thread pool")
  }
  val ioSemanticBlocking: IO[Unit] = IO.sleep(2.second) >> IO("Computed on normal CE normal thread pool")
  val runBlockingIO: IO[Unit] = for {
    _ <- ioOnBlocking
    _ <- ioSemanticBlocking.debug
    _ <- ioSemanticBlocking.debug
    _ <- ioOnBlocking
    _ <- ioOnBlocking
    _ <- ioSemanticBlocking.debug
    _ <- ioOnBlocking
  } yield ()
```