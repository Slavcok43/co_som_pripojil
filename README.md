# Čo som pripojil?

Jednoduchá natívna Android aplikácia na kontrolu pripojenia. Zobrazuje lokálnu IP adresu, predvolenú gateway, DNS servery, Wi-Fi signál, verejnú IP a odozvu na ping `1.1.1.1`. Výsledok možno zdieľať ako obyčajný text.

## Spustenie

Otvorte priečinok `co-som-pripojil` v Android Studio. Projekt potrebuje JDK 17, Android SDK Platform 35 a Android Gradle Plugin 8.7.3. Po synchronizácii projektu ho možno spustiť na zariadení s Androidom 8.0 alebo novším.

## Diagnostika

Kontrola rozlišuje chýbajúcu predvolenú gateway, zlyhanie DNS prekladu a úspešné načítanie verejnej IP. Ping používa `InetAddress.isReachable`; niektoré siete blokujú ICMP, preto „Bez odpovede“ samo osebe neznamená výpadok internetu.

Verejná IP sa zisťuje cez službu `api.ipify.org`. Aplikácia neposiela diagnostické údaje nikam inam; zdieľanie spustí až používateľ.