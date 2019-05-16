package edu.iis.mto.testreactor.exc3;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

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
    public void whenAmountIsBiggerThanZeroButRequiresOneBanknote_thenProperPaymentIsReturned() {
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

    @Test
    public void whenCardServiceThrowsCardAuthorizationException_thenAtmExceptionIsThrown() throws CardAuthorizationException {
        doThrow(CardAuthorizationException.class).when(cardProviderService).authorize(any(Card.class));
        exception.expect(AtmException.class);
        AtmMachine atmMachine = new AtmMachine(cardProviderService, bankService, moneyDepot);
        Money money = Money.builder().withAmount(200).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        atmMachine.withdraw(money, card);
    }

    @Test
    public void whenAmountCannotBePayedWithBankotes_thenWrongMoneyAmountExceptionIsThrown() {
        exception.expect(WrongMoneyAmountException.class);
        AtmMachine atmMachine = new AtmMachine(cardProviderService, bankService, moneyDepot);
        Money money = Money.builder().withAmount(222).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        atmMachine.withdraw(money, card);
    }

    @Test
    public void whenBankServiceThrowsInsufficientFundsException_thenAtmExceptionIsThrown() throws InsufficientFundsException {
        doThrow(InsufficientFundsException.class).when(bankService).charge(any(AuthenticationToken.class), any(Money.class));
        exception.expect(AtmException.class);
        AtmMachine atmMachine = new AtmMachine(cardProviderService, bankService, moneyDepot);
        Money money = Money.builder().withAmount(100).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        atmMachine.withdraw(money, card);
    }

    @Test
    public void whenAmountIsBiggerThanZeroButRequiresMoreThanOneBanknote_thenProperPaymentIsReturned() {
        AtmMachine atmMachine = new AtmMachine(cardProviderService, bankService, moneyDepot);
        Money money = Money.builder().withAmount(800).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        Payment payment = atmMachine.withdraw(money, card);
        assertEquals(payment.getValue().size(), 3);
        assertThat(payment.getValue(), containsInAnyOrder(Banknote.PL500, Banknote.PL200, Banknote.PL100));
    }
}
