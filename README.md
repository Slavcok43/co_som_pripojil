# What Am I Connected To?

A simple native Android app for checking your network connection. It shows the local IP address, default gateway, DNS servers, Wi-Fi signal, public IP, and ping response to `1.1.1.1`. The result can be shared as plain text.

## Running the app

Open the `co-som-pripojil` folder in Android Studio. The project requires JDK 17, Android SDK Platform 35, and Android Gradle Plugin 8.7.3. After the project syncs, it can run on a device with Android 8.0 or newer.

## Diagnostics

The check distinguishes between a missing default gateway, a DNS resolution failure, and a successful public IP lookup. Ping uses `InetAddress.isReachable`; some networks block ICMP, so "No response" alone doesn't necessarily mean the internet is down.

The public IP is retrieved via the `api.ipify.org` service. The app doesn't send diagnostic data anywhere else; sharing is only triggered by the user.