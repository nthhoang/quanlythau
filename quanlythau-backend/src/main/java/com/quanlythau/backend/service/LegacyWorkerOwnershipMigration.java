package com.quanlythau.backend.service;

import com.quanlythau.backend.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LegacyWorkerOwnershipMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LegacyWorkerOwnershipMigration.class);

    private final NguoiDungRepository nguoiDungRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<Long, Set<Long>> contractorsByWorker = new HashMap<>();
        for (Object[] pair : nguoiDungRepository.findLegacyWorkerContractorPairs()) {
            Long workerId = ((Number) pair[0]).longValue();
            Long contractorId = ((Number) pair[1]).longValue();
            contractorsByWorker.computeIfAbsent(workerId, ignored -> new HashSet<>()).add(contractorId);
        }

        int migrated = 0;
        int ambiguous = 0;
        for (Map.Entry<Long, Set<Long>> entry : contractorsByWorker.entrySet()) {
            if (entry.getValue().size() != 1) {
                ambiguous += nguoiDungRepository.disableAmbiguousLegacyWorker(entry.getKey());
                continue;
            }
            Long contractorId = entry.getValue().iterator().next();
            migrated += nguoiDungRepository.assignLegacyWorkerToContractor(entry.getKey(), contractorId);
        }
        if (migrated > 0) {
            log.info("Assigned {} legacy worker accounts to their uniquely identified contractor", migrated);
        }
        if (ambiguous > 0) {
            log.warn("Disabled {} shared legacy worker accounts to prevent cross-contractor access; their history remains available to contractors", ambiguous);
        }
    }
}
