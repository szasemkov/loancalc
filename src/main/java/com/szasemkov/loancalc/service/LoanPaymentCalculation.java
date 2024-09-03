package com.szasemkov.loancalc.service;

import com.szasemkov.loancalc.model.Loan;
import com.szasemkov.loancalc.model.LoanPayment;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.szasemkov.loancalc.model.TypePayment.ANNUIT;

@Service
public class LoanPaymentCalculation {

    public List<LoanPayment> createLoanPayments(Loan loan) {
        if (loan.getTypePayment() == ANNUIT) {
            return calculateAnnuityPayments(loan);
        } else
            return calculateDifferentiatedPayments(loan);
    }


    // Метод для расчета аннуитетных платежей
    private List<LoanPayment> calculateAnnuityPayments(Loan loan) {
        double principal = loan.getAmount();
        double annualRate = loan.getInterestRate();
        int months = loan.getTerm();
        Long loanId = loan.getId();

        // Рассчитываем ежемесячную процентную ставку
        double monthlyRate = annualRate / 12;

        // Рассчитываем ежемесячный аннуитетный платеж с округлением до сотых
        double annuityPayment = principal * (monthlyRate * Math.pow(1 + monthlyRate, months)) / (Math.pow(1 + monthlyRate, months) - 1);
        annuityPayment = Math.round(annuityPayment * 100.0) / 100.0; // округление до сотых

        // Форматирование вывода до двух десятичных знаков
        DecimalFormat df = new DecimalFormat("#.00");

        // Начальное значение оставшегося основного долга
        double remainingPrincipal = principal;
        double totalPrincipalPaid = 0;


        LocalDate paymentDate = loan.getLoanDate().toLocalDate();
        List<LoanPayment> loanPayments = new ArrayList<LoanPayment>();
        for (int month = 1; month <= months; month++) {
            // Рассчитываем процентную часть платежа с округлением до сотых
            double interestPayment = remainingPrincipal * monthlyRate;
            interestPayment = Math.round(interestPayment * 100.0) / 100.0; // округление до сотых

            // Рассчитываем основную часть платежа с округлением до сотых
            double principalPayment = annuityPayment - interestPayment;
            principalPayment = Math.round(principalPayment * 100.0) / 100.0; // округление до сотых

            // Обновляем оставшийся основной долг
            remainingPrincipal -= principalPayment;

            // Увеличиваем общую сумму выплаченного основного долга
            totalPrincipalPaid += principalPayment;

            // Если это последний месяц, корректируем основную часть платежа
            if (month == months) {
                double correction = principal - totalPrincipalPaid;
                principalPayment += correction;
                annuityPayment = principalPayment + interestPayment;
                remainingPrincipal = 0; // Оставшийся основной долг должен быть нулевым после последнего платежа
            }


            paymentDate = paymentDate.plusMonths(1);
            LoanPayment loanPayment = LoanPayment.builder()
                    .setLoanId(loanId)
                    .setNumberPayment(month)
                    .setDatePayment(Date.valueOf(paymentDate))
                    .setAmountPayment(annuityPayment)
                    .setPrincipalDebt(principalPayment)
                    .setInterestDebt(interestPayment)
                    .setRemainingDebt(remainingPrincipal)
                    .setDescriptionPayment("Payment for " + paymentDate.format(DateTimeFormatter.ofPattern("MM/yyyy")))
                    .build();
            loanPayments.add(loanPayment);

        }

        // Проверяем, если общая сумма выплаченного основного долга меньше суммы кредита, корректируем итоговую сумму
        if (totalPrincipalPaid < principal) {
            double difference = principal - totalPrincipalPaid;
        }
        return loanPayments;
    }

    // Метод для расчета дифференцированных платежей
    private List<LoanPayment> calculateDifferentiatedPayments(Loan loan) {
        double principal = loan.getAmount();
        double annualRate = loan.getInterestRate();
        int months = loan.getTerm();
        Long loanId = loan.getId();

        // Рассчитываем ежемесячную процентную ставку
        double monthlyRate = annualRate / 12;

        // Начальное значение оставшегося основного долга
        double remainingPrincipal = principal;
        double totalInterestPaid = 0;

        DecimalFormat df = new DecimalFormat("#.00");


        List<LoanPayment> loanPayments = new ArrayList<LoanPayment>();
        LocalDate paymentDate = loan.getLoanDate().toLocalDate();
        for (int month = 1; month <= months; month++) {
            // Рассчитываем процентную часть платежа с округлением до сотых
            double interestPayment = remainingPrincipal * monthlyRate;
            interestPayment = Math.round(interestPayment * 100.0) / 100.0; // округление до сотых

            // Рассчитываем основную часть платежа с округлением до сотых
            double principalPayment = principal / months;
            principalPayment = Math.round(principalPayment * 100.0) / 100.0; // округление до сотых

            // Увеличиваем общую сумму выплаченных процентов
            totalInterestPaid += interestPayment;

            // Уменьшаем оставшийся основной долг
            remainingPrincipal -= principalPayment;

            // Выводим данные для текущего месяца с округлением до сотых
            double totalPayment = principalPayment + interestPayment;
            paymentDate = paymentDate.plusMonths(1);

            LoanPayment loanPayment = LoanPayment.builder()
                    .setLoanId(loanId)
                    .setNumberPayment(month)
                    .setDatePayment(Date.valueOf(paymentDate))
                    .setAmountPayment(totalPayment)
                    .setPrincipalDebt(principalPayment)
                    .setInterestDebt(interestPayment)
                    .setRemainingDebt(remainingPrincipal)
                    .setDescriptionPayment("Payment for " + paymentDate.format(DateTimeFormatter.ofPattern("MM/yyyy")))
                    .build();
            loanPayments.add(loanPayment);


        }

        // Проверяем, если общая сумма выплаченных процентов меньше суммы кредита, корректируем итоговую сумму
        if (totalInterestPaid < principal * (annualRate / 12) * months) {
            double difference = principal * (annualRate / 12) * months - totalInterestPaid;
        }
        return loanPayments;
    }

}
