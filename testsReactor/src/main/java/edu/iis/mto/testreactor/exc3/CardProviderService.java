package edu.iis.mto.testreactor.exc3;

import java.util.Optional;

public interface CardProviderService {

    Optional<AuthenticationToken> authorize(Card card);

}
