// Parametrizing the above operation
  def seqFutToFutSeqP[T, U](seq: Seq[T], op: T => Future[U]): Future[Seq[U]] = {
    val seqOFutures = seq.map(op)
    val futureSequence = Future.sequence(seqOFutures)
    futureSequence
  }

seqFutToFutSeqP(List("raman", "Shaman"),
      (x: String) => Future(x.toLowerCase())).onComplete{
      case Success(listOfLCaseStrings) => listOfLCaseStrings.foreach(println)
      case Failure(ex) => println(ex.getMessage)
    }
