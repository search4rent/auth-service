package com.rendl.auth

import com.sun.jersey.spi.resource.Singleton
import javax.ws.rs.{POST, Consumes, Produces, Path}
import javax.ws.rs.core.{Response => HttpResponse}
import javax.ws.rs.core.Response.Status
import com.restfb.types.{User => FbUser}

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
      case result: ResponsePreconditionFailed => HttpResponse.status(Status.PRECONDITION_FAILED).entity(result).build
      case _ => HttpResponse.status(Status.INTERNAL_SERVER_ERROR).entity(Map("message" -> "something went wrong")).build
    }
  }

}

case class Credentials(token: String, provider: String)

case class Response()
case class ResponseOk(id: String) extends Response
case class ResponsePreconditionFailed(token: String) extends Response