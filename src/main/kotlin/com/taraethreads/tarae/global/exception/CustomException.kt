package com.taraethreads.tarae.global.exception

class CustomException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)
