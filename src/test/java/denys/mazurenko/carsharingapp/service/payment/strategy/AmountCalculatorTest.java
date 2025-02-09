package denys.mazurenko.carsharingapp.service.payment.strategy;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import denys.mazurenko.carsharingapp.model.Rental;
import denys.mazurenko.carsharingapp.service.payment.strategy.impl.CalculationProviderManager;
import denys.mazurenko.carsharingapp.service.payment.strategy.impl.CalculationType;
import denys.mazurenko.carsharingapp.service.payment.strategy.impl.FineAmountCalculation;
import denys.mazurenko.carsharingapp.service.payment.strategy.impl.OnTimeAmountCalculation;
import denys.mazurenko.carsharingapp.util.TestObjectBuilder;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AmountCalculatorTest {
    @InjectMocks
    private AmountCalculator amountCalculator;

    @Mock
    private CalculationProviderManager calculationProviderManager;

    @Test
    @DisplayName("""
            Successfully calculate the amount for an on-time rental
            """)
    void calculateAmountOnTime_success() {
        Rental rental = TestObjectBuilder.initFirstCompletedRental();
        BigDecimal expectedAmount = BigDecimal.valueOf(100);

        OnTimeAmountCalculation mockAmountCalculator = mock(OnTimeAmountCalculation.class);

        when(calculationProviderManager.getCalculationProvider(CalculationType.ON_TIME.name()))
                .thenReturn(mockAmountCalculator);

        when(mockAmountCalculator.getAmount(rental)).thenReturn(expectedAmount);

        BigDecimal actual = amountCalculator.calculate(rental);

        assertThat(actual).isEqualTo(expectedAmount);

        verify(calculationProviderManager, times(1))
                .getCalculationProvider(CalculationType.ON_TIME.name());
        verify(mockAmountCalculator, times(1))
                .getAmount(rental);
        verifyNoInteractions(mock(FineAmountCalculation.class));
    }

    @Test
    @DisplayName("""
            Successfully calculate the amount including fine for a late rental
            """)
    void calculateAmountFine_success() {
        Rental rental = TestObjectBuilder.initSecondCompletedRental();

        BigDecimal onTimeAmount = BigDecimal.valueOf(100);
        BigDecimal fineAmount = BigDecimal.valueOf(50);
        BigDecimal expectedAmount = onTimeAmount.add(fineAmount);

        OnTimeAmountCalculation mockOnTimeAmountCalculator = mock(OnTimeAmountCalculation.class);
        FineAmountCalculation mockFineAmountCalculator = mock(FineAmountCalculation.class);

        when(calculationProviderManager.getCalculationProvider(CalculationType.ON_TIME.name()))
                .thenReturn(mockOnTimeAmountCalculator);
        when(calculationProviderManager.getCalculationProvider(CalculationType.LATE.name()))
                .thenReturn(mockFineAmountCalculator);

        when(mockOnTimeAmountCalculator.getAmount(rental)).thenReturn(onTimeAmount);
        when(mockFineAmountCalculator.getAmount(rental)).thenReturn(fineAmount);

        BigDecimal actualAmount = amountCalculator.calculate(rental);

        assertThat(actualAmount).isEqualTo(expectedAmount);

        verify(calculationProviderManager, times(1))
                .getCalculationProvider(CalculationType.ON_TIME.name());
        verify(calculationProviderManager, times(1))
                .getCalculationProvider(CalculationType.LATE.name());
        verify(mockOnTimeAmountCalculator, times(1))
                .getAmount(rental);
        verify(mockFineAmountCalculator, times(1))
                .getAmount(rental);
    }

    @Test
    @DisplayName("""
            Throw IllegalArgumentException when calculation provider is not found
            """)
    void calculateAmount_providerNotFound_throwsException() {
        Rental rental = TestObjectBuilder.initSecondCompletedRental();

        when(calculationProviderManager.getCalculationProvider(anyString()))
                .thenThrow(new IllegalArgumentException("Can't calculate amount by key UNKNOWN"));

        assertThrows(IllegalArgumentException.class, () -> amountCalculator.calculate(rental));

        verify(calculationProviderManager, times(1)).getCalculationProvider(anyString());
        verifyNoInteractions(
                mock(OnTimeAmountCalculation.class),
                mock(FineAmountCalculation.class)
        );
    }
}
