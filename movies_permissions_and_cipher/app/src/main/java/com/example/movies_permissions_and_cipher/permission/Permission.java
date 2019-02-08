package com.example.movies_permissions_and_cipher.permission;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;

import com.example.movies_permissions_and_cipher.MainActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Permission {

    private WeakReference<MainActivity> wrMainActivity;
    private List<PermissionInformation> permissionsInfos;

    public Permission(MainActivity mainActivity, List<PermissionInformation> permissionsInfos) {
        this.wrMainActivity = new WeakReference<MainActivity>(mainActivity);
        this.permissionsInfos = permissionsInfos;
    }

    public boolean isPermissionGranted(Context context, String permission) {
        int permissionState = context.checkSelfPermission(permission);
        if (permissionState == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public void askPermission() {
        final MainActivity mainActivity = wrMainActivity.get();
        String[] critiquePerms = new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
        mainActivity.requestPermissions(critiquePerms, RequestCode.CRITIQUE.getCode());
    }

}
