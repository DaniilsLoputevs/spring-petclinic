POST http://localhost:9082/realms/petclinic/protocol/openid-connect/auth
[FormParams]
response_type: code
redirect_uri: http://localhost:8080/login/oauth2/code/keycloak
client_id: portal

HTTP/1.1 200
[Captures]
authorization_link: regex "action=\"([^\"]+)"

POST {{authorization_link}}
[FormParams]
username: user
password: user
credentialId: ""

HTTP/1.1 302
[Captures]
authorization_code: header "Location" regex "code=([^&]+)"

POST http://localhost:9082/realms/petclinic/protocol/openid-connect/token
[FormParams]
grant_type: authorization_code
code: {{authorization_code}}
client_id: portal
redirect_uri: http://localhost:8080/login/oauth2/code/keycloak

HTTP/1.1 200
[Captures]
access_token: jsonpath "$.access_token"

GET http://localhost:8080/rest/vets
Content-type: application/json
Authorization: Bearer {{access_token}}

HTTP/1.1 200
[Asserts]
jsonpath "$.totalElements" exists
