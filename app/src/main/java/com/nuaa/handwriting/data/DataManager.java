package com.nuaa.handwriting.data;

import com.nuaa.handwriting.model.AddListRequest;
import com.nuaa.handwriting.model.BaseResponse;
import com.nuaa.handwriting.model.CheckIndentityRequest;
import com.nuaa.handwriting.model.CheckIndentityResponse;
import com.nuaa.handwriting.model.EmptyResponse;
import com.nuaa.handwriting.model.MergeInfoRequest;
import com.nuaa.handwriting.model.PullDataRequest;
import com.nuaa.handwriting.model.QueryListRequest;
import com.nuaa.handwriting.model.QueryListResponse;
import com.nuaa.handwriting.model.VectorBean;

import java.util.List;

import io.reactivex.Observable;

public class DataManager {

    private static DataManager sDataManager = null;

    private IDataSource mDataSource;

    public DataManager(IDataSource dataSource) {
        this.mDataSource = dataSource;
    }

    public static DataManager getInstance() {
        if (sDataManager == null) {
            synchronized (DataManager.class) {
                if (sDataManager == null) {
                    sDataManager = new DataManager(RetrofitServiceUtil.create());
                }
            }
        }
        return sDataManager;
    }

    public Observable<BaseResponse<EmptyResponse>> addList(String userName, String phone, String actValue, boolean sys, List<VectorBean> list) {
        AddListRequest request = new AddListRequest(userName, phone, actValue, sys, list);
        return mDataSource.addList(request);
    }

    public Observable<BaseResponse<List<QueryListResponse>>> queryList(String userName, String cellphone) {
        QueryListRequest request = new QueryListRequest(userName, cellphone);
        return mDataSource.queryList(request);
    }

    public Observable<BaseResponse<String>> queryValue(String userName, String cellphone) {
        QueryListRequest request = new QueryListRequest(userName, cellphone);
        return mDataSource.queryValue(request);
    }

    public Observable<BaseResponse<String>> pullData() {
        PullDataRequest request = new PullDataRequest("2");
        return mDataSource.pullData(request);
    }

    public Observable<BaseResponse<CheckIndentityResponse>> checkIndentity(String userName, String phone) {
        CheckIndentityRequest request = new CheckIndentityRequest(userName, phone);
        return mDataSource.checkIndentity(request);
    }

    public Observable<BaseResponse<EmptyResponse>> mergeInfo(String userName, String phone, String type, List<String> list) {
        MergeInfoRequest request = new MergeInfoRequest(userName, phone, type, list);
        return mDataSource.mergeInfo(request);
    }

}
