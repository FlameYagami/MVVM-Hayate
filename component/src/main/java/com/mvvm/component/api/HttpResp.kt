package com.mvvm.component.api

/**
 * Created by FlameYagami on 2016/8/4.
 */

class HttpResp<T> {
    var errCode: Int = 0
    var errMsg: String? = null
    var result: T? = null
}
