package com.example.simon.blue426;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.amazonaws.mobile.content.*;
import com.amazonaws.mobile.util.ImageSelectorUtils;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.regions.Regions;


/**
 * Created by Ludo Reynders on 4/1/2017.
 */

public class AWS {
    public static final String S3_PREFIX_PUBLIC = "public/";
    public static final String S3_PREFIX_PRIVATE = "private/";
    public static final String S3_PREFIX_PROTECTED = "protected/";
    public static final String S3_PREFIX_UPLOADS = "uploads/";

    /** Permission Request Code (Must be < 256). */
    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 93;

    /** Upload Request Code for uploads folder action **/
    private static final int UPLOAD_REQUEST_CODE = 112;

    /** The user file manager. Used on uploads folder */
    private UserFileManager userFileManager;

    private void upload(final String bucket, final Regions region) {
        AWSMobileClient.defaultMobileClient()
                .createUserFileManager(getContext(), bucket, S3_PREFIX_UPLOADS, region,
                        new UserFileManager.BuilderResultHandler() {
                            @Override
                            public void onComplete(final UserFileManager userFileManager) {
                                if (!isAdded()) {
                                    userFileManager.destroy();
                                    return;
                                }

                                UserFilesDemoFragment.this.userFileManager = userFileManager;
                                userFileManagerCreatingLatch.countDown();
                            }
                        });

        final Activity activity = getActivity();

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            return;
        }

        // We have permission, so show the image selector.
        final Intent intent = ImageSelectorUtils.getImageSelectionIntent();
        startActivityForResult(intent, UPLOAD_REQUEST_CODE);
    }





}
