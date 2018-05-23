package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.keepalive.KeepAliveProvider;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.Base64;
import net.schmizz.sshj.common.Buffer;
import net.schmizz.sshj.common.SecurityUtils;

import java.io.IOException;
import java.security.PublicKey;
import java.time.Duration;

@Slf4j
final class SSHClientFactory {

    private SSHClientFactory(){
    }

    private static final Duration sshTimeout  = Duration.ofSeconds(300);
    private static final Duration sshConnectionTimeout  = Duration.ofSeconds(5);

    static SSHClient create(String hostKey, String host, String username, String password) throws IOException {

        final DefaultConfig configuration = new DefaultConfig();
        configuration.setKeepAliveProvider(KeepAliveProvider.KEEP_ALIVE);
        final SSHClient ssh = new SSHClient(configuration);
        ssh.useCompression();
        //ssh.loadKnownHosts();


        if (hostKey.indexOf(':') > 0) {
            ssh.addHostKeyVerifier(hostKey);
        } else {
             byte[] keyBytes = Base64.decode(hostKey);
            PublicKey key = new Buffer.PlainBuffer(keyBytes).readPublicKey();
            ssh.addHostKeyVerifier(SecurityUtils.getFingerprint(key));
        }

        ssh.setTimeout((int) sshTimeout.toMillis());
        ssh.setConnectTimeout((int) sshConnectionTimeout.toMillis());

        ssh.connect(host);

        ssh.authPassword(username, password);

        return ssh;
    }
}
