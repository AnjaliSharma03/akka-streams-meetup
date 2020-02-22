package com.knoldus

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{
  Broadcast,
  Flow,
  GraphDSL,
  Merge,
  RunnableGraph,
  Sink,
  Source
}

import scala.concurrent.Future

object MyStreamGraph extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("system")

  val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val in: Source[Int, NotUsed] = Source(1 to 10)
    val out: Sink[Int, Future[Done]] = Sink.foreach[Int](
      int => println(s"Printing integers by creating a graph $int")
    )

    val broadcast = builder.add(Broadcast[Int](2))
    val merge = builder.add(Merge[Int](2))

    val f1, f2, f3, f4 = Flow[Int].map(_ + 10)

    in ~> f1 ~> broadcast ~> f2 ~> merge ~> f3 ~> out
    broadcast ~> f4 ~> merge

    ClosedShape
  })

  graph.run()

}
