# Privacy Policy — What Am I Connected To?

_Last updated: 2026-10-07_

"What Am I Connected To?" ("the app") is a network diagnostics tool for
Android. This policy explains what data the app accesses and how it is used.

## Data collection

The app does **not** collect, store, or transmit any personal data. There are
no user accounts, no analytics, and no advertising SDKs.

## Network requests

To perform its diagnostic checks, the app makes the following network calls
directly from your device:

- A request to `api.ipify.org` to determine your current public IP address.
- A connectivity/ping check to `1.1.1.1` (Cloudflare's public DNS resolver)
  to verify that the internet is reachable.

These requests are made only when you open the app or tap "Refresh." No data
from these requests is sent anywhere other than directly between your device
and the service being checked. The app itself does not log, store, or share
the results outside of your device.

## Permissions used

- `INTERNET` — required to perform the connectivity checks described above.
- `ACCESS_NETWORK_STATE` — required to read basic connection info (e.g.
  gateway, DNS servers) from the Android OS.
- `ACCESS_WIFI_STATE` — required to read Wi-Fi signal strength.

None of these permissions are used to identify you personally or to track
your activity.

## Sharing results

The app lets you manually share your connection report (e.g. via a messaging
app or email) using Android's built-in share feature. This only happens when
you explicitly tap "Share result" — the app never shares data on its own.

## Changes to this policy

If this policy changes, the updated version will be published at this same
location with a new "Last updated" date.

## Contact

For questions about this policy, contact the developer via the GitHub
repository: https://github.com/Slavcok43/co_som_pripojil
