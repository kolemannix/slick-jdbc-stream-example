# slick-jdbc-stream-example
Streaming results from a MySQL store using Slick and Akka Streams

Gotchas:
As of Slick 3.1.1, streaming from MySQL will not work unless you tweak the query parameters like so:
```
val enableJdbcStreaming: (java.sql.Statement) ⇒ Unit = { statement ⇒
  if (statement.isWrapperFor(classOf[com.mysql.jdbc.StatementImpl])) {
    statement.unwrap(classOf[com.mysql.jdbc.StatementImpl]).enableStreamingResults()
  }
}
val queryStr = sql"select * from kittens;"
val query = queryStr.result.transactionally
db.stream(query.withStatementParameters(statementInit = enableJdbcStreaming))
```
