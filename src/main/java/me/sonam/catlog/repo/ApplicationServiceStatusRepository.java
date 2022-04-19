package me.sonam.catlog.repo;

import me.sonam.catlog.repo.entity.ApplicationServiceStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ApplicationServiceStatusRepository extends ReactiveCrudRepository<ApplicationServiceStatus, UUID> {
    Mono<Integer> countByApplicationId(UUID appid);
    Mono<Integer> deleteByApplicationIdAndLocalDateTimeBefore(UUID applicationId, LocalDateTime localDateTime);

    ApplicationServiceStatus findTop1ByApplicationIdAndServiceIdAndEnvironmentIdOrderByLocalDateTimeDesc(UUID applicationId, UUID serviceId, UUID EnvironmentId);

    @Query("select a from ApplicationServiceStatus a where a.applicationId=?1 and a.localDateTime >= ?2 and a.httpStatusValue NOT between ?3 and ?4")
    Flux<ApplicationServiceStatus> findHttpStatusNot(UUID appId, LocalDateTime localDateTime, int httpStatus1, int httpsStatus2, Pageable pageable);

    @Query("select a from ApplicationServiceStatus a where a.applicationId=?1 and a.localDateTime >= ?2 and a.httpStatusValue NOT between ?3 and ?4 order by a.localDateTime desc")
    Flux<ApplicationServiceStatus> findHttpStatusNot1(UUID appId, LocalDateTime localDateTime, int httpStatus1, int httpsStatus2, Pageable pageable);

    @Query("select a from ApplicationServiceStatus a where a.applicationId=?1 and a.environmentId=?2 and a.localDateTime >= ?3 and a.httpStatusValue NOT between ?4 and ?5 order by a.localDateTime desc")
    Flux<ApplicationServiceStatus> findHttpStatusNot1(UUID appId, UUID environmentId, LocalDateTime localDateTime, int httpStatus1, int httpsStatus2, Pageable pageable);

    @Query("select a from ApplicationServiceStatus a where a.applicationId=?1 and a.environmentId=?2 and a.lastPingDateTime >= ?3 and a.httpStatusValue NOT between ?4 and ?5 order by a.lastPingDateTime desc")
    Flux<ApplicationServiceStatus> findHttpStatusNotMeeting(UUID appId, UUID environmentId, LocalDateTime localDateTime, int httpStatus1, int httpsStatus2, Pageable pageable);

    @Query("select a from ApplicationServiceStatus a where a.applicationId=?1 and a.environmentId=?2 and a.httpStatusValue NOT between ?3 and ?4 order by a.lastPingDateTime desc")
    Flux<ApplicationServiceStatus> findHttpStatus(UUID appId, UUID environmentId, int httpStatus1, int httpsStatus2, Pageable pageable);

    Mono<ApplicationServiceStatus> findTop1ByApplicationIdAndEnvironmentIdAndLastPingDateTimeAfterAndHttpStatusValueBetweenOrderByLastPingDateTimeDesc(UUID appId, UUID environmentId, LocalDateTime localDateTime, int httpStatus1, int httpsStatus2);

    Flux<ApplicationServiceStatus> findByApplicationIdAndEnvironmentIdAndLocalDateTimeAfter(UUID appId, UUID environmentId, LocalDateTime localDateTime, Pageable pageable);
    Flux<ApplicationServiceStatus> findByApplicationIdAndEnvironmentIdAndLastPingDateTimeAfter(UUID appId, UUID environmentId, LocalDateTime localDateTime, Pageable pageable);

    Flux<ApplicationServiceStatus> findTop100ByApplicationIdAndEnvironmentIdOrderByLastPingDateTimeDesc(UUID appId, UUID environmentId, Pageable pageable);

    Mono<Integer> deleteByServiceId(UUID serviceId);
}
