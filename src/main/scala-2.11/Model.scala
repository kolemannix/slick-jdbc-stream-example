import scala.util.Random

/**
 * Created by kolemannix (mailto:koleman@livesafemobile.com).
 */
object Model {
  case class DomainObject(id: Option[Long], detail: String, lat: Float, lon: Float, secretKey: Option[String], value: String)

  def generateDomainObject: DomainObject = {
    DomainObject(
      id = None,
      detail = Random.nextLong().toString,
      lat = Random.nextFloat(),
      lon = Random.nextFloat(),
      secretKey = if (Random.nextBoolean()) Some(Random.nextLong().toString) else None,
      value = Random.nextLong().toString
    )
  }
}
