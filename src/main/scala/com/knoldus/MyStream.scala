package com.knoldus

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import java.io.File

object MyStream extends App {

  implicit val actorSystem = ActorSystem("system")
  implicit val actorMaterializer = ActorMaterializer()

  val source = Source(List("test1.txt", "test2.txt", "test3.txt"))
  val mapper = Flow[String].map(new File(_))
  val existsFilter = Flow[File].filter(_.exists())
  val lengthZeroFilter = Flow[File].filter(_.length() != 0)
  val sink =
    Sink.foreach[File](f => println(s"Absolute path: ${f.getAbsolutePath}"))

  val stream = source
    .via(mapper)
    .via(existsFilter)
    .via(lengthZeroFilter)
    .to(sink)

  stream.run()
}
