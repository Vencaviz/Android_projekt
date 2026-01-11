package com.projekt.xvizvary.communication

import com.projekt.xvizvary.network.model.InterestRateDisplay
import com.projekt.xvizvary.network.model.InterestRateResponse
import javax.inject.Inject

class InterestRateRemoteRepositoryImpl @Inject constructor(
    private val api: InterestRateAPI
) : IInterestRateRemoteRepository {

    override suspend fun getAllInterestRates(): CommunicationResult<List<InterestRateDisplay>> {
        return when (val result = processResponse { api.getInterestRates() }) {
            is CommunicationResult.Success -> {
                CommunicationResult.Success(mapResponseToRates(result.data))
            }
            is CommunicationResult.Error -> result
            is CommunicationResult.ConnectionError -> result
            is CommunicationResult.Exception -> result
        }
    }

    override suspend fun getInterestRateByCountry(country: String): CommunicationResult<InterestRateDisplay?> {
        return when (val result = processResponse { api.getInterestRateByCountry(country = country) }) {
            is CommunicationResult.Success -> {
                val rates = mapResponseToRates(result.data)
                CommunicationResult.Success(rates.firstOrNull())
            }
            is CommunicationResult.Error -> result
            is CommunicationResult.ConnectionError -> result
            is CommunicationResult.Exception -> result
        }
    }

    private fun mapResponseToRates(response: InterestRateResponse): List<InterestRateDisplay> {
        val rates = mutableListOf<InterestRateDisplay>()

        // Map central bank rates
        response.centralBankRates.forEach { rate ->
            rates.add(
                InterestRateDisplay(
                    id = "${rate.country}_${rate.centralBank}".replace(" ", "_"),
                    name = rate.centralBank,
                    country = rate.country,
                    ratePct = rate.ratePct,
                    lastUpdated = rate.lastUpdated,
                    isCentralBank = true
                )
            )
        }

        // Map non-central bank rates
        response.nonCentralBankRates?.forEach { rate ->
            rates.add(
                InterestRateDisplay(
                    id = rate.name.replace(" ", "_"),
                    name = rate.name,
                    country = "",
                    ratePct = rate.ratePct,
                    lastUpdated = rate.lastUpdated,
                    isCentralBank = false
                )
            )
        }

        return rates.sortedBy { it.country.ifEmpty { it.name } }
    }
}
