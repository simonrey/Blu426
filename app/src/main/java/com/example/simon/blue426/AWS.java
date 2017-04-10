package com.example.simon.blue426;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.amazonaws.mobile.AWSConfiguration;
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
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;


/**
 * Created by Ludo Reynders on 4/1/2017.
 */

public class AWS {
    public static final String S3_PREFIX_DOWNLOAD = "public/lambda/rockAndRoll.txt";
    public static final String S3_PREFIX_PRIVATE = "private/";
    public static final String S3_PREFIX_PROTECTED = "protected/";
    public static final String S3_PREFIX_UPLOADS = "uploads/discovery/";

    private static final Regions region = AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET_REGION;
    private static final String bucket = AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET;

    /** Permission Request Code (Must be < 256). */
    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 93;

    /** Upload Request Code for uploads folder action **/
    private static final int UPLOAD_REQUEST_CODE = 112;

    private Context context;

    public AWS (Context context_a) {
        context = context_a;
    }

    public void upload(final String path) {
        AWSMobileClient.defaultMobileClient()
                .createUserFileManager(context, bucket, S3_PREFIX_UPLOADS ,region, new UserFileManager.BuilderResultHandler() {
                    @Override
                    public void onComplete(final UserFileManager userFileManager) {
                        final File file = new File(path);
                        userFileManager.uploadContent(file, file.getName(), new ContentProgressListener() {

                            @Override
                            public void onSuccess(final ContentItem contentItem) {
                                // Handle successful action here
                            }

                            @Override
                            public void onProgressUpdate(final String fileName, final boolean isWaiting,
                                                         final long bytesCurrent, final long bytesTotal) {
                                // Handle progress update here
                            }

                            @Override
                            public void onError(final String fileName, final Exception ex) {
                                // Handle error case here
                            }
                        });
                    }
                });
    }

    public boolean downloadFile(final String path){


        return false;
    }





}
