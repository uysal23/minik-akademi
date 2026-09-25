package com.uysal.minikakademi.feature.learningpath

import android.content.Context
import org.json.JSONObject

data class MathLesson(
    val id: String,
    val curriculumId: String,
    val sequenceId: String,
    val title: String,
    val sourceType: String,
    val prerequisites: List<String>,
    val activityType: String,
    val prompt: String,
    val target: String,
    val options: List<String>,
    val correctIndex: Int
)

object MathProgressStore {
    private const val PREFS = "minik_akademi_math"
    private const val KEY = "completed"

    fun load(context: Context): Set<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(KEY, emptySet())
            ?.toSet()
            .orEmpty()

    fun complete(context: Context, activityId: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val next = prefs.getStringSet(KEY, emptySet()).orEmpty().toMutableSet()
        next.add(activityId)
        prefs.edit().putStringSet(KEY, next).apply()
    }
}

object MathContent {
    fun load(context: Context): List<MathLesson> {
        val raw = context.assets.open("curriculum_manifest.json")
            .bufferedReader()
            .use { it.readText() }
        val root = JSONObject(raw)
        val sequences = root.getJSONArray("sequences")
        val result = mutableListOf<MathLesson>()

        for (si in 0 until sequences.length()) {
            val sequence = sequences.getJSONObject(si)
            if (sequence.getString("domain") != "MATHEMATICS") continue
            val sequenceId = sequence.getString("id")
            val nodes = sequence.getJSONArray("nodes")
            for (ni in 0 until nodes.length()) {
                val node = nodes.getJSONObject(ni)
                val id = node.getString("id")
                val prerequisites = buildList {
                    val arr = node.optJSONArray("prerequisites")
                    if (arr != null) {
                        for (i in 0 until arr.length()) add(arr.getString(i))
                    }
                }
                result += buildLesson(
                    id = id,
                    sequenceId = sequenceId,
                    title = node.getString("title"),
                    sourceType = node.getString("sourceType"),
                    prerequisites = prerequisites
                )
            }
        }
        return result
    }

    private fun buildLesson(
        id: String,
        sequenceId: String,
        title: String,
        sourceType: String,
        prerequisites: List<String>
    ): MathLesson {
        fun choice(
            type: String,
            prompt: String,
            target: String,
            options: List<String>,
            correctIndex: Int
        ) = MathLesson(
            id = "ACT-$id",
            curriculumId = id,
            sequenceId = sequenceId,
            title = title,
            sourceType = sourceType,
            prerequisites = prerequisites,
            activityType = type,
            prompt = prompt,
            target = target,
            options = options,
            correctIndex = correctIndex
        )

        if (id.startsWith("MAT-DIGIT-")) {
            val digit = id.removePrefix("MAT-DIGIT-")
            return choice(
                type = "TRACE_NUMBER",
                prompt = "$digit rakamının noktalı yolunu parmağınla takip et.",
                target = digit,
                options = emptyList(),
                correctIndex = -1
            )
        }

        return when (id) {
            "MAT-SP-01" -> choice("POSITION", "Top masanın neresinde?", "üstünde", listOf("üstünde", "altında", "yanında"), 0)
            "MAT-SP-02" -> choice("POSITION", "Kuşlar ağacın neresinde?", "etrafında", listOf("etrafında", "altında", "içinde"), 0)
            "MAT-SP-03" -> choice("POSITION", "Top iki çocuğun neresinde?", "arasında", listOf("arasında", "arkasında", "dışında"), 0)
            "MAT-SP-04" -> choice("POSITION", "Araba kamyonun neresinde?", "önünde", listOf("önünde", "arkasında", "içinde"), 0)
            "MAT-SP-05" -> choice("POSITION", "Sincap çocuğa göre nerededir?", "yakın", listOf("yakın", "uzak", "içinde"), 0)
            "MAT-SP-06" -> choice("POSITION", "Pembe balon yeşil balona göre nerededir?", "yüksekte", listOf("yüksekte", "alçakta", "arasında"), 0)
            "MAT-SP-07" -> choice("POSITION", "Muz tabağın neresindedir?", "içinde", listOf("içinde", "dışında", "üstünde"), 0)
            "MAT-SP-08" -> choice("POSITION", "Yasemin çocuğun neresindedir?", "sağında", listOf("sağında", "solunda", "arkasında"), 0)
            "MAT-EQ-01" -> choice("CHOICE", "Aynı aracı bul.", "araba", listOf("araba", "bisiklet", "kamyon"), 0)
            "MAT-EQ-02" -> choice("CHOICE", "Aynı şekli bul.", "üçgen", listOf("üçgen", "çember", "kare"), 0)
            "MAT-EQ-03" -> choice("CHOICE", "Eş nesneyi seç.", "kalem", listOf("kalem", "kitap", "silgi"), 0)
            "MAT-EQ-04" -> choice("CHOICE", "İki görseli eş yapmak için farklı nesneyi seç.", "top", listOf("top", "kitap", "kalem"), 0)
            "MAT-SP-ADAPT" -> choice("POSITION", "Avatarı sandalyenin neresine yerleştir?", "üstünde", listOf("üstünde", "altında", "uzakta"), 0)

            "MAT-NUM-01" -> choice("INTRO", "Rakamların sayıları oluşturduğunu incele.", "19", emptyList(), -1)
            "MAT-NUM-02" -> choice("COUNT", "Nesneleri say ve doğru sayıyı seç.", "7", listOf("6", "7", "8"), 1)
            "MAT-NUM-03" -> choice("COUNT", "Verilen sayı kadar nesneyi say.", "12", listOf("10", "12", "14"), 1)
            "MAT-TENS-01" -> choice("CHOICE", "10 birlik kaç onluk eder?", "10 birlik", listOf("1 onluk", "2 onluk", "10 onluk"), 0)
            "MAT-TENS-02" -> choice("CHOICE", "18 sayısını onluk ve birliklerine ayır.", "18", listOf("1 onluk 8 birlik", "8 onluk 1 birlik", "18 onluk"), 0)
            "MAT-TENS-03" -> choice("CHOICE", "1 onluk 5 birlik hangi sayıdır?", "1 onluk 5 birlik", listOf("15", "51", "5"), 0)
            "MAT-TENS-04" -> choice("CHOICE", "17 sayısını onluk ve birlik olarak göster.", "17", listOf("1 onluk 7 birlik", "7 onluk 1 birlik", "1 onluk 5 birlik"), 0)
            "MAT-TENS-05" -> choice("CHOICE", "1 onluk 3 birlik hangi sayıdır?", "1 onluk 3 birlik", listOf("13", "31", "10"), 0)
            "MAT-TENS-06" -> choice("CHOICE", "16 sayısını seç.", "16", listOf("1 onluk 6 birlik", "6 onluk 1 birlik", "1 onluk 9 birlik"), 0)
            "MAT-ORD-01" -> choice("CHOICE", "Üçüncü sırayı seç.", "1. 2. 3. 4. 5.", listOf("1.", "3.", "5."), 1)
            "MAT-CMP-01" -> choice("CHOICE", "İki grubun çokluğunu karşılaştır.", "●●●   ●●●", listOf("eşit", "solda daha çok", "sağda daha çok"), 0)
            "MAT-CMP-02" -> choice("CHOICE", "Hangi grupta daha çok nesne var?", "●●●●●●   ●●●●", listOf("solda", "sağda", "eşit"), 0)
            "MAT-SKIP-01" -> choice("SEQUENCE", "Birer ritmik saymayı tamamla.", "1, 2, 3, 4, ?", listOf("5", "6", "7"), 0)
            "MAT-SKIP-05" -> choice("SEQUENCE", "Beşer ritmik saymayı tamamla.", "5, 10, 15, 20, ?", listOf("25", "30", "22"), 0)
            "MAT-SKIP-10" -> choice("SEQUENCE", "Onar ritmik saymayı tamamla.", "10, 20, 30, 40, ?", listOf("50", "45", "60"), 0)
            "MAT-SKIP-02" -> choice("SEQUENCE", "İkişer ritmik saymayı tamamla.", "2, 4, 6, 8, ?", listOf("10", "9", "12"), 0)
            "MAT-BACK-01" -> choice("SEQUENCE", "20'den geriye birer saymayı tamamla.", "20, 19, 18, 17, ?", listOf("16", "15", "14"), 0)
            "MAT-BACK-02" -> choice("SEQUENCE", "20'den geriye ikişer saymayı tamamla.", "20, 18, 16, 14, ?", listOf("12", "13", "10"), 0)
            "MAT-PAT-01" -> choice("SEQUENCE", "Örüntüde sıradaki öğeyi seç.", "sarı, sarı, mor, sarı, sarı, mor, ?", listOf("sarı", "mor", "mavi"), 0)
            "MAT-PAT-02" -> choice("SEQUENCE", "Örüntüyü tamamla.", "üçgen, daire, üçgen, daire, ?", listOf("üçgen", "daire", "kare"), 0)
            "MAT-PAT-03" -> choice("SEQUENCE", "Örüntü kuralını sürdür.", "kare, kare, daire, kare, kare, daire, ?", listOf("kare", "daire", "üçgen"), 0)
            "MAT-PAT-04" -> choice("SEQUENCE", "Örüntü yolunu tamamla.", "yıldız, daire, yıldız, daire, ?", listOf("yıldız", "daire", "kare"), 0)
            "MAT-REV-01" -> choice("CHOICE", "10'dan büyük olan sayıyı seç.", "12 ve 8", listOf("12", "8", "10"), 0)
            "MAT-REV-02" -> choice("CHOICE", "Soldan üçüncü sırayı seç.", "1 2 3 4 5", listOf("2.", "3.", "4."), 1)
            "MAT-ASSESS-01" -> choice("SEQUENCE", "İkişer ritmik saymayı tamamla.", "2, 4, 6, 8, ?", listOf("10", "9", "12"), 0)
            "MAT-COUNT-ADAPT" -> choice("COUNT", "Yeni nesneleri say ve doğru sayıyı seç.", "9", listOf("8", "9", "10"), 1)

            "MAT-LEN-01" -> choice("CHOICE", "Daha uzun olanı seç.", "────────   ────", listOf("soldaki", "sağdaki", "eşit"), 0)
            "MAT-LEN-02" -> choice("CHOICE", "Sınıfın uzunluğu için uygun ölçme aracını seç.", "sınıf", listOf("adım", "parmak", "silgi"), 0)
            "MAT-LEN-03" -> choice("CHOICE", "Silginin boyu için uygun ölçme aracını seç.", "silgi", listOf("parmak", "adım", "kulaç"), 0)
            "MAT-LEN-04" -> choice("CHOICE", "Masa 5 karış, kalemlik 2 karış. Hangisi daha uzun?", "5 karış / 2 karış", listOf("masa", "kalemlik", "eşit"), 0)
            "MAT-LEN-05" -> choice("CHOICE", "Tahmin 6 karış, ölçüm 5 karış. Fark kaç?", "6 - 5", listOf("1", "2", "3"), 0)
            "MAT-MASS-01" -> choice("CHOICE", "Kitap ve silgiyi karşılaştır.", "kitap / silgi", listOf("kitap daha ağır", "silgi daha ağır", "eşit"), 0)
            "MAT-MASS-02" -> choice("CHOICE", "Terazinin iki tarafı aynı seviyede. Sonuç nedir?", "terazi", listOf("eşit ağırlık", "sol daha ağır", "sağ daha ağır"), 0)
            "MAT-MASS-03" -> choice("CHOICE", "En ağır olanı seç.", "karpuz, elma, çilek", listOf("karpuz", "elma", "çilek"), 0)
            "MAT-MASS-04" -> choice("CHOICE", "Ağırdan hafife doğru sırayı seç.", "ağırdan hafife", listOf("karpuz > elma > çilek", "çilek > elma > karpuz", "elma > karpuz > çilek"), 0)
            "MAT-MASS-05" -> choice("CHOICE", "Daha ağır nesneyi seç.", "kitap, tüy, pamuk", listOf("kitap", "tüy", "pamuk"), 0)
            "MAT-MEASURE-ADAPT" -> choice("CHOICE", "Masa için uygun ölçme sonucunu seç.", "masa", listOf("5 karış", "2 adım", "1 kulaç"), 0)

            "EXT-ADD-01" -> choice("ADD", "3 nesneye 2 nesne daha ekle. Toplamı seç.", "3 + 2", listOf("4", "5", "6"), 1)
            "EXT-SUB-01" -> choice("SUB", "5 nesneden 2 nesneyi ayır. Kalanı seç.", "5 - 2", listOf("2", "3", "4"), 1)
            "EXT-MUL-01" -> choice("GROUP", "3 grupta 2'şer nesne var. Toplamı seç.", "3 × 2", listOf("5", "6", "8"), 1)
            else -> choice("CHOICE", title, title, listOf("Doğru", "Tekrar bak", "Başka seçenek"), 0)
        }
    }
}
