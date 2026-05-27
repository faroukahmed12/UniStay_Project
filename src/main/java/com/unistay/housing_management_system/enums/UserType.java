package com.unistay.housing_management_system.enums;

public enum UserType {
    STUDENT,
    ADMIN,
    MAINTENANCE_STAFF
}

/*
public enum UserType implements GrantedAuthority
{
    STUDENT,
    ADMIN,
    MAINTENANCE_STAFF;

    @Override
    public String getAuthority()
    {
        return "";
    }
}
*/