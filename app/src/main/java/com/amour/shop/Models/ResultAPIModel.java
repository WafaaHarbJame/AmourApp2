package com.amour.shop.Models;


public class ResultAPIModel<T> {

//    public boolean success;
//    public List<String> message = new ArrayList<>();
//{"type":"https://tools.ietf.org/html/rfc7231#section-6.5.1",
//        "title":"One or more validation errors occurred.",
//        "status":400,
//        "traceId":"00-4200bb023f08df448644d1124276555e-d6e4e0afb0ee484c-00",
//        "errors":{
//    "$.city":["The JSON value could not be converted to System.String. Path: $.city | LineNumber: 0 | BytePositionInLine: 97."]}}

    public T data;
    public String title;
    public ErrorModel error;
    public String message;
    public int status;


    public boolean isSuccessful() {
        return status == 200 || data != null;
    }
}
