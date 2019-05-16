package edu.iis.mto.testreactor.exc3;

import java.util.List;

public interface MoneyDepot {

    void releaseBanknotes(List<Banknote> withdrawal) throws MoneyDepotException;

}
