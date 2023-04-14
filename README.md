# RastroJWT
A standalone Kotlin project for signing JWTs for Rastro

## Pre-requisites

You need to have Docker installed

## Instructions

1. Edit file `GeneratekeyApplication.kt` under `src/main/kotlin/nl/ngti/rastrojwt`
   1. Change the value of `context` to the desired context
   2. Change the value of `issuer` to the desired issuer
   3. Change the value of `privateKey` to the privateKey from the context
   4. Change the value of `publicKey` to the publicKey from the context
2. Run the application with Docker
   1. `docker build . -t rastrojwt`
   2. `docker run rastrojwt`