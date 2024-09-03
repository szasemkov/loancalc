package com.szasemkov.loancalc.metric;

import com.szasemkov.loancalc.repository.LoanRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricService {

    private static final String LOAN_COUNT = "loan_count";
    private final LoanRepository loanRepository;
    private final AtomicLong loanCount;

    public MetricService(
            MeterRegistry meterRegistry,
            LoanRepository loanRepository
    ) {
        this.loanCount = meterRegistry.gauge(LOAN_COUNT, new AtomicLong(0));
        this.loanRepository = loanRepository;
    }

    @Scheduled(fixedDelay = 5000L)
    public void collectingMetrics() {
        final long count = loanRepository.count();
        loanCount.set(count);
    }
}
