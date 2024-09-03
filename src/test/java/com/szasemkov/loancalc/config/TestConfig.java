package com.szasemkov.loancalc.config;

import com.szasemkov.loancalc.mapper.LoanMapper;
import com.szasemkov.loancalc.mapper.LoanPaymentMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public LoanMapper getLoanMapper() {
        return Mappers.getMapper(LoanMapper.class);
    }


    @Bean
    public LoanPaymentMapper getLoanPaymentMapper() {
        return Mappers.getMapper(LoanPaymentMapper.class);
    }
}
