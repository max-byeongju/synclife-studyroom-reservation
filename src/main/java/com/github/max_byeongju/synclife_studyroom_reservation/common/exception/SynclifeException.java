package com.github.max_byeongju.synclife_studyroom_reservation.common.exception;

import lombok.Getter;

@Getter
public class SynclifeException extends RuntimeException {

  private final ErrorCode errorCode;

  public SynclifeException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public SynclifeException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }
}
