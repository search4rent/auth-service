package com.rendl.auth

import org.elasticsearch.index.query.{FieldQueryBuilder, BoolQueryBuilder, QueryStringQueryBuilder, QueryBuilders}
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator
import org.elasticsearch.search.SearchHits
import com.restfb.{DefaultFacebookClient, FacebookClient}
import com.restfb.types.User
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import com.restfb.exception.FacebookException
import java.util.UUID
import com.randl.core.servicelib.elasticsearch.ESClient
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.update.UpdateRequestBuilder
import com.codahale.jerkson.Json
import org.elasticsearch.action.index.IndexRequestBuilder

/**
 * The delegate performing some cool stuff
 */
class Auth extends ESClient{
  /**
   *
   * @param credentials
   */
  def auth(credentials: Credentials): Response = {
    // check if token already existing and still valid
    if (isLocal(credentials))    // todo: extend TTL here
      throw new NotImplementedException()
    else  // check if provider validates
      login(credentials)
  }

  /**
   * todo: implement TTL
   * @param credentials
   * @return
   */
  private def isLocal(credentials: Credentials): Boolean = {
    return false;

    val queryToken: QueryStringQueryBuilder = QueryBuilders
      .queryString(credentials.token)
      .field("token")
      .defaultOperator(Operator.OR)

    val queryBool: BoolQueryBuilder = QueryBuilders
      .boolQuery()
      .must(queryToken)

    val search = client
      .prepareSearch("auth")
      .setTypes("token")
      .setQuery(queryBool)

    println("search query:\n" + search)

    val hits: SearchHits = search.execute.actionGet().getHits

    println("hits:\n" + hits)

    if (hits.totalHits == 1) // todo: check if still alive, otherwise relogin to FB
      true
    else
      false
  }

  private def login(credentials: Credentials): Response = {
    credentials.provider match {
      case "fb" => loginFb(credentials.token)
      case _ => throw new RuntimeException("no provider for key '" + credentials.provider + "' found!")
    }
  }

  /**
   * asks fb for the userinfo, if not yet registered: create the user on db
   * @param token
   * @return
   */
  private def loginFb(token: String): Response = {
    try {
      val client: FacebookClient = new DefaultFacebookClient(token)
      val fbUser: User = client.fetchObject("me", classOf[User])

      (Option(fbUser.getId), exists(fbUser.getId, "fb")) match {
        case (Some(id: String), Some(user: UserDocument)) => {
          ResponseOk(user.id) // todo: log him in
        }
        case (Some(id: String), None) => {
          // try to create user
          val user: UserDocument = UserDocument(UUID.randomUUID.toString, fbUser.getFirstName, fbUser.getLastName, fbUser.getEmail, Provider(id, ""))
          insert(user)
          ResponseOk(user.id) // todo log him in
        }
        case _ => ResponsePreconditionFailed(token)
      }
    } catch {
      case e: FacebookException => ResponsePreconditionFailed(token)
      case e => throw e
    }
  }

  private def exists(id: String, provider: String): Option[UserDocument] = {
    val queryUser: FieldQueryBuilder = {
      provider match {
        case "fb" => QueryBuilders.fieldQuery("providers.facebook", id)
        case _ => throw new IllegalArgumentException("non specified provider: " + provider)
      }
    }

    val search = client
      .prepareSearch("user")
      .setTypes("user")
      .setQuery(queryUser)

    val hits: SearchHits = search.execute.actionGet().getHits

    println("search for user with " + provider + " id: " + id + ": \n" + queryUser)
    println("hits: " + hits.totalHits())

    if (hits.totalHits() == 0) None
    else {
      println("result from ES: " + hits.getHits.head.getSourceAsString)
      val user: UserDocument = Json.parse[UserDocument](hits.getHits.head.getSourceAsString)
      println("user: " + user)
      Option(user)
    }
  }

  private def insert(user: UserDocument): Unit = {
    val writeRequest: IndexRequestBuilder = client.prepareIndex().setIndex("user").setType("user").setId(user.id).setSource(Json.generate(user))
    val builder: BulkRequestBuilder = client.prepareBulk
    builder.add(writeRequest)
    builder.execute.actionGet

    client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet()
  }


  case class Auth(token: String, provider: String, ttl: Long)
}
