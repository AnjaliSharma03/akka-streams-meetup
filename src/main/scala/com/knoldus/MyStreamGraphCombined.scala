package com.knoldus

import java.io.File
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ClosedShape, FlowShape}

object MyStreamGraphCombined extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("system")

  val combinedFlow = Flow.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val mapper = builder.add(Flow[String].map(new File(_)))
    val lengthZeroFilter = builder.add(Flow[File].filter(_.length() == 0))

    mapper ~> lengthZeroFilter

    FlowShape(mapper.in, lengthZeroFilter.out)
  })

  val source = Source(List("test1.txt", "test2.txt", "test3.txt"))
  val sink =
    Sink.foreach[File](
      f => println(s"Absolute path using stream: ${f.getAbsolutePath}")
    )
  val stream = source
    .via(combinedFlow)
    .to(sink)

  stream.run()

  val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val source = Source(List("test1.txt", "test2.txt", "test3.txt"))
    val sink =
      Sink.foreach[File](
        f => println(s"Absolute path using graph: ${f.getAbsolutePath}")
      )

    source ~> combinedFlow ~> sink

    ClosedShape
  })

  graph.run()

}
