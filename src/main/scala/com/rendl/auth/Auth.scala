package com.rendl.auth

import org.elasticsearch.index.query.{BoolQueryBuilder, QueryStringQueryBuilder, QueryBuilders}
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator
import com.rendl.auth.service.elasticsearch.ElasticSearchClient
import org.elasticsearch.search.SearchHits
import com.restfb.{DefaultFacebookClient, FacebookClient}
import com.restfb.types.User
import com.codahale.jerkson.Json
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import com.restfb.exception.FacebookException

/**
 * The delegate performing some cool stuff
 */
class Auth{
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
    val queryToken: QueryStringQueryBuilder = QueryBuilders
      .queryString(credentials.token)
      .field("token")
      .defaultOperator(Operator.OR)

    val queryBool: BoolQueryBuilder = QueryBuilders
      .boolQuery()
      .must(queryToken)

    val search = ElasticSearchClient.client
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

  private def loginFb(token: String): Response = {
    try {
      val client: FacebookClient = new DefaultFacebookClient(token)
      val fbUser: User = client.fetchObject("me", classOf[User])

      Option(fbUser.getEmail) match {
        case Some(email: String) => ResponseOk(email)
        case _ => ResponseUnauthorized(fbUser.getName, fbUser.getBirthdayAsDate, fbUser.getGender)
      }
    } catch {
      case e: FacebookException => ResponsePreconditionFailed(token)
      case e => throw e
    }
  }

  private def insert(credentials: Credentials) {}


  case class Auth(token: String, provider: String, ttl: Long)
}
