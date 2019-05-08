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

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IDataSource {

    @POST("/verctor/addList/")
    Observable<BaseResponse<EmptyResponse>> addList(@Body AddListRequest request);

    @POST("/verctor/query")
    Observable<BaseResponse<List<QueryListResponse>>> queryList(@Body QueryListRequest request);

    @POST("/verctor/queryValue")
    Observable<BaseResponse<String>> queryValue(@Body QueryListRequest request);

    @POST("/system/pullData")
    Observable<BaseResponse<String>> pullData(@Body PullDataRequest request);

    @POST("/verctor/checkIndentity")
    Observable<BaseResponse<CheckIndentityResponse>> checkIndentity(@Body CheckIndentityRequest request);

    @POST("/verctor/mergeInfo")
    Observable<BaseResponse<EmptyResponse>> mergeInfo(@Body MergeInfoRequest request);

}
