package realworld.com.articles.comments

import org.scalatest.time.{Seconds, Span}
import realworld.com.BaseServiceTest
import realworld.com.articles.JdbcArticleStorage
import realworld.com.test_helpers.{Articles, Authors, Comments}
import realworld.com.users.JdbcUserStorage
import realworld.com.utils.{DatabaseCleaner, InMemoryPostgresStorage}

class CommentStorageTest extends BaseServiceTest {
  override def afterEach(): Unit = {
    DatabaseCleaner.cleanDatabase(InMemoryPostgresStorage.databaseConnector)
    super.afterEach()
  }
  "createComment and getComments" when {
    "return comments and create comments" in new Context {
      awaitForResult(
        for {
          u <- userStorage.saveUser(Authors.normalAuthor)
          commentUser <- userStorage.saveUser(Authors.normalAuthor.copy(email = "second email", username = "second usernam"))
          a <- articleStorage.createArticle(Articles.normalArticle.copy(authorId = u.id))
          _ <- commentStorage.createComment(Comments.normalComment.copy(authorId = commentUser.id, articleId = a.id))
          _ <- commentStorage.createComment(
            Comments.normalComment.copy(id = 2, body = "second comment", authorId = commentUser.id, articleId = a.id))
          comments <- commentStorage.getComments(a.id)
        } yield {
          comments shouldBe Seq(
            Comments.normalComment.copy(articleId = a.id, authorId = commentUser.id),
            Comments.normalComment.copy(id = 2, body = "second comment", articleId = a.id, authorId = commentUser.id))
        }
      )
    }
  }

  "deleteComment" when {
    "delete a comment" in new Context {
      awaitForResult(for {
        u <- userStorage.saveUser(Authors.normalAuthor)
        a <- articleStorage.createArticle(Articles.normalArticle.copy(authorId = u.id))
        c <- commentStorage.createComment(Comments.normalComment.copy(authorId = u.id, articleId = a.id))
        res <- commentStorage.deleteComments(a.id)
        comments <- commentStorage.getComments(c.id)
      } yield {
        res shouldBe 0
        println(comments)
        comments.length shouldBe 0
      })
    }
  }

  trait Context {
    val commentStorage: CommentStorage = new JdbcCommentStorage(
      InMemoryPostgresStorage.databaseConnector)
    val articleStorage = new JdbcArticleStorage(
      InMemoryPostgresStorage.databaseConnector)
    val userStorage = new JdbcUserStorage(
      InMemoryPostgresStorage.databaseConnector)
  }
}
//package realworld.com.users
//
//import java.sql.{ JDBCType, Timestamp }
//import java.util.Date
//
//import realworld.com.BaseServiceTest
//import realworld.com.core.User
//import realworld.com.profile.Profile
//import realworld.com.utils.{ DatabaseCleaner, InMemoryPostgresStorage }
//
//class UserStorageTest extends BaseServiceTest {
//  override def beforeEach(): Unit = {
//    DatabaseCleaner.cleanDatabase(InMemoryPostgresStorage.databaseConnector)
//    super.beforeEach()
//  }
//
//  "getUserByUsername" when {
//    "return profile by id" in new Context {
//      awaitForResult(for {
//        users <- userStorage.getUsers()
//        _ <- userStorage.saveUser(testUser1)
//        _ <- userStorage.saveUser(testUser2)
//        maybeProfile <- userStorage.getUserByUsername(testUser2.username)
//      } yield {
//        maybeProfile shouldBe Some(testUser2)
//      })
//    }
//  }
//  "follow" when {
//    "success" in new Context {
//      awaitForResult(for {
//        a <- userStorage.saveUser(testUser1)
//        b <- userStorage.saveUser(testUser2)
//        successFlag <- userStorage.follow(a.id, b.id)
//      } yield {
//        successFlag shouldBe 1
//      })
//    }
//  }
//
//  "isFollowing" when {
//    "return true" in new Context {
//      awaitForResult(for {
//        a <- userStorage.saveUser(testUser1)
//        b <- userStorage.saveUser(testUser2)
//        _ <- userStorage.follow(a.id, b.id)
//        isFollowing <- userStorage.isFollowing(a.id, b.id)
//      } yield true shouldBe true)
//    }
//
//    "return false" in new Context {
//      awaitForResult(for {
//        a <- userStorage.saveUser(testUser1)
//        b <- userStorage.saveUser(testUser2)
//        _ <- userStorage.follow(a.id, b.id)
//        isFollowing <- userStorage.isFollowing(b.id, a.id)
//      } yield isFollowing shouldBe false)
//    }
//  }
//  trait Context {
//    val userStorage: UserStorage = new JdbcUserStorage(InMemoryPostgresStorage.databaseConnector)
//
//    def currentWhenInserting = new Timestamp((new Date).getTime)
//    def testUser(testUser: TestUser) = User(testUser.userId, testUser.username, testUser.password, testUser.email, None, image = None, createdAt = currentWhenInserting, updatedAt = currentWhenInserting)
//
//    val testUser1 = testUser(TestUser(1, "username-1", "username-email-1", "user-password-1"))
//    val testUser2 = testUser(TestUser(2, "username-2", "username-email-2", "user-password-2"))
//
//    case class TestUser(userId: Long, username: String, email: String, password: String)
//  }
//}