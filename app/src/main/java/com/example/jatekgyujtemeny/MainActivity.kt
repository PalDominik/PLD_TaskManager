package com.example.jatekgyujtemeny

import android.R
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jatekgyujtemeny.ui.theme.JatekGyujtemenyTheme
import java.nio.file.WatchEvent

data class Feladat(
    val id: Number,
    val nev: String,
    val kategoria: String,
    val prioitas: Int,
    val elkeszult: Boolean = false
)

enum class FeladatSzuro{
    OSSZES,
    AKTIV,
    ELKESZULT
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color( 241,  247, 253)
                )
                {
                    FeladatKezelo()
                }

            }
        }
    }
}
private  val Narancs = Color(255, 165, 0)
private val Zold = Color(85, 166, 48)
private val Sotet = Color(23, 43, 77)
private val Szurke = Color(102, 112, 133)
private  val Kek = Color(52, 120, 246)

private  val Piros = Color(220, 53, 69)

@Composable
fun FeladatKezelo()
{
    val context = androidx.compose.ui.platform.LocalContext.current

    val feladatok = remember { mutableStateListOf<Feladat>() }
    var kovetkezoId by remember { mutableStateOf(1L) }

    var nev by remember{mutableStateOf("")}
    var kategoria by remember { mutableStateOf("") }
    var prioitas by remember { mutableStateOf("") }

    var nevHiba by remember { mutableStateOf<String?>(null) }
    var kategoriaHiba by remember { mutableStateOf<String?>(null) }
    var prioitasHiba by remember { mutableStateOf<String?>(null) }

    var szuro by remember { mutableStateOf(FeladatSzuro.OSSZES) }
    var szerkesztendo by remember { mutableStateOf<Feladat?>(null) }
    var torlendo by remember { mutableStateOf<Feladat?>(null) }

    val aktivDb = feladatok.count { !it.elkeszult }
    val elkeszultDb = feladatok.count { it.elkeszult }

    val szurtFeladatok = feladatok.filter { feladat ->
        when (szuro) {
            FeladatSzuro.OSSZES -> true
            FeladatSzuro.AKTIV -> !feladat.elkeszult
            FeladatSzuro.ELKESZULT -> feladat.elkeszult
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp))
    {
        Text(
            text = "Feladatkezelő",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Sotet,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(3.dp))

        Text(
            text = "Szervezd meg a napi teendőidet!",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 13.sp,
            color = Szurke,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = nev,
            onValueChange = {
                nev = it
                nevHiba = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {Text("Feladat neve")},
            singleLine = true,
            isError = nevHiba != null,
            supportingText = {
                nevHiba?.let { Text(it) }
            }
        )

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = kategoria,
            onValueChange = {
                kategoria = it
                kategoriaHiba = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {Text("Kategória")},
            singleLine = true,
            isError = kategoriaHiba != null,
            supportingText = {
                kategoriaHiba?.let { Text(it) }
            }
        )

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = prioitas,
            onValueChange = {
                prioitas = it
                prioitasHiba = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {Text("Prioritás (1-3)")},
            singleLine = true,
            isError = prioitasHiba != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            supportingText = {
                prioitasHiba?.let { Text(it) }
            }
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                nevHiba = if(nev.isBlank()) {
                "A feladat neve kötelező!"
                }else null

                kategoriaHiba = if(kategoria.isBlank()) {
                    "A kategória megadása kötelező!"
                }else null

                val p = prioitas.toIntOrNull()

                prioitasHiba = when {
                    prioitas.isBlank() -> "A prioritás megadása kötelező!"
                    p == null -> "A prioritásnak egész számnak kell lennie!"
                    p !in 1..3 -> "A prioritásnak csak 1 és 3 közti érték lehet!"
                    else -> null
                }

                if(nevHiba != null || kategoriaHiba != null || prioitasHiba != null )
                {
                    Toast.makeText(
                        context,
                        "Ellenörizd a megadott adatokat!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    feladatok.add(
                        Feladat(
                            id = kovetkezoId++,
                            nev = nev.trim(),
                            kategoria = kategoria.trim(),
                            prioitas = p!!,
                            elkeszult = false
                        )
                    )

                    nev = ""
                    kategoria = ""
                    prioitas = ""

                    nevHiba = null
                    kategoriaHiba = null
                    prioitasHiba = null

                    Toast.makeText(
                        context,
                        "Feladat sikeresen hozzáadva!",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Kek
            )
        ) {
            Text("Feladat hozzáadása")
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            SzamlaloKartya(
                cim = "Összes",
                darab = feladatok.size,
                modifier = Modifier.weight(1f),
                hatter = Color.White
            )

            SzamlaloKartya(
                cim = "Aktív",
                darab = aktivDb,
                modifier = Modifier.weight(1f),
                hatter = Color(229, 247, 237)
            )

            SzamlaloKartya(
                cim = "Elkészült",
                darab = elkeszultDb,
                modifier = Modifier.weight(1f),
                hatter = Color(240,255,229)
            )

        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SzuroGomb(
                szoveg = "Összes",
                kivalasztva = szuro == FeladatSzuro.OSSZES,
                modifier = Modifier.weight(1f)
            ){
                szuro = FeladatSzuro.OSSZES
            }

            SzuroGomb(
                szoveg = "Aktív",
                kivalasztva = szuro == FeladatSzuro.AKTIV,
                modifier = Modifier.weight(1f)
            ){
                szuro = FeladatSzuro.AKTIV
            }

            SzuroGomb(
                szoveg = "Elkészült",
                kivalasztva = szuro == FeladatSzuro.ELKESZULT,
                modifier = Modifier.weight(1f)
            ){
                szuro = FeladatSzuro.ELKESZULT
            }
        }

        Spacer(Modifier.height(10.dp))

        if(szurtFeladatok.isEmpty())
        {
            val uzenet = when{
                feladatok.isEmpty() -> "Még nincs felatatod."

                szuro == FeladatSzuro.AKTIV -> "Nincs aktív feladat."
                szuro == FeladatSzuro.ELKESZULT -> "Még nincs elkészült feladat."

                else -> "Még nincs felatadod"
            }

            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "▤",
                        fontSize = 48.sp,
                        color = Color(247,197,213)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = uzenet,
                        textAlign = TextAlign.Center,
                        color = Szurke,
                        fontSize = 15.sp
                    )

                }
            }
        }else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = szurtFeladatok,
                    key = { it.id }
                ){
                    feladat -> FeladatKartya(
                        feladat = feladat,
                        onAllapotValtas = {
                            val index = feladatok.indexOfFirst{
                                it.id == feladat.id
                            }

                            if(index != -1){
                                feladatok[index] = feladat.copy(
                                    elkeszult = !feladat.elkeszult
                                )
                            }
                        },
                    onSzerkesztes = {
                        szerkesztendo = feladat
                    },
                    onTorles = {
                        torlendo = feladat
                    }
                    )
                }
            }
        }

    }

    szerkesztendo?.let { feladat ->
        SzerkesztesDialog(
            feladat = feladat,
            onMegse = {
                szerkesztendo = null
            },
            onMentes = { ujNev, ujKategoria, ujPrioritas ->
                val index = feladatok.indexOfFirst {
                    it.id == feladat.id
                }

                if (index != -1) {
                    feladatok[index] = feladatok[index].copy(
                        nev = ujNev.trim(),
                        kategoria = ujKategoria.trim(),
                        prioitas = ujPrioritas
                    )
                }

                szerkesztendo = null

                Toast.makeText(
                    context,
                    "Feladat sikeresen módosítva!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    // Törlés megerősítése
    torlendo?.let { feladat ->
        AlertDialog(
            onDismissRequest = {
                torlendo = null
            },
            title = {
                Text(
                    "Feladat törlése",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Biztosan törölni szeretnéd az " +
                            "„${feladat.nev}” feladatot?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        feladatok.removeAll {
                            it.id == feladat.id
                        }

                        torlendo = null

                        Toast.makeText(
                            context,
                            "Feladat törölve!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text(
                        "Törlés",
                        color = Piros,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        torlendo = null
                    }
                ) {
                    Text("Mégse")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}



@Composable
fun SzamlaloKartya(
    cim: String,
    darab: Int,
    modifier: Modifier = Modifier,
    hatter: Color
)
{
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = hatter
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = cim,
                fontSize = 11.sp,
                color = Sotet,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = darab.toString(),
                fontSize = 18.sp,
                color = Sotet,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SzuroGomb(
    szoveg: String,
    kivalasztva: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
)
{
    Button(
        onClick = onClick,
        modifier = modifier.height(38.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 2.dp, vertical = 0.dp),
        shape = RoundedCornerShape(9.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if(kivalasztva) Kek else Color.White,
            contentColor = if(kivalasztva) Color.White else Sotet
        )
    ){
        Text(
            text = szoveg,
            fontSize = 12.sp,
            maxLines = 1
        )
    }

}

@Composable
fun FeladatKartya(
    feladat: Feladat,
    onAllapotValtas: () -> Unit,
    onSzerkesztes: () -> Unit,
    onTorles: () -> Unit
) {
    val prioritasiSzin = when (feladat.prioitas) {
        1 -> Zold
        2 -> Narancs
        else -> Color(244, 67, 54)
    }

    val prioritasiSzoveg = when (feladat.prioitas) {
        1 -> "Alacsony prioritás"
        2 -> "Közepes prioritás"
        else -> "Magas prioritás"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Kör alakú prioritásjelző
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(prioritasiSzin),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = feladat.prioitas.toString(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = feladat.nev,
                        color = Sotet,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = feladat.kategoria,
                        color = Szurke,
                        fontSize = 12.sp
                    )

                    Text(
                        text = prioritasiSzoveg,
                        color = prioritasiSzin,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = if (feladat.elkeszult) {
                            "Állapot: Elkészült"
                        } else {
                            "Állapot: Aktív"
                        },
                        color = if (feladat.elkeszult) Zold else Szurke,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = onSzerkesztes,
                    modifier = Modifier.size(34.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = R.drawable.ic_menu_edit
                        ),
                        contentDescription = "Feladat szerkesztése",
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = onTorles,
                    modifier = Modifier.size(34.dp)
                ) {
                    Image(
                        painter = painterResource(
                            R.drawable.ic_menu_delete
                        ),
                        contentDescription = "Feladat törlése",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onAllapotValtas,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(9.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (feladat.elkeszult) {
                        Color(0xFFE5F7ED)
                    } else {
                        Kek
                    },
                    contentColor = if (feladat.elkeszult) {
                        Color(0xFF267A43)
                    } else {
                        Color.White
                    }
                )
            ) {
                Text(
                    text = if (feladat.elkeszult) {
                        "Újra aktív"
                    } else {
                        "Elkészült"
                    },
                    fontSize = 12.sp
                )
            }
        }
    }
}
    @Composable
    fun SzerkesztesDialog(
        feladat: Feladat,
        onMegse: () -> Unit,
        onMentes: (String, String, Int) -> Unit
    ) {
        var nev by remember(feladat.id) {
            mutableStateOf(feladat.nev)
        }

        var kategoria by remember(feladat.id) {
            mutableStateOf(feladat.kategoria)
        }

        var prioritas by remember(feladat.id) {
            mutableStateOf(feladat.prioitas.toString())
        }

        var nevHiba by remember { mutableStateOf<String?>(null) }
        var kategoriaHiba by remember { mutableStateOf<String?>(null) }
        var prioritasHiba by remember { mutableStateOf<String?>(null) }

        val context = androidx.compose.ui.platform.LocalContext.current

        AlertDialog(
            onDismissRequest = onMegse,
            title = {
                Text(
                    "Feladat szerkesztése",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = nev,
                        onValueChange = {
                            nev = it
                            nevHiba = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Feladat neve") },
                        singleLine = true,
                        isError = nevHiba != null,
                        supportingText = {
                            nevHiba?.let { Text(it) }
                        }
                    )

                    OutlinedTextField(
                        value = kategoria,
                        onValueChange = {
                            kategoria = it
                            kategoriaHiba = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Kategória") },
                        singleLine = true,
                        isError = kategoriaHiba != null,
                        supportingText = {
                            kategoriaHiba?.let { Text(it) }
                        }
                    )

                    OutlinedTextField(
                        value = prioritas,
                        onValueChange = {
                            prioritas = it
                            prioritasHiba = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Prioritás (1-3)") },
                        singleLine = true,
                        isError = prioritasHiba != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        supportingText = {
                            prioritasHiba?.let { Text(it) }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        nevHiba = if (nev.isBlank()) {
                            "A feladat neve kötelező!"
                        } else null

                        kategoriaHiba = if (kategoria.isBlank()) {
                            "A kategória megadása kötelező!"
                        } else null

                        val p = prioritas.toIntOrNull()

                        prioritasHiba = when {
                            prioritas.isBlank() ->
                                "A prioritás megadása kötelező!"
                            p == null ->
                                "A prioritásnak egész számnak kell lennie!"
                            p !in 1..3 ->
                                "A prioritás csak 1 és 3 közötti érték lehet!"
                            else -> null
                        }

                        if (
                            nevHiba == null &&
                            kategoriaHiba == null &&
                            prioritasHiba == null
                        ) {
                            onMentes(
                                nev,
                                kategoria,
                                p!!
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Ellenőrizd a megadott adatokat!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Kek
                    )
                ) {
                    Text("Mentés")
                }
            },
            dismissButton = {
                TextButton(onClick = onMegse) {
                    Text("Mégse")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }



