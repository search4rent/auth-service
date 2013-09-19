package com.rendl.auth

case class UserDocument(id: String, firstName: String, lastName: String, email: String, providers: Provider)
case class Provider(facebook: String, google: String)