package com.zmbdp.judge.rabbit;

import com.zmbdp.api.domain.dto.JudgeSubmitDTO;
import com.zmbdp.common.core.constants.RabbitMQConstants;
import com.zmbdp.judge.service.IJudgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JudgeConsumer {

    @Autowired
    private IJudgeService judgeService;

    /**
     * 消费者接收消息
     *
     * @param judgeSubmitDTO 接收到的对象
     */
    @RabbitListener(queues = RabbitMQConstants.OJ_WORK_QUEUE) // 监听这个队列，有消息就消费
    public void consume(JudgeSubmitDTO judgeSubmitDTO) {
        log.info("收到消息为: {}", judgeSubmitDTO);
        judgeService.doJudgeJavaCode(judgeSubmitDTO);
    }
}
