package com.amour.shop.ApiHandler

interface DataFetcherCallBack {
    fun Result(
        obj: Any?,
        func: String?,
        IsSuccess: Boolean
    ) //    public Object ReturnResult(Object obj, String func, boolean IsSuccess);
}
