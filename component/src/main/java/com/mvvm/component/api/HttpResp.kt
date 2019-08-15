package com.mvvm.component.api

/**
 * Created by FlameYagami on 2016/8/4.
 */

class HttpResp<T> {
    var code: Int = 0
    var message: String? = null
    var result: T? = null
}
