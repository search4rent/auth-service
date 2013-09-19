Auth service
===========

## Endpoints

### login to rendl system with external oauth provider token

- Endpoint url: /authservice/oauth
- Method: POST

#### Request
```json
{
    "token": "someToken23132132",
    "provider": "fb" // fb | gp | tw | rendl
}
```

#### Responses
**200 OK**
> the token is valid and the user exists in the rendis database
```json
{
    "id": "the randl user id"
}
```

**412 PRECONDITION FAILED**
> the token you provided does not grant access to the needed data on the provider (not logged in anymore, outdated)
```json
{
    "token": "thatsTheProvidedToken"
}
```


##Testing

You can receive a token with your fb user by authorising the app here:

http://www.line030.de/rendl/login.html

go to firebug/console and type `FB.getAccessToken()`


