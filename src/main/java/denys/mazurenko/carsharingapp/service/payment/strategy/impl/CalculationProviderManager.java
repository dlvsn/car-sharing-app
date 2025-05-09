package denys.mazurenko.carsharingapp.service.payment.strategy.impl;

public interface CalculationProviderManager {
    CalculationProvider getCalculationProvider(String key);
}
