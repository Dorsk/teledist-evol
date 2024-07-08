package fr.actia.teledist.evol.login;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;

public class LDAPconnect {

    private static final String LDAP_HOST = "hostAChanger";
    private static final int LDAP_PORT = 389;
    private static final String BASE_DN = "dc=actia,dc=local";

    public static boolean authenticate(String username, String password) {
        LDAPConnection connection = null;
        try {
            connection = new LDAPConnection(LDAP_HOST, LDAP_PORT);
            String userDN = "uid=" + username + "," + BASE_DN;
            connection.bind(userDN, password);
            return true; // Authentication succeeded
        } catch (LDAPException e) {
            e.printStackTrace();
            return false; // Authentication failed
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}