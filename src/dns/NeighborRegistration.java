package dns;

import java.io.IOException;
import java.net.InetAddress;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class NeighborRegistration {
    private JmDNS jmdns;
    private ServiceInfo serviceInfo;
    private static boolean finished;

    private final String type = "_test._tcp.local.";
    private final int port = 6666;

    public NeighborRegistration(InetAddress localAddr, String username) {
        try {
            jmdns = JmDNS.create(localAddr, "jmdnsRegister");
            serviceInfo = ServiceInfo.create(type, username, port, "path=index.html");

            jmdns.registerService(serviceInfo);

            finished = true;
        } catch(IOException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(
            new Thread(new Runnable() {

                @Override
                public void run() {
                    jmdns.unregisterAllServices();
                }
            }
        ));
    }

    public static boolean finished() {
        return finished;
    }
}