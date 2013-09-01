Auth service
===========

*OAuth login*
- POST /auth-service/oauth

Request:
```
{
    "token": "someToken23132132",
    "provider": "fb" // fb | gp | tw | rendl
}
```

Response successfull
200
```
{
    "email": "email@mail.com"
}
```

otherwise 401

