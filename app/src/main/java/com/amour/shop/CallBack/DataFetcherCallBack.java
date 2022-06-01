package com.amour.shop.CallBack;

import java.util.Map;


public interface DataFetcherCallBack {
    void dialogResult(Map<String, Object> data);
    void dialogResult(Object obj, String func, boolean IsSuccess);
    }






