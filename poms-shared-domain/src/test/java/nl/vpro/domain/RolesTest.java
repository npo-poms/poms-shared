package nl.vpro.domain;

import org.junit.jupiter.api.Test;

class RolesTest {

    @Test
    public  void allRoles() {
        Roles.allRoles().forEach(r -> {
            System.out.println(r);
        });

    }

}
