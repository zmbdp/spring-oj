package com.zmbdp.judge.callback;

import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.zmbdp.common.core.enums.CodeRunStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DockerStartResultCallback extends ExecStartResultCallback {

    private CodeRunStatus codeRunStatus;  // 记录执行成功还是失败

    private String errorMessage;

    private String message;

    @Override
    public void onNext(Frame frame) {
        StreamType streamType = frame.getStreamType();
        if (StreamType.STDERR.equals(streamType)) {
            if (StrUtil.isEmpty(errorMessage)) {
                errorMessage = new String(frame.getPayload());
            } else {
                errorMessage = errorMessage + new String(frame.getPayload());
            }
            codeRunStatus = CodeRunStatus.FAILED;
        } else {
            message = new String(frame.getPayload());
            codeRunStatus = CodeRunStatus.SUCCEED;
        }
        super.onNext(frame);
    }
}
