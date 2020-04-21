package dns;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import dns.UserInfo;

public class NeiborDiscovery {

    private static Set<UserInfo> userInfos;
    private static String localAddr;
    private static JmDNS jmdns;

    public NeiborDiscovery(JmDNS jmdns) throws UnknownHostException {
        userInfos = new HashSet<UserInfo>();
        localAddr = InetAddress.getLocalHost().getHostAddress();
        jmdns.addServiceListener("_test._tcp.local.", new NeiborListener());
    }

    public Set<UserInfo> getNeibors() {
        return userInfos;
    }

    private static class NeiborListener implements ServiceListener {
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
        	System.out.println("GG");
        	ServiceInfo info = event.getInfo();
            String name = info.getName();
            String ipAddr = info.getInet4Addresses()[0].getHostAddress();

            if (!ipAddr.equals(localAddr)) {
                userInfos.add(new UserInfo(name, ipAddr));

                // Debug
                System.out.printf("(%s, %s) has added\n", name, ipAddr);
            }
        }
    }
}