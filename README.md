[![Build status](https://github.com/szasemkov/loancalc/actions/workflows/workflow.yml/badge.svg?branch=master)](https://github.com/szasemkov/loancalc/actions/workflows/workflow.yml)

## Сервис расчета процентов кредитного договора
Реализован сервис расчета процентов кредитного договора по входным параметрам: 
сумма кредита, срок, процентная ставка, дата получения, тип платежа

## API
Ввод данных для кредита ***`/loan/create`***  
Входные данные вводим в виде JSON: \
**{ \
"ammount": ammount, \
 "term": term,\
"interestRate": 10,\
"loanDate": "2024-08-30", \
"typePayment": "ANNUIT"\
}** 

Где ammount > 0, term > 0, interestRate > 0,  loanDate >= текущая дата, typePayment = "ANNUIT" либо "DIFFER" 
(тип платежей либо аннуитетные либо дифференцированные)

После успешного ввода корректных данных происходит сохранение заявки на кредит в базе данных.

Формирование графика выплат ***`/loan-payment/create`*** на основании введенной заявки. \
Ввод также в формате JSON: \
**{\
"loanId": EXIST_LOAN_ID \
}**

## Запуск приложения
Запуск приложения осуществляется через docker-compose: 
*   docker-compose up -d

при этом стартуают docker образы java приложения, postgesql, prometheus

## Мониторинг
Для мониторинга реализована метрика `loan_count` - количество введенных договоров.
Мониторинг доступен через prometheus.\
Для построения графического dashboard можно подключить <a href="https://grafana.com/">Grafana</a>

## CI/CD процессы
Подключен github action для сборки и прогона тестов при пуше в бранчи:
* master
* dev



    



