package nl.ngti.rastrojwt.jwt

import com.nimbusds.jose.crypto.Ed25519Signer
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.OctetKeyPair
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.JWTClaimsSet
import org.springframework.stereotype.Service
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableMap
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jwt.SignedJWT
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class JwtService {

    private lateinit var publicKey: String
    private lateinit var privateKey: String
    /**
     * Generates a JWT token signed using Ed25519 algorithm
     *
     * @param username Current username
     * @param expiresInSecs TokenData expiration in seconds
     * @param tokenType Type of the token (according to the TokenType enum)
     * @param tokenAudience For which audience is this token from
     * @param properties Claims of the token (payload of the JWT)
     * @throws IllegalStateException In case the token could not be created or signed
     */
    fun generateToken(
        username: String,
        expiresInSecs: Long,
        tokenType: TokenType,
        tokenAudience: String,
        properties: Map<String, Any> = ImmutableMap.of(),
        tokenIssuer: String
    ): TokenData {
        val expiresAt = Instant.now().plus(expiresInSecs, ChronoUnit.SECONDS)

        try {
            // create Ed25519 signer with the public/private key
            val privateKey =
                OctetKeyPair.Builder(Curve.Ed25519, Base64URL(publicKey)).d(Base64URL(privateKey)).build()
            val signer = Ed25519Signer(privateKey)

            // prepare JWT with claims set
            val builder = JWTClaimsSet.Builder()
                .subject(username)
                .expirationTime(Date.from(expiresAt))
                .issuer(tokenIssuer)
                .claim(TokenData.TOKEN_TYPE_CLAIM, tokenType.toValue())
                .audience(tokenAudience)

            // add other properties to JWT
            for ((key, value) in properties) {
                builder.claim(key, value)
            }

            val signedJwt =
                SignedJWT(JWSHeader.Builder(JWSAlgorithm.EdDSA).keyID("1").build(), builder.build())

            // compute the Ed25519 signature
            signedJwt.sign(signer)

            // serialize to compact form
            val tokenValue = signedJwt.serialize()
            return TokenData(username, tokenValue, expiresAt, properties)
        } catch (e: JOSEException) {
            throw IllegalStateException("Could not create or sign the token", e)
        }
    }

    data class TokenData(val username: String, val tokenValue: String, val expiresAt: Instant,
                         val properties: Map<String, Any> = HashMap()) {
        companion object {
            const val TOKEN_TYPE_CLAIM = "token_type"
        }
    }

    enum class TokenType {
        ACCESS_TOKEN, REFRESH_TOKEN;

        @JsonValue
        fun toValue(): String {
            return this.name
        }

        companion object {
            @JsonCreator
            fun forValue(value: String): TokenType {
                Preconditions.checkNotNull(value, "Won't lookup null value")
                return valueOf(value)
            }
        }
    }
}