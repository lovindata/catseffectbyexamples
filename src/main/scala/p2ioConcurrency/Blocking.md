CCL

- IO.sleep is scheduling the computation <X> seconds later => yield computation over the thread calling
- IO.blocking { _ } yield computation on a blocking thread (the thread will do nothing until computation is done) => yield computation over the thread calling
- IO.cede hint for CE3 to yield computation on another thread (not systematic)