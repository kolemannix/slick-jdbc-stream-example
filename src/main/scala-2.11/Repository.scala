import scala.concurrent.Future

import Model.DomainObject
import slick.backend.{ DatabaseConfig, DatabasePublisher }
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend

/**
 * Created by kolemannix (mailto:koleman@livesafemobile.com).
 */
object Repository {

  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("datastore.default")

  val db: JdbcBackend#Database = dbConfig.db

  import dbConfig.driver.api._

  class DomainObjects(tag: Tag) extends Table[DomainObject](tag, "domain") {

    def id = column[Long]("id", O.PrimaryKey)
    def detail = column[String]("detail")
    def lat = column[Float]("lat")
    def lon = column[Float]("lon")
    def secretKey = column[Option[String]]("secretKey")
    def value = column[String]("value")

    def * = (id.?, detail, lat, lon, secretKey, value) <> (DomainObject.tupled, DomainObject.unapply)
  }

  val domainObjects: TableQuery[DomainObjects] = TableQuery[DomainObjects]

  def addObjects(count: Int): Future[Unit] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val objs = (1 to count) map { x => Model.generateDomainObject }
    db.run(domainObjects ++= objs) map { _ => () }
  }

  def getById(id: Long): Future[Option[DomainObject]] = {
    val q = (for {
      obj <- domainObjects if obj.id === id
    } yield obj).result.headOption
    db.run(q)
  }

  val enableJdbcStreaming: (java.sql.Statement) ⇒ Unit = { statement ⇒
    if (statement.isWrapperFor(classOf[com.mysql.jdbc.StatementImpl])) {
      statement.unwrap(classOf[com.mysql.jdbc.StatementImpl]).enableStreamingResults()
    }
  }

  def streamDomainObjects(): DatabasePublisher[DomainObject] = {
    val query = (for (o <- domainObjects) yield o).result.transactionally
    db.stream(query.withStatementParameters(statementInit = enableJdbcStreaming))
  }

  def doStreamDomainObjects(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val query = (for (o <- domainObjects) yield o).result.transactionally
    db.stream(query.withStatementParameters(statementInit = enableJdbcStreaming)).foreach(d => println(d))
  }

  def allDomainObjects(): Future[Seq[DomainObject]] = {
    val query = (for (o <- domainObjects) yield o).result
    db.run(query)
  }
}


