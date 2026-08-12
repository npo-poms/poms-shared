package nl.vpro.nep.service.impl;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.keepalive.KeepAliveProvider;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.Buffer;
import net.schmizz.sshj.common.SecurityUtils;

import java.io.IOException;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;

@Slf4j
final class SSHClientFactory {

    private SSHClientFactory(){
    }

    private static final Duration sshTimeout  = Duration.ofSeconds(300);
    private static final Duration sshConnectionTimeout  = Duration.ofSeconds(5);

    private static final ConcurrentMap<String, String> FINGERPRINTS = new ConcurrentHashMap<>();

    /**
     * @param hostKeys the RSA host key or if containing a semicolon one of the fingerprints supported by sshj.
     */
    static ClientHolder create(
        @NonNull final String hostKeys,
        @NonNull final String host,
        @NonNull String username,
        @NonNull String password) throws IOException {

        final DefaultConfig configuration = new DefaultConfig();
        configuration.setKeepAliveProvider(KeepAliveProvider.KEEP_ALIVE);
        final SSHClient ssh = new SSHClient(configuration);
        ssh.useCompression();

        for (String hostKey : hostKeys.split("\\s*,\\s*")) {
            String fingerprint = FINGERPRINTS.computeIfAbsent(hostKey, (hk) -> {
                if (hostKey.startsWith("MD5:") || hostKey.startsWith("SHA256:") || hostKey.startsWith("SHA1:")) {
                    // This is a fingerprint already
                    log.info("Validating {} with fingerprint {}", host, hostKey);
                    return hostKey;

                } else {
                    try {
                        byte[] keyBytes = Base64.getDecoder().decode(hostKey);
                        PublicKey key = new Buffer.PlainBuffer(keyBytes).readPublicKey();
                        String fingerPrint = SecurityUtils.getFingerprint(key);
                        log.info("Validating {} with fingerprint {}", host, fingerPrint);
                        return fingerPrint;
                    } catch (IOException ioe) {
                        log.error(ioe.getMessage());
                        return hostKey;
                    }
                }
            });
            ssh.addHostKeyVerifier(fingerprint);
        }

        ssh.setTimeout((int) sshTimeout.toMillis());
        ssh.setConnectTimeout((int) sshConnectionTimeout.toMillis());
        ssh.connect(host);
        ssh.authPassword(username, password);

        return new ClientHolder(ssh);
    }


    @ToString
    public static class ClientHolder implements Supplier<SSHClient>, AutoCloseable {
        final SSHClient client;
        @Getter
        final Instant creationTime;

        ClientHolder(SSHClient client) {
            this.client = client;
            this.creationTime = Instant.now();
        }

        @Override
        public SSHClient get() {
            return client;

        }

        @Override
        public void close() throws IOException {
            client.close();
        }
    }
}
