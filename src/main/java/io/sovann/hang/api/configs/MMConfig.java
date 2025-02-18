package io.sovann.hang.api.configs;

import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.internal.converter.MergingCollectionConverter;

public class MMConfig {
    private MMConfig() {
        throw new IllegalStateException("ModelMapperConfig class");
    }

    public static ModelMapper mapper() {
        ModelMapper modelMapper = new ModelMapper();
        Condition<?, ?> skipNulls =
                context -> context.getSource() != null;
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(skipNulls);
        modelMapper.addConverter(new MergingCollectionConverter());
        return modelMapper;
    }
}
