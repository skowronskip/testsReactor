package edu.iis.mto.testreactor.exc3;

public interface BankService {

    void startTransaction(AuthenticationToken authCode);

    void charge(AuthenticationToken authCode, Money amount) throws InsufficientFundsException;

    void commit(AuthenticationToken authCode);

    void abort(AuthenticationToken authCode);

}
