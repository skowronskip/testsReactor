package edu.iis.mto.testreactor.exc3;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AtmMachineTest {

    private CardProviderService cardProviderService;
    private BankService bankService;
    private MoneyDepot moneyDepot;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws CardAuthorizationException, InsufficientFundsException, MoneyDepotException {
        cardProviderService = mock(CardProviderService.class);
        bankService = mock(BankService.class);
        moneyDepot = mock(MoneyDepot.class);
        when(cardProviderService.authorize(any(Card.class))).thenReturn(AuthenticationToken.builder().withUserId("userId").build());
        doNothing().when(bankService).startTransaction(any(AuthenticationToken.class));
        doNothing().when(bankService).charge(any(AuthenticationToken.class), any(Money.class));
        doNothing().when(bankService).commit(any(AuthenticationToken.class));
        doNothing().when(moneyDepot).releaseBanknotes(anyListOf(Banknote.class));
    }

    @Test
    public void itCompiles() {
        assertThat(true, equalTo(true));
    }

    @Test
    public void whenAmountIsBiggerThanZero_thenProperPaymentIsReturned() {
        AtmMachine atmMachine = new AtmMachine(cardProviderService, bankService, moneyDepot);
        Money money = Money.builder().withAmount(200).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        Payment payment = atmMachine.withdraw(money, card);
        assertEquals(payment.getValue().size(), 1);
        assertEquals(payment.getValue().get(0), Banknote.PL200);
    }

    @Test
    public void whenAmountIsEqualZero_thenWrongMoneyAmountExceptionIsThrown() {
        exception.expect(WrongMoneyAmountException.class);
        AtmMachine atmMachine = new AtmMachine(cardProviderService, bankService, moneyDepot);
        Money money = Money.builder().withAmount(0).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        atmMachine.withdraw(money, card);
    }
}
