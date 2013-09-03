Auth service
===========

## Endpoints

### login to rendl system with external oauth provider token

- Endpoint url: /auth-service/oauth
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
    "email": "email@mail.com"
}
```

**401 UNAUTHORIZED**
> the token is valid but the user is not yet registered and the user cannot be automatically get registered because of missing
> values from the provider.
> Please ask the user for his data and register.
> Will return provided data from the provider which we can prefill in registration form
```json
{
    "name": "Ali Adelfarugh",
    "birthday": 12313541, // timestamp
    "sex": "MALE" // MALE | FEMALE
}
```

**412 PRECONDITION FAILED**
> the token you provided does not grant access to the needed data on the provider (not logged in anymore, outdated)
```json
{
    "token": "thatsTheProvidedToken"
}
```


