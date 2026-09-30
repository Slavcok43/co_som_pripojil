package sk.cosompripojil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.os.Bundle
import android.os.Build
import android.view.WindowInsets
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import java.net.URLConnection
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

class MainActivity : Activity() {
    private val worker = Executors.newSingleThreadExecutor()
    private val values = linkedMapOf<String, TextView>()
    private lateinit var statusTitle: TextView
    private lateinit var statusDetail: TextView
    private lateinit var refreshButton: Button
    private var lastReport = ""

    private data class NetworkSnapshot(
        val ip: String,
        val gateway: String,
        val dns: String,
        val wifi: WifiInfo?
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(242, 247, 245)
        window.navigationBarColor = Color.rgb(242, 247, 245)
        if (Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        setContentView(createScreen())
        refresh()
    }

    override fun onDestroy() {
        worker.shutdownNow()
        super.onDestroy()
    }

    private fun createScreen(): View {
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(28))
            setBackgroundColor(Color.rgb(242, 247, 245))
        }
        page.addView(label("SIEŤOVÁ KONTROLA", 12, Color.rgb(0, 112, 97), true))
        page.addView(label("Čo som pripojil?", 28, Color.rgb(25, 43, 41), true).apply {
            setPadding(0, dp(5), 0, dp(3))
        })
        page.addView(label("Prehľad pripojenia k internetu", 15, Color.rgb(91, 108, 104)))

        val status = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(17), dp(15), dp(17), dp(15))
            background = rounded(Color.WHITE, dp(12), Color.rgb(220, 231, 227))
        }
        statusTitle = label("Kontrolujem pripojenie…", 18, Color.rgb(25, 43, 41), true)
        statusDetail = label("Chvíľku strpenia.", 14, Color.rgb(91, 108, 104)).apply {
            setPadding(0, dp(5), 0, 0)
        }
        status.addView(statusTitle)
        status.addView(statusDetail)
        page.addView(status, columnParams(top = 20))

        page.addView(label("ÚDAJE O PRIPOJENÍ", 12, Color.rgb(91, 108, 104), true).apply {
            setPadding(0, dp(23), 0, dp(9))
        })
        val info = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(15), dp(2), dp(15), dp(2))
            background = rounded(Color.WHITE, dp(12), Color.rgb(220, 231, 227))
        }
        listOf(
            "IP adresa" to "Zisťujem…",
            "Gateway" to "Zisťujem…",
            "DNS" to "Zisťujem…",
            "Wi-Fi signál" to "Zisťujem…",
            "Verejná IP" to "Zisťujem…",
            "Ping · 1.1.1.1" to "Zisťujem…"
        ).forEachIndexed { index, (name, initial) ->
            val row = createInfoRow(name, initial)
            info.addView(row, ViewGroup.LayoutParams.MATCH_PARENT, dp(59))
            if (index < 5) {
                val divider = View(this).apply { setBackgroundColor(Color.rgb(232, 238, 236)) }
                info.addView(divider, ViewGroup.LayoutParams.MATCH_PARENT, dp(1))
            }
        }
        page.addView(info)

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        refreshButton = actionButton("Obnoviť", Color.rgb(0, 112, 97), Color.WHITE)
        refreshButton.setOnClickListener { refresh() }
        val exportButton = actionButton("Zdieľať výsledok", Color.WHITE, Color.rgb(25, 71, 64)).apply {
            background = rounded(Color.WHITE, dp(10), Color.rgb(195, 213, 207))
            setOnClickListener { shareReport() }
        }
        actions.addView(refreshButton, LinearLayout.LayoutParams(0, dp(50), 1f))
        actions.addView(View(this), LinearLayout.LayoutParams(dp(10), 1))
        actions.addView(exportButton, LinearLayout.LayoutParams(0, dp(50), 1f))
        page.addView(actions, columnParams(top = 16))

        return ScrollView(this).apply {
            isFillViewport = true
            setBackgroundColor(Color.rgb(242, 247, 245))
            addView(page)
            if (Build.VERSION.SDK_INT >= 30) {
                setOnApplyWindowInsetsListener { view, insets ->
                    val bars = insets.getInsets(WindowInsets.Type.systemBars())
                    view.setPadding(0, bars.top, 0, bars.bottom)
                    insets
                }
            }
        }
    }

    private fun createInfoRow(name: String, initial: String): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        row.addView(label(name, 14, Color.rgb(91, 108, 104)), LinearLayout.LayoutParams(0, -2, 1f))
        val value = label(initial, 14, Color.rgb(25, 43, 41), true).apply {
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            maxWidth = dp(205)
        }
        values[name] = value
        row.addView(value)
        return row
    }

    private fun refresh() {
        refreshButton.isEnabled = false
        refreshButton.text = "Kontrolujem…"
        statusTitle.text = "Kontrolujem pripojenie…"
        statusDetail.text = "Overujem DNS a dostupnosť internetu."
        val snapshot = readSnapshot()
        setValue("IP adresa", snapshot.ip)
        setValue("Gateway", snapshot.gateway)
        setValue("DNS", snapshot.dns)
        setValue("Wi-Fi signál", wifiSignal(snapshot.wifi))
        setValue("Verejná IP", "Zisťujem…")
        setValue("Ping · 1.1.1.1", "Zisťujem…")

        worker.execute {
            val dnsOk = runCatching { InetAddress.getByName("api.ipify.org") }.isSuccess
            val publicIp = fetchPublicIp() ?: "Nezistená"
            val reachable = runCatching {
                var responded = false
                val elapsed = measureTimeMillis {
                    responded = InetAddress.getByName("1.1.1.1").isReachable(1800)
                }
                if (responded) "$elapsed ms" else "Bez odpovede"
            }.getOrDefault("Nedostupný")
            val pingResult = reachable
            val internetOk = runCatching {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress("1.1.1.1", 443), 2500)
                }
                true
            }.getOrDefault(false)

            val (title, detail, color) = when {
                snapshot.gateway == "Nezistená" -> Triple(
                    "Problém s gateway", "Zariadenie nenašlo predvolenú bránu.", Color.rgb(166, 73, 26)
                )
                !dnsOk -> Triple(
                    "Problém s DNS", "Názov internetovej služby sa nepodarilo preložiť.", Color.rgb(166, 73, 26)
                )
                !internetOk -> Triple(
                    "Problém s gateway", "DNS funguje, ale internetová brána neodpovedá.", Color.rgb(166, 73, 26)
                )
                else -> Triple(
                    "Internet OK", "Pripojenie aj DNS odpovedajú.", Color.rgb(0, 112, 97)
                )
            }
            val report = buildReport(snapshot, publicIp, pingResult, title, detail)
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread
                setValue("Verejná IP", publicIp)
                setValue("Ping · 1.1.1.1", pingResult)
                statusTitle.text = title
                statusTitle.setTextColor(color)
                statusDetail.text = detail
                lastReport = report
                refreshButton.isEnabled = true
                refreshButton.text = "Obnoviť"
            }
        }
    }

    private fun readSnapshot(): NetworkSnapshot {
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = manager.activeNetwork
        val properties = network?.let(manager::getLinkProperties)
        val capabilities = network?.let(manager::getNetworkCapabilities)
        val ip = properties?.linkAddresses
            ?.map { it.address }
            ?.firstOrNull { it is Inet4Address && !it.isLoopbackAddress }
            ?.hostAddress
            ?: properties?.linkAddresses?.firstOrNull()?.address?.hostAddress
            ?: "Nezistená"
        val gateway = properties?.routes
            ?.firstOrNull { it.isDefaultRoute }
            ?.gateway?.hostAddress
            ?: "Nezistená"
        val dns = properties?.dnsServers
            ?.mapNotNull { it.hostAddress }
            ?.joinToString(", ")
            ?.takeIf { it.isNotBlank() }
            ?: "Nezistené"
        val wifi = if (Build.VERSION.SDK_INT >= 29 &&
            capabilities?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) == true
        ) {
            capabilities.transportInfo as? WifiInfo
        } else {
            null
        }
        return NetworkSnapshot(ip, gateway, dns, wifi)
    }

    private fun wifiSignal(info: WifiInfo?): String {
        if (info == null) return "Nie ste na Wi-Fi"
        val rssi = info.rssi
        if (rssi <= -100 || rssi == 0) return "Nezistený"
        val level = android.net.wifi.WifiManager.calculateSignalLevel(rssi, 5)
        val quality = when (level) {
            4 -> "Výborný"
            3 -> "Dobrý"
            2 -> "Slabší"
            else -> "Slabý"
        }
        return "$quality · $rssi dBm"
    }

    private fun fetchPublicIp(): String? = runCatching {
        val connection: URLConnection = URL("https://api.ipify.org").openConnection()
        connection.connectTimeout = 3500
        connection.readTimeout = 3500
        connection.getInputStream().bufferedReader().use { it.readText().trim() }
    }.getOrNull()?.takeIf { it.isNotBlank() }

    private fun buildReport(
        snapshot: NetworkSnapshot,
        publicIp: String,
        ping: String,
        result: String,
        detail: String
    ): String = listOf(
        "Čo som pripojil? – výsledok kontroly",
        "Stav: $result",
        "Detail: $detail",
        "IP adresa: ${snapshot.ip}",
        "Gateway: ${snapshot.gateway}",
        "DNS: ${snapshot.dns}",
        "Wi-Fi signál: ${wifiSignal(snapshot.wifi)}",
        "Verejná IP: $publicIp",
        "Ping 1.1.1.1: $ping"
    ).joinToString("\n")

    private fun shareReport() {
        val text = lastReport.ifBlank { "Kontrola pripojenia ešte prebieha." }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Zdieľať výsledok"))
    }

    private fun setValue(key: String, value: String) {
        values[key]?.text = value
    }

    private fun label(text: String, size: Int, color: Int, bold: Boolean = false) = TextView(this).apply {
        this.text = text
        textSize = size.toFloat()
        setTextColor(color)
        if (bold) typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }

    private fun actionButton(text: String, backgroundColor: Int, textColor: Int) = Button(this).apply {
        this.text = text
        textSize = 14f
        isAllCaps = false
        setTextColor(textColor)
        background = rounded(backgroundColor, dp(10), backgroundColor)
        minimumHeight = dp(50)
        stateListAnimator = null
    }

    private fun rounded(fill: Int, radius: Int, stroke: Int) = GradientDrawable().apply {
        setColor(fill)
        cornerRadius = radius.toFloat()
        setStroke(dp(1), stroke)
    }

    private fun columnParams(top: Int) = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply { topMargin = dp(top) }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}