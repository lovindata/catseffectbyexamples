# Learning - Cats Effect 3 functional style programming for asynchronous runtime

[![Generic badge](https://img.shields.io/badge/Scala-2.12.16-darkred.svg?style=plastic)](https://www.scala-lang.org/)
[![Generic badge](https://img.shields.io/badge/CatsEffect3-3.3.14-red.svg?style=plastic)](https://typelevel.org/cats-effect/)
[![Generic badge](https://img.shields.io/badge/SBT-1.6.2-blue.svg?style=plastic)](https://www.scala-sbt.org/)
[![Generic badge](https://img.shields.io/badge/OpenJDK-11-white.svg?style=plastic)](https://adoptium.net/)

![img.png](docs/front-img.jpg)

🤓📚 Learning one of the dominant Scala library for writing asynchronous codes.
The objective is to do an easy-to-access codes snippets collections of important Cats Effect 3 concepts to keep in mind.
Cats Effect 3 possesses [IO](https://typelevel.org/cats-effect/api/3.x/cats/effect/IO.html)
for wrapping & chaining codes executions.
It has also the concept of [Resource](https://typelevel.org/cats-effect/api/3.x/cats/effect/kernel/Resource.html)
for an automatic resources releasing after usage.
[Semaphore](https://typelevel.org/cats-effect/api/3.x/cats/effect/std/Semaphore.html),
[CountDownLatch](https://typelevel.org/cats-effect/api/3.x/cats/effect/std/CountDownLatch.html) and
[CyclicBarrier](https://typelevel.org/cats-effect/api/3.x/cats/effect/std/CyclicBarrier.html) well known asynchronous
programming patterns are also part of the library.


# LogBook

- Don't forget to add debug with Time
- Continue on CountDownLatches & CycleBarrier
- Think about a better way to render execution results => `catseffect3byexamples.github.io`
  - Exploration todo on GitHub pages
  - Markdown to GitHub page (with auto nicer visual)
  - _.scala to code fences inside markdown


# Main references

- [Official docs](https://typelevel.org/cats-effect/docs/getting-started)
- [ScalaDoc](https://typelevel.org/cats-effect/api/3.x/index.html)
- [RockTheJVM course](https://rockthejvm.com/p/cats-effect)