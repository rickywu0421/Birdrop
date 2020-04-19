package dns;

import java.util.Objects;

public class UserInfo {
    private String name;
    private String hostAddr;

    public UserInfo(String name, String hostAddr) {
        this.name = name;
        this.hostAddr = hostAddr;
    }

    public String getName() {
        return name;
    }

    public String getHostAddr() {
        return hostAddr;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
 
        UserInfo userInfo = (UserInfo) obj;
        if (!Objects.equals(name, userInfo.name)) {
            return false;
        }

        return true;
    }
}