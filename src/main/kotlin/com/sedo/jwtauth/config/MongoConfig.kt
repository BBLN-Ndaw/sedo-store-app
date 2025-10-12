package com.sedo.jwtauth.config

import org.bson.types.Decimal128
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.math.BigDecimal

/**
 * Configuration class to handle custom conversions for MongoDB.
 * Specifically, it provides converters to handle BigDecimal to Decimal128 conversions and vice versa.
 * It will avoid storing BigDecimal as String in MongoDB.
 */
@Configuration
class MongoConfig {

    @Bean
    fun mongoCustomConversions(): MongoCustomConversions {
        return MongoCustomConversions(
            listOf(
                BigDecimalToDecimal128Converter(),
                Decimal128ToBigDecimalConverter()
            )
        )
    }

    class BigDecimalToDecimal128Converter : Converter<BigDecimal, Decimal128> {
        override fun convert(source: BigDecimal): Decimal128 = Decimal128(source)
    }

    class Decimal128ToBigDecimalConverter : Converter<Decimal128, BigDecimal> {
        override fun convert(source: Decimal128): BigDecimal = source.bigDecimalValue()
    }
}

