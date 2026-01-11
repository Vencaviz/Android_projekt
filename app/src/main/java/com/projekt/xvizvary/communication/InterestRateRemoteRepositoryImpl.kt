package com.projekt.xvizvary.communication

import com.projekt.xvizvary.network.model.CentralBankData
import com.projekt.xvizvary.network.model.HistoricalRate
import com.projekt.xvizvary.network.model.InterestRateDisplay
import kotlinx.coroutines.delay
import javax.inject.Inject

class InterestRateRemoteRepositoryImpl @Inject constructor() : IInterestRateRemoteRepository {

    override suspend fun getAllInterestRates(): CommunicationResult<List<InterestRateDisplay>> {
        return try {
            // Simulate network delay
            delay(500)
            CommunicationResult.Success(CentralBankData.getAllRates())
        } catch (e: Exception) {
            CommunicationResult.Exception(e)
        }
    }

    override suspend fun getInterestRateById(id: String): CommunicationResult<InterestRateDisplay?> {
        return try {
            delay(300)
            val rate = CentralBankData.getAllRates().find { it.id == id }
            CommunicationResult.Success(rate)
        } catch (e: Exception) {
            CommunicationResult.Exception(e)
        }
    }

    override suspend fun getHistoricalData(rateId: String): CommunicationResult<List<HistoricalRate>> {
        return try {
            delay(300)
            CommunicationResult.Success(CentralBankData.getHistoricalData(rateId))
        } catch (e: Exception) {
            CommunicationResult.Exception(e)
        }
    }
}
