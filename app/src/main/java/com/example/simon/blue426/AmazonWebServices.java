package com.example.simon.blue426;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.util.List;

/**
 * Created by simon on 3/30/2017.
 */

public interface AmazonWebServices {

    TransferUtility transferUtility = null;
    List<TransferObserver> observers = null;

    void InitializeAWS();
    void UploadAWS();
    void DownloadAWS();

}
