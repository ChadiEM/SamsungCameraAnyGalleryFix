package com.smartmadsoft.xposed.samsungcameragallery;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Selfie implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("com.sec.android.app.camera"))
            return;

        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.sec.android.app.camera.Camera", lpparam.classLoader), "onLaunchGalleryForImage", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {

                Context context = (Context) param.thisObject;
                ContentResolver contentResolver = context.getContentResolver();

                Object mEngine = XposedHelpers.getObjectField(context, "mEngine");
                Uri lastUri = (Uri) XposedHelpers.callMethod(mEngine, "getLastContentUri");
                String mime = contentResolver.getType(lastUri);

                Intent intent = new Intent();
                intent.setAction("com.android.camera.action.REVIEW");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(lastUri, mime);
                context.startActivity(intent);

                return null;
            }
        });
    }
}
