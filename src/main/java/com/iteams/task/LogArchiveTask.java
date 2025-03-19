package com.iteams.task;

import com.iteams.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 操作日志归档定时任务
 * <p>
 * 定期执行日志归档操作，防止日志表数据量过大影响性能
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogArchiveTask {
    
    private final OperationLogService operationLogService;
    
    /**
     * 日志保留天数，默认30天
     */
    @Value("${iteams.log.keep-days:30}")
    private int keepDays;
    
    /**
     * 每月1日凌晨2点执行日志归档
     * 将超过保留天数的日志进行归档处理
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void archiveOldLogs() {
        try {
            // 计算保留日期，超过此日期的日志将被归档
            LocalDateTime archiveTime = LocalDateTime.now().minusDays(keepDays);
            log.info("开始归档{}天前的操作日志, 归档日期: {}", keepDays, archiveTime);
            
            // 执行归档
            int count = operationLogService.archiveLogs(archiveTime);
            log.info("操作日志归档完成, 共归档{}条记录", count);
        } catch (Exception e) {
            log.error("操作日志归档失败", e);
        }
    }
}
