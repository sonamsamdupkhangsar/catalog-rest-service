package me.sonam.catalog.appstatus;

import me.sonam.catalog.util.Util;
import me.sonam.catalog.repo.*;
import me.sonam.catalog.repo.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ApplicationStatusService implements ApplicationStatusBehavior {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStatusService.class);

    @Autowired
    private ApplicationStatusRepository applicationStatusRepository;

    @Autowired
    private ApplicationServiceStatusRepository applicationServiceStatusRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public Mono<Page<ApplicationStatus>> getApplications(Pageable pageable) {
        LOG.info("get applicationstatus");

        return applicationStatusRepository.findAllBy(pageable).collectList()
                .zipWith(this.applicationStatusRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }


    @Override
    public Flux<ApplicationServiceStatus> getAppByEnv(UUID applicationId, UUID environmentId) {
        LOG.info("get app by envId");
        LocalDateTime localDateTime = Util.getMountainTime();
        LocalDate localDate = localDateTime.toLocalDate();
        final LocalDateTime startOfDay = localDate.atStartOfDay();

        Pageable pageable = PageRequest.of(0, 100, Sort.by("lastPingDateTime").descending());

        Flux<ApplicationServiceStatus> applicationServiceStatusFlux = applicationServiceStatusRepository.
                findByApplicationIdAndEnvironmentIdAndLastPingDateTimeAfter(applicationId, environmentId, startOfDay, pageable);

        Flux<ApplicationServiceStatus> flux3 = applicationServiceStatusFlux.hasElements().flatMapMany(aBoolean -> {
            Flux<ApplicationServiceStatus>  flux2 = null;
           if (aBoolean == false) {
               flux2 = applicationServiceStatusRepository.
                       findTop100ByApplicationIdAndEnvironmentIdOrderByLastPingDateTimeDesc(applicationId,
                               environmentId, pageable);
               setIdObjects(flux2);
               LOG.info("find by appId and envId without startOfDay");
               return flux2;
           }
           else {
               LOG.info("returning the startOfDay param");
               return applicationServiceStatusFlux;
           }
        });
        return flux3;
    }

    private void setIdObjects(Flux<ApplicationServiceStatus> flux) {
        LOG.info("set id objects");

        flux.doOnNext(applicationServiceStatus ->{
            Mono<Environment> environmentMono = environmentRepository.findById(applicationServiceStatus.getEnvironmentId());
            environmentMono.map(environment -> {
                LOG.info("set environment");
                applicationServiceStatus.setEnvironment(environment);
                applicationRepository.findById(applicationServiceStatus.getApplicationId()).doOnNext(application -> {
                    applicationServiceStatus.setApplication(application);
                    serviceRepository.findById(applicationServiceStatus.getServiceId()).doOnNext(service -> {
                        LOG.info("set applicationServiceStatus.service");
                        applicationServiceStatus.setService(service);
                    });
                });

                LOG.info("set ass env: {}, app: {}, service: {}",
                        applicationServiceStatus.getEnvironment(), applicationServiceStatus.getApplication(), applicationServiceStatus.getService());
                return applicationServiceStatus;
            });
        });
    }
}
