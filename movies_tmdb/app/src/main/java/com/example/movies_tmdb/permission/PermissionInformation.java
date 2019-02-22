package com.example.movies_tmdb.permission;

import java.util.Objects;

public class PermissionInformation {

    private String permission;
    private boolean isGranted;

    public PermissionInformation(String permission) {
        this.permission = permission;
        this.isGranted = false;
    }

    public PermissionInformation(String permission, Boolean isGranted) {
        this.permission = permission;
        this.isGranted = isGranted;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isGranted() {
        return isGranted;
    }

    public void setGranted(boolean granted) {
        isGranted = granted;
    }

    @Override
    public String toString() {
        return "PermissionInformation{" +
                "permission='" + permission + '\'' +
                ", isGranted=" + isGranted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionInformation that = (PermissionInformation) o;
        return isGranted == that.isGranted &&
                Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permission, isGranted);
    }
}
