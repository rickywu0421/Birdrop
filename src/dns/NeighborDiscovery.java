package dns;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import dns.UserInfo;

public class NeighborDiscovery {

    private static List<UserInfo> userInfos;
    private static JmDNS jmdns;
    private static InetAddress localAddr;
    private static boolean finished;

    private static final String type = "_test._tcp.local.";

    public NeighborDiscovery(InetAddress localAddr) throws UnknownHostException {
        this.localAddr = localAddr;
        userInfos = new ArrayList<UserInfo>();

        try {
            jmdns = JmDNS.create(localAddr, "jmdnsDiscover");
            
            jmdns.addServiceListener(type, new NeighborListener());

            finished = true;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static List<UserInfo> getNeighbors() {
        return userInfos;
    }

    private static class NeighborListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            ServiceInfo info = event.getInfo();
            String name = info.getName();
            String ipAddr = info.getInet4Addresses()[0].getHostAddress();

            userInfos.remove(new UserInfo(name, ipAddr));

            // Debug
            System.out.printf("(%s, %s) has removed\n", name, ipAddr);
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            ServiceInfo info = event.getInfo();
            String name = info.getName();
            String ipAddr = info.getInet4Addresses()[0].getHostAddress();

            if (!ipAddr.equals(localAddr) && !userInfos.contains(new UserInfo(name, ipAddr))) {
                userInfos.add(new UserInfo(name, ipAddr));

                // Debug
                System.out.printf("(%s, %s) has added\n", name, ipAddr);
            }
        }
    }

    public static boolean finished() {
        return finished;
    }
}