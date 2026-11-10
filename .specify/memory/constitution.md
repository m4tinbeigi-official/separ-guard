# Separ Guard Constitution
<!-- Mobile Security, Anti-Malware & Threat Eradication Suite -->

## Core Principles

### I. Universal Accessibility & Visual-First UX
Every security feature, alert, and flow must be operable without requiring reading ability or technical knowledge. All screens must strictly rely on three universal states: Safe (#00E676 Green), Danger (#FF1744 Crimson), and Attention (#FFC107 Amber). Every critical decision must have an offline, clear Persian voice prompt alternative and a single-tap action.

### II. Zero-Tolerance Threat Eradication & Strict Velora Policy
Any application package containing the substring "velora" (including but not limited to com.velora.cloud) must be automatically flagged as high-severity malware and presented for immediate uninstallation. The engine must actively maintain signatures and package identifiers for all known Iranian banking trojans, SMS worms, fake government apps (Sana, Eblaghieh, Saham Edalat), and unauthorized remote administration tools (RATs).

### III. 100% Offline-First Architecture & Absolute Privacy
The application must execute all scanning, analysis, heuristics, and voice guidance entirely on-device without requiring internet connectivity or uploading telemetry. No user SMS, contact list, device identifier, or personal data may ever leave the device.

### IV. Post-Infection Session Remediation
Malware uninstallation is insufficient on its own. The system must provide direct, visual, three-step deep-link workflows for domestic (Rubika, Eitaa, Bale, Soroush+) and international (Telegram, Instagram, WhatsApp) messaging platforms to terminate all active sessions and eject adversaries.

### V. Persistent 24/7 Autonomous Shielding
Continuous background protection is enforced via three decoupled shields:
1. SMS Guard: Real-time pattern and phishing link detection on incoming SMS.
2. Web & Gateway Guard: Accessibility-based address bar monitoring against spoofed Shaparak portals.
3. Install Guard: Zero-second package install and replacement verification.

### VI. Commercial Proprietary Governance & Sole Ownership
Separ Guard is a strictly commercial, closed-source security asset developed and owned exclusively by Rick Sanchez. The software is not open-source, and all source code, algorithms, visual layouts, and threat databases are fully proprietary. No third-party developer names or trademarked brands may be used.

### VII. Anti-Slop, VibeFarsi & Pure Native UI Standards
1. Strict RTL Layout: Rely strictly on logical direction properties (layoutDirection, start, end, ms, me, ps, pe) with zero hardcoded left/right alignments.
2. Typography & Zero Em-Dash: Mandatory Vazirmatn font family with letter-spacing strictly zero. Absolute prohibition of em-dashes (—) across all strings, layouts, logs, and comments.
3. Persian Numerals & Currency: Enforce Persian digits (۰-۹) across all visual counters, dates, and amounts, with currency strictly denoted in Toman (تومان) placed after the value.
4. Anti-Slop Aesthetic: OLED dark theme (#080B10, #0F141C), neon cyan (#00E5FF), phosphor green (#00E676), danger crimson (#FF1744). Zero cheesy emojis, zero boilerplate placeholding, and razor-sharp functional microcopy.

### VIII. Airplane Mode Persistence & Emergency Card Freeze
When an account compromise, SMS worm, or banking trojan is detected:
1. Airplane Mode Lockdown: The infected phone must strictly remain in Airplane Mode. Attackers control remote C2 links and will intercept newly issued SMS OTPs over the internet if the device goes online.
2. Cross-Device Remediation: Account and card freezing must be performed via Telephone Banking IVR from another telephone or landline.
3. Universal Bank Hotlines: Built-in offline directory with direct phone numbers and IVR keypad sequences for all 16 major Iranian banks.

### IX. Physical Gold & Marketplace Cashing-Out Defense
Attackers routinely exploit intercepted OTPs or stored card credentials on e-commerce platforms (Digikala, DigiPay, SnappPay, Milli Gold) to buy physical gold bars, coins, and vouchers:
1. Real-time interception of marketplace order confirmations and OTP messages.
2. Instant full-screen confirmation prompt: "Did you place this order yourself?"
3. Immediate 1-tap cancellation routing and direct 24/7 hotline dispatch (e.g. Digikala 021-61930000) if the user did not authorize the purchase.

## Architecture and Quality Standards

### Stack & Platform Requirements
- Language: Modern Kotlin on Android SDK (minSdk 21, targetSdk 35/36).
- UI Framework: Jetpack Compose with Material 3 in high-contrast Dark Mode with strict VibeFarsi RTL layout standards.
- Local Storage: Embedded SQLite/Room database with indexed SHA-256 and package lookup tables.
- Audio Subsystem: Jetpack Media3 / Android MediaPlayer streaming compressed offline OGG/MP3 assets.
- Independence: Rebranded package ir.ricksanchez.separ with zero usage of third-party trademarked names or logos.

## Development Workflow and Quality Gates

- Every threat detection rule must include a verifiable test case.
- False positive prevention: Official system applications and certified domestic banking apps must never be falsely flagged.
- Clean diffs: Minimal code changes, YAGNI compliance, and zero superfluous dependencies.

## Governance

This Constitution supersedes all ad-hoc conventions. Amendments must be versioned semantically. All components and code reviews must verify adherence to accessibility, offline privacy, and proactive eradication principles.

**Version**: 1.0.0 | **Ratified**: 2026-11-04 | **Last Amended**: 2026-11-04
