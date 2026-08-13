package com.wintercogs.beyonddimensions.forgecompat.fluids;

import java.util.function.Consumer;

public class FluidType
{
    public static final int BUCKET_VOLUME = 1000;

    private final Properties properties;

    public FluidType(Properties properties)
    {
        this.properties = properties;
    }

    public Properties getProperties()
    {
        return properties;
    }

    public void initializeClient(Consumer<Object> consumer)
    {
    }

    public static class Properties
    {
        private int lightLevel;
        private int density;
        private int viscosity;

        public static Properties create()
        {
            return new Properties();
        }

        public Properties lightLevel(int lightLevel)
        {
            this.lightLevel = lightLevel;
            return this;
        }

        public Properties density(int density)
        {
            this.density = density;
            return this;
        }

        public Properties viscosity(int viscosity)
        {
            this.viscosity = viscosity;
            return this;
        }

        public int getLightLevel()
        {
            return lightLevel;
        }
    }
}
