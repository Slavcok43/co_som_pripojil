# Google Play Store Listing Draft

Use this as a starting point when filling in the Play Console "Store listing" page.
Edit freely — this is a draft, not final copy.

## App name
What Am I Connected To?

## Short description (max 80 characters)
Instantly check your IP, gateway, DNS, Wi-Fi signal and internet status.

## Full description (max 4000 characters)
What Am I Connected To? is a simple, no-frills network diagnostics tool for
Android. With one tap, it shows you everything you need to know about your
current connection:

• Local IP address
• Default gateway
• DNS servers
• Wi-Fi signal strength
• Public IP address
• Ping response time to 1.1.1.1

The app clearly flags common problems — a missing gateway, a DNS resolution
failure, or an unreachable internet connection — so you know exactly what's
wrong before you start troubleshooting.

Results can be shared as plain text with one tap, making it easy to send your
connection details to tech support or a friend helping you debug a network
issue.

No accounts, no ads, no data collection — just a fast, focused connection
check whenever you need one.

## Category
Tools

## Tags / keywords
network, wifi, ip address, dns, ping, connectivity, diagnostics

## Privacy policy
See `store/privacy-policy.md` in this repo. Host it somewhere public (e.g.
GitHub Pages, a Gist, or your own site) and paste that URL into the Play
Console "Privacy policy" field — Google requires a reachable link, not just a
file in the repo.

## Graphics checklist (not included — prepare separately)
- [ ] App icon, 512×512 px, PNG, no transparency
- [ ] Feature graphic, 1024×500 px
- [ ] At least 2 phone screenshots (recommended: 4+), min 320px, max 3840px per side
- [ ] Optional: tablet screenshots if you want tablet listing support

## Content rating
Run the Play Console content rating questionnaire. This app has no
user-generated content, violence, or mature themes, so it should qualify for
the lowest rating tier (e.g. "Everyone" / "PEGI 3").

## Data safety form
Declare:
- Data collected: none (no accounts, no analytics, no ads SDKs)
- Data shared with third parties: none
- The app does make network requests to `api.ipify.org` to detect the public
  IP, and to `1.1.1.1` for the ping check — these are standard network
  diagnostics calls, not user data collection, but mention them if the form
  asks about "device or other IDs" / network info.
