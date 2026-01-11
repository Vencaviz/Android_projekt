package com.projekt.xvizvary.communication

import com.projekt.xvizvary.network.model.HistoricalRate
import com.projekt.xvizvary.network.model.InterestRateDisplay

interface IInterestRateRemoteRepository {

    suspend fun getAllInterestRates(): CommunicationResult<List<InterestRateDisplay>>

    suspend fun getInterestRateById(id: String): CommunicationResult<InterestRateDisplay?>

    suspend fun getHistoricalData(rateId: String): CommunicationResult<List<HistoricalRate>>
}
