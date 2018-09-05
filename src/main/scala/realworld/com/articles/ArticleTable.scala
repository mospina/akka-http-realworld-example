package realworld.com.articles

import java.sql.Timestamp
import java.util.Date

import realworld.com.utils.DatabaseConnector

trait ArticleTable {
  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class Articles(tag: Tag) extends Table[Article](tag, "users") {
    def currentWhenInserting = new Timestamp((new Date).getTime)
    def id = column[Long]("id",  O.AutoInc, O.PrimaryKey)
    def slug = column[String]("slug")
    def title = column[String]("title")
    def description = column[String]("description")
    def body = column[String]("body")
    def authorId = column[Long]("bio")

    def createdAt = column[Timestamp]("created_at", O.Default(currentWhenInserting))

    def updatedAt = column[Timestamp]("updated_at", O.Default(currentWhenInserting))

    def * = (id, slug, title, body, description,  authorId, createdAt, updatedAt) <> ((Article.apply _).tupled, Article.unapply)
  }

  protected val articles = TableQuery[Articles]

}
