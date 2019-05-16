package edu.iis.mto.testreactor.exc3;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AtmMachineTest {

    private CardProviderService cardProviderService;
    private BankService bankService;
    private MoneyDepot moneyDepot;
    private AtmMachine atmMachine;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws CardAuthorizationException, InsufficientFundsException, MoneyDepotException {
        cardProviderService = mock(CardProviderService.class);
        bankService = mock(BankService.class);
        moneyDepot = mock(MoneyDepot.class);
        atmMachine = new AtmMachine(cardProviderService, bankService, moneyDepot);
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
        Money money = Money.builder().withAmount(200).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        Payment payment = atmMachine.withdraw(money, card);
        assertThat(payment.getValue().size(), is(1));
        assertThat(payment.getValue(), contains(Banknote.PL200));
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
        Money money = Money.builder().withAmount(200).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        atmMachine.withdraw(money, card);
    }

    @Test
    public void whenAmountCannotBePayedWithBankotes_thenWrongMoneyAmountExceptionIsThrown() {
        exception.expect(WrongMoneyAmountException.class);
        Money money = Money.builder().withAmount(222).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        atmMachine.withdraw(money, card);
    }

    @Test
    public void whenBankServiceThrowsInsufficientFundsException_thenAtmExceptionIsThrown() throws InsufficientFundsException {
        doThrow(InsufficientFundsException.class).when(bankService).charge(any(AuthenticationToken.class), any(Money.class));
        exception.expect(AtmException.class);
        Money money = Money.builder().withAmount(100).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        atmMachine.withdraw(money, card);
    }

    @Test
    public void whenAmountIsBiggerThanZeroButRequiresMoreThanOneBanknote_thenProperPaymentIsReturned() {
        Money money = Money.builder().withAmount(800).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        Payment payment = atmMachine.withdraw(money, card);
        assertThat(payment.getValue().size(), is(3));
        assertThat(payment.getValue(), containsInAnyOrder(Banknote.PL500, Banknote.PL200, Banknote.PL100));
    }

    @Test
    public void whenWithdrawIsCalled_thenCardServiceAuthorizeIsCalledOnce() throws CardAuthorizationException {
        Money money = Money.builder().withAmount(800).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        atmMachine.withdraw(money, card);
        verify(cardProviderService, times(1)).authorize(any(Card.class));
    }

    @Test
    public void whenWithdrawIsCalled_thenBankServiceStartTransactionIsCalledOnce() {
        Money money = Money.builder().withAmount(800).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        atmMachine.withdraw(money, card);
        verify(bankService, times(1)).startTransaction(any(AuthenticationToken.class));
    }

    @Test
    public void whenBankServiceChargeIsNotThrowingException_thenBankServiceAbortIsNotCalled() {
        Money money = Money.builder().withAmount(800).withCurrency(Currency.PL).build();
        Card card = Card.builder().build();
        atmMachine.withdraw(money, card);
        verify(bankService, never()).abort(any(AuthenticationToken.class));
    }
}
