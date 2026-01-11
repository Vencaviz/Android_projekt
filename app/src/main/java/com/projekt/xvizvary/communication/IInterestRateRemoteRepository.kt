package com.projekt.xvizvary.communication

import com.projekt.xvizvary.network.model.InterestRateDisplay

interface IInterestRateRemoteRepository : IBaseRemoteRepository {

    suspend fun getAllInterestRates(): CommunicationResult<List<InterestRateDisplay>>

    suspend fun getInterestRateByCountry(country: String): CommunicationResult<InterestRateDisplay?>
}
