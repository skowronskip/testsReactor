package edu.iis.mto.testreactor.exc3;

public interface CardProviderService {

    AuthenticationToken authorize(Card card) throws CardAuthorizationException;

}
