POST http://localhost:9082/realms/petclinic/protocol/openid-connect/token
[FormParams]
grant_type: client_credentials
client_id: external-integration
client_secret: AYWkFMBQl6AXsd0rYGFx7Z6hTe8IywE4
HTTP 200
[Captures]
bearer_token: jsonpath "$['access_token']"

#GET http://localhost:8080/rest/vets
#Content-type: application/json
#Authorization: Bearer {{bearer_token}}

#GET http://localhost:8080/oportal/visits/by-pet-with-vet?id=1&page=0&size=10
#Content-type: application/json
#Authorization: Bearer {{bearer_token}}

POST http://localhost:8080/oportal/owners/find-by-key-fields
Content-type: application/json
Authorization: Bearer {{bearer_token}}
```json
[
  {
    "firstName": "George",
    "lastName": "Franklin",
    "telephone": "6085551023"
  }
]
```

HTTP/1.1 200
[Captures]
owner_id: jsonpath "$[0].id"

GET http://localhost:8080/oportal/owners/{{owner_id}}/pets
Content-type: application/json
Authorization: Bearer {{bearer_token}}

POST http://localhost:8080/oportal/visits
Content-type: application/json
Authorization: Bearer {{bearer_token}}
```json
{
  "ownerId": "1",
  "petId": "1",
  "date": "2000-09-07",
  "description": "Recommended visit"
}
```

#GET http://localhost:8080/vets
###
# {"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJTbmFKNnJ5RDV6QlloMjgwZjFpQ3pKM0JXRkpKX2h1RkZ4SEZmZUJ0QWJZIn0.eyJleHAiOjE3MjI5Mzg2OTMsImlhdCI6MTcyMjkzODM5MywianRpIjoiZWVlMmRjZGUtYjQ1Ni00M2M0LTkyYWQtOWZhYTUxMjVjZTRiIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDgyL3JlYWxtcy9wZXRjbGluaWMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZjFiMjkzYmEtMGI4OS00OGJjLWI3Y2MtMDEwMDg2ZGNkODFhIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZXh0ZXJuYWwtaW50ZWdyYXRpb24iLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6ODA4MCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1wZXRjbGluaWMiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiY2xpZW50SG9zdCI6IjE3Mi4yOC4wLjEiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InNlcnZpY2UtYWNjb3VudC1leHRlcm5hbC1pbnRlZ3JhdGlvbiIsImNsaWVudEFkZHJlc3MiOiIxNzIuMjguMC4xIiwiY2xpZW50X2lkIjoiZXh0ZXJuYWwtaW50ZWdyYXRpb24ifQ.Xir0StP5VC65JmqG781ms4c0F4TdcPysy-6wQJCVtJBo_q0f-PBxW5gsEW5OND8YM9oFfR4cmWdNCoXqJUYrXZOTmpvSnfi8MHSO8useKGWYg1rqTE_s_Cb1gKAwY7HquAoSxYBUD1jGW4z9ZC1H-QGjc2pz0gC1L9AZclyhxIp_srfAshQYK7eeFshKVXNFzLp0Pk6TEAiW1U6SRnjz5yb7wnrBE4-PbPyp3VqrSh12yE4-Im-x8GbSOzjwzQ-pV3n0ThSIHH7qs2lUAu0HO6mY3In6daOgjMwAHf3rMEIZr-c9erj_xOW1zt6sClIa0sfgexLld_96F8b-KF0kWA","expires_in":300,"refresh_expires_in":0,"token_type":"Bearer","not-before-policy":0,"scope":"email profile"}* Response: (received 1476 bytes in 402 ms)
