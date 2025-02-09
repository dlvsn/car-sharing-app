package denys.mazurenko.carsharingapp.service.payment.strategy.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CalculationProviderManagerImpl implements CalculationProviderManager {
    private final List<CalculationProvider> calculationProviders;

    @Override
    public CalculationProvider getCalculationProvider(String key) {
        return calculationProviders.stream()
                .filter(e -> e.getKey().equals(key))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Can't calculate amount by key " + key));
    }
}
