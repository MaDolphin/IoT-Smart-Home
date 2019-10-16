/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.authz.model;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;

public class CustomAuthorizationInfo implements AuthorizationInfo {

    /**
     *
     */
    private static final long serialVersionUID = 8761506468789414656L;

    private Collection<String> roles;
    private Collection<Permission> permissions;

    public CustomAuthorizationInfo(Collection<String> roles, Collection<Permission> permissions) {
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    public Collection<String> getRoles() {
        return roles;
    }

    @Override
    public Collection<String> getStringPermissions() {
        Collection<String> res = new ArrayList<>();
        for (Permission perm : permissions) {
            res.add(perm.toString());
        }
        return res;
    }

    @Override
    public Collection<Permission> getObjectPermissions() {
        return permissions;
    }
}
