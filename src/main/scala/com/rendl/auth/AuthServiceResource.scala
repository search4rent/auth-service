package com.rendl.auth

import com.sun.jersey.spi.resource.Singleton
import javax.ws.rs.{POST, Consumes, Produces, Path}
import javax.ws.rs.core.{Response => HttpResponse, Context}
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.Response.Status
import com.restfb.{DefaultFacebookClient, FacebookClient}
import com.restfb.types.{User => FbUser}
import com.codahale.jerkson.Json
import java.util.Date

@Singleton
@Path("/")
@Produces(Array("application/json"))
@Consumes(Array("application/json"))
class AuthServiceResource {

  //@POST
  //@Path("")
  //def create(user: Auth, @Context context: HttpServletRequest): Response = {
  //  Response.status(Status.OK).entity(Map("message" -> "service stub working, now implement me faster!")).build
  //}

  val delegate: Auth = new Auth

  @POST
  @Path("oauth")
  def login(credentials: Credentials): HttpResponse = {
    delegate.auth(credentials) match {
      case result: ResponseOk => HttpResponse.status(Status.OK).entity(result).build
      case result: ResponseUnauthorized => HttpResponse.status(Status.UNAUTHORIZED).entity(result).build
      case result: ResponsePreconditionFailed => HttpResponse.status(Status.PRECONDITION_FAILED).entity(result).build
      case _ => HttpResponse.status(Status.INTERNAL_SERVER_ERROR).entity(Map("message" -> "somethign went wrong")).build
    }
  }

}

case class Credentials(token: String, provider: String)

case class Response()
case class ResponseOk(email: String) extends Response
case class ResponseUnauthorized(name: String, birthday: Date, sex: String) extends Response //todo: change sex to enum
case class ResponsePreconditionFailed(token: String) extends Response