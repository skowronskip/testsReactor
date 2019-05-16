package edu.iis.mto.testreactor.exc3;

public interface BankService {

    void startTransaction(AuthenticationToken authCode);

    boolean charge(AuthenticationToken authCode, Money amount);

    void commit(AuthenticationToken authCode);

    void abort(AuthenticationToken authCode);

}
