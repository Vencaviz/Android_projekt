package com.projekt.xvizvary.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Data class representing an ATM location
 * Implements ClusterItem for use with Google Maps Utility clustering
 */
data class Atm(
    val id: String,
    val bankName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isOpen24Hours: Boolean = false,
    val hasDeposit: Boolean = false
) : ClusterItem {
    
    override fun getPosition(): LatLng = LatLng(latitude, longitude)
    
    override fun getTitle(): String = bankName
    
    override fun getSnippet(): String = address
    
    override fun getZIndex(): Float = 0f
}

/**
 * Bank types for filtering
 */
enum class BankType(val displayName: String) {
    ALL("All"),
    CSOB("ČSOB"),
    KB("Komerční banka"),
    CS("Česká spořitelna"),
    RAIFFEISEN("Raiffeisenbank"),
    UNICREDIT("UniCredit"),
    MONETA("MONETA"),
    FIO("Fio banka"),
    AIRBANK("Air Bank")
}

/**
 * Sample ATM data for Brno area
 */
object AtmSampleData {
    
    val atms = listOf(
        // ČSOB
        Atm(
            id = "csob_1",
            bankName = "ČSOB",
            address = "Joštova 5, Brno",
            latitude = 49.1954,
            longitude = 16.6050,
            isOpen24Hours = true,
            hasDeposit = true
        ),
        Atm(
            id = "csob_2",
            bankName = "ČSOB",
            address = "Česká 12, Brno",
            latitude = 49.1932,
            longitude = 16.6089,
            isOpen24Hours = true
        ),
        Atm(
            id = "csob_3",
            bankName = "ČSOB",
            address = "Galerie Vaňkovka, Brno",
            latitude = 49.1897,
            longitude = 16.6127,
            isOpen24Hours = false,
            hasDeposit = true
        ),
        
        // Komerční banka
        Atm(
            id = "kb_1",
            bankName = "Komerční banka",
            address = "Náměstí Svobody 21, Brno",
            latitude = 49.1947,
            longitude = 16.6081,
            isOpen24Hours = true,
            hasDeposit = true
        ),
        Atm(
            id = "kb_2",
            bankName = "Komerční banka",
            address = "Královo Pole, Brno",
            latitude = 49.2236,
            longitude = 16.5934,
            isOpen24Hours = true
        ),
        Atm(
            id = "kb_3",
            bankName = "Komerční banka",
            address = "Mendlovo náměstí, Brno",
            latitude = 49.1852,
            longitude = 16.5934,
            isOpen24Hours = false
        ),
        
        // Česká spořitelna
        Atm(
            id = "cs_1",
            bankName = "Česká spořitelna",
            address = "Hlavní nádraží, Brno",
            latitude = 49.1904,
            longitude = 16.6128,
            isOpen24Hours = true,
            hasDeposit = true
        ),
        Atm(
            id = "cs_2",
            bankName = "Česká spořitelna",
            address = "Běhounská 2, Brno",
            latitude = 49.1978,
            longitude = 16.6068,
            isOpen24Hours = true
        ),
        Atm(
            id = "cs_3",
            bankName = "Česká spořitelna",
            address = "OC Olympia, Brno",
            latitude = 49.1721,
            longitude = 16.6584,
            isOpen24Hours = false,
            hasDeposit = true
        ),
        Atm(
            id = "cs_4",
            bankName = "Česká spořitelna",
            address = "Bystrc, Brno",
            latitude = 49.2283,
            longitude = 16.5183,
            isOpen24Hours = true
        ),
        
        // Raiffeisenbank
        Atm(
            id = "rb_1",
            bankName = "Raiffeisenbank",
            address = "Kobližná 3, Brno",
            latitude = 49.1941,
            longitude = 16.6073,
            isOpen24Hours = true
        ),
        Atm(
            id = "rb_2",
            bankName = "Raiffeisenbank",
            address = "Brno-Žabovřesky",
            latitude = 49.2102,
            longitude = 16.5778,
            isOpen24Hours = false
        ),
        
        // UniCredit
        Atm(
            id = "uc_1",
            bankName = "UniCredit",
            address = "Nádražní 6, Brno",
            latitude = 49.1918,
            longitude = 16.6115,
            isOpen24Hours = true,
            hasDeposit = true
        ),
        
        // MONETA
        Atm(
            id = "mon_1",
            bankName = "MONETA",
            address = "Masarykova 28, Brno",
            latitude = 49.1963,
            longitude = 16.6042,
            isOpen24Hours = true
        ),
        Atm(
            id = "mon_2",
            bankName = "MONETA",
            address = "Campus Square, Brno",
            latitude = 49.2265,
            longitude = 16.5763,
            isOpen24Hours = false
        ),
        
        // Fio banka
        Atm(
            id = "fio_1",
            bankName = "Fio banka",
            address = "Veveří 102, Brno",
            latitude = 49.2045,
            longitude = 16.5912,
            isOpen24Hours = true
        ),
        
        // Air Bank
        Atm(
            id = "air_1",
            bankName = "Air Bank",
            address = "Šilingrovo náměstí, Brno",
            latitude = 49.1925,
            longitude = 16.6045,
            isOpen24Hours = true,
            hasDeposit = true
        ),
        Atm(
            id = "air_2",
            bankName = "Air Bank",
            address = "Lesná, Brno",
            latitude = 49.2198,
            longitude = 16.6178,
            isOpen24Hours = false
        )
    )
    
    fun getByBank(bankName: String): List<Atm> {
        return if (bankName == BankType.ALL.displayName) {
            atms
        } else {
            atms.filter { it.bankName.equals(bankName, ignoreCase = true) }
        }
    }
    
    fun search(query: String): List<Atm> {
        if (query.isBlank()) return atms
        val lowerQuery = query.lowercase()
        return atms.filter { 
            it.bankName.lowercase().contains(lowerQuery) ||
            it.address.lowercase().contains(lowerQuery)
        }
    }
}
