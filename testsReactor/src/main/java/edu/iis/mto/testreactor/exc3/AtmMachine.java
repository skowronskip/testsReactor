package edu.iis.mto.testreactor.exc3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AtmMachine {

    private final CardProviderService cardService;
    private final BankService bankService;
    private final MoneyDepot moneyDepot;

    public AtmMachine(CardProviderService cardService, BankService bankService, MoneyDepot moneyDepot) {
        this.cardService = cardService;
        this.bankService = bankService;
        this.moneyDepot = moneyDepot;
    }

    public Payment withdraw(Money amount, Card card) {
        validateAmount(amount);
        AuthenticationToken authCode = autorize(card);
        return performTransaction(amount, authCode);
    }

    private void validateAmount(Money amount) {
        if (amount.getAmount() <= 0 || cannotBePayedWithBanknotes(amount)) {
            throw new WrongMoneyAmountException();
        }
    }

    private boolean cannotBePayedWithBanknotes(Money amount) {
        return Banknote.forCurrency(amount.getCurrency())
                       .stream()
                       .noneMatch(banknote -> amount.getAmount() % banknote.getValue() == 0);
    }

    private AuthenticationToken autorize(Card card) {
        Optional<AuthenticationToken> authCode = cardService.authorize(card);
        if (authCode.isPresent()) {
            return authCode.get();
        }
        throw new CardAuthorizationException();
    }

    private Payment performTransaction(Money amount, AuthenticationToken authCode) {
        bankService.startTransaction(authCode);
        try {
            chargeAccount(amount, authCode);
            Payment payment = releasePayment(amount);
            bankService.commit(authCode);
            return payment;
        } catch (Exception e) {
            bankService.abort(authCode);
            throw e;
        }
    }

    private void chargeAccount(Money amount, AuthenticationToken authCode) {
        if (!bankService.charge(authCode, amount)) {
            throw new InsufficientFundsException();
        }
    }

    private Payment releasePayment(Money money) {
        List<Banknote> banknotes = preparePayment(money);
        if (!moneyDepot.releaseBanknotes(banknotes)) {
            throw new MoneyDepotException();
        }
        return new Payment(banknotes);
    }

    private List<Banknote> preparePayment(Money money) {
        List<Banknote> banknotesForCurrency = Banknote.forCurrency(money.getCurrency())
                                                      .stream()
                                                      .sorted(Collections.reverseOrder())
                                                      .collect(Collectors.toList());
        int amount = money.getAmount();
        List<Banknote> paymentBanknotes = new ArrayList<>();
        for (Banknote banknote : banknotesForCurrency) {
            while (amount >= banknote.getValue()) {
                amount = amount - banknote.getValue();
                paymentBanknotes.add(banknote);
            }
        }
        return paymentBanknotes;
    }

}
