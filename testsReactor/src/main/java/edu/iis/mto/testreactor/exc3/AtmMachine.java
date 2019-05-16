package edu.iis.mto.testreactor.exc3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AtmMachine {

    private final CardProviderService cardService;
    private final BankService bankService;
    private final MoneyDepot moneyDepot;

    public AtmMachine(CardProviderService cardService, BankService bankService, MoneyDepot moneyDepot) {
        this.cardService = Objects.requireNonNull(cardService, "cardService==null");
        this.bankService = Objects.requireNonNull(bankService, "bankService==null");
        this.moneyDepot = Objects.requireNonNull(moneyDepot, "moneyDepot==null");
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
        try {
            return cardService.authorize(card);
        } catch (CardAuthorizationException e) {
            throw new AtmException(e);
        }

    }

    private Payment performTransaction(Money amount, AuthenticationToken authCode) {
        bankService.startTransaction(authCode);
        try {
            bankService.charge(authCode, amount);
            Payment payment = releasePayment(amount);
            bankService.commit(authCode);
            return payment;
        } catch (Exception e) {
            bankService.abort(authCode);
            throw new AtmException(e);
        }
    }

    private Payment releasePayment(Money money) throws MoneyDepotException {
        List<Banknote> banknotes = preparePayment(money);
        moneyDepot.releaseBanknotes(banknotes);
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
