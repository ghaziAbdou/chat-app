package com.centreon.chatservice.infrastructure;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Jwt token configuration
 *
 * @author ghazi
 **/
@Validated
@Configuration
@ConfigurationProperties(prefix = "authentication.jwt")
public class JwtTokenConfigurationProperties
{
	/**
	 * The private key used to sign jwt tokens.
	 */
	@NotNull
	@Setter
	private String privateKeyPem;

	@Getter
	private ECPrivateKey privateKey;

	/**
	 * The public key used to decode jwt tokens.
	 */
	@NotNull
	@Setter
	private String publicKeyPem;

	@Getter
	private ECPublicKey publicKey;

	@NotNull
	@Setter
	@Getter
	private Duration validity;

	@PostConstruct
	public void init() throws IOException
	{
		publicKey = readPublicKey(new StringReader(publicKeyPem));
		privateKey = readPrivateKey(new StringReader(privateKeyPem));
	}

	private ECPrivateKey readPrivateKey(Reader reader)
		throws IOException
	{
			PEMParser pemParser = new PEMParser(reader);
			Object object = pemParser.readObject();
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
			KeyPair kp;
			if (object instanceof PEMEncryptedKeyPair) {
				throw new IllegalArgumentException("encrypted key");
			} else {
				PEMKeyPair ukp = (PEMKeyPair) object;
				kp = converter.getKeyPair(ukp);
			}
			return (ECPrivateKey)kp.getPrivate();
	}

	private ECPublicKey readPublicKey(Reader reader) throws IOException {
			PEMParser pemParser = new PEMParser(reader);
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
			SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
			return (ECPublicKey) converter.getPublicKey(publicKeyInfo);
	}
}
