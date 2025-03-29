package com.keakimleang.digital_menu.configs;

import org.modelmapper.*;
import org.modelmapper.convention.*;
import org.modelmapper.internal.converter.*;

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
