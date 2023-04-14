package nl.ngti.rastrojwt

import nl.ngti.rastrojwt.jwt.JwtService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.UUID


/**
 * Author: Wessel Meijer
 *
 * For generating JWTs for the Rastro Platform for Provelo.
 * Instructions:
 *
 * Put your own publicKey and privateKey in the code below.
 * Run `main` function of program (possibly through IDEA or other IDE).
 * JWT will be in console output.
 */
@SpringBootApplication
class GeneratekeyApplication

fun main(args: Array<String>) {
	val context = //"provelo"
	val issuer = //"provelo"
	val publicKey = //PUBLIC KEY HERE
	val privateKey = //PRIVATE KEY HERE

	val jwtService = JwtService()

	JwtService::class.java.getDeclaredField("privateKey").apply {
		isAccessible = true
		set(jwtService, privateKey)
	}

	JwtService::class.java.getDeclaredField("publicKey").apply {
		isAccessible = true
		set(jwtService, publicKey)
	}

	val username = UUID.randomUUID().toString()
	val jwtProperties = hashMapOf<String, Map<String, String>>()

	val roles = hashMapOf<String, String>()

	roles[context] = "ROLE_CONTEXT_ADMIN"
	jwtProperties["roles"] = roles

	val token = jwtService.generateToken(
		username,
		1296000L,
		JwtService.TokenType.ACCESS_TOKEN,
		context,
		jwtProperties,
		issuer
	).tokenValue

	println("Username: $username")
	println("JWT: $token")
}


