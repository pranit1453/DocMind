package com.pranit.docmind.security.helper;

import com.pranit.docmind.security.exception.KeyExtensionException;
import com.pranit.docmind.security.exception.KeyNotLoadedException;
import com.pranit.docmind.security.exception.KeyResourceNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class LoadKey {
    private LoadKey() {
    }

    public static PrivateKey loadPrivateKey(final String pemPath) {
        validatePemFile(pemPath);
        try {
            final String key = readKeyFromFile(pemPath)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            final byte[] decoded = Base64.getDecoder().decode(key);
            final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new KeyNotLoadedException("Failed to load private key: " + pemPath);
        }
    }

    private static void validatePemFile(final String path) {
        if (path == null || path.isBlank())
            throw new KeyResourceNotFoundException("Please provide a valid pem file path");

        if (!path.toLowerCase().endsWith(".pem"))
            throw new KeyExtensionException("Only .pem key files are supported");

    }

    private static String readKeyFromFile(final String pemPath) throws IOException {
        final Path path = Path.of(pemPath);
        if (!Files.exists(path)) throw new KeyResourceNotFoundException("Resource not found: " + pemPath);
        return Files.readString(path);
    }

    public static PublicKey loadPublicKey(final String pemPath) {
        validatePemFile(pemPath);
        try {
            final String Key = readKeyFromFile(pemPath)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            final byte[] decoded = Base64.getDecoder().decode(Key);
            final X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new KeyNotLoadedException("Failed to load public key: " + pemPath);
        }
    }
}
